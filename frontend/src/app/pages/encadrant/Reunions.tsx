import { useEffect, useState } from "react";
import { Calendar, MapPin, Video } from "lucide-react";
import { toast } from "sonner";
import meetingService, { MeetingDTO } from "../../../api/meetingService";
import dashboardService, { StudentSummaryDTO } from "../../../api/dashboardService";

export function EncadrantReunions() {
  const [reunions, setReunions] = useState<MeetingDTO[]>([]);
  const [etudiants, setEtudiants] = useState<StudentSummaryDTO[]>([]);
  const [formulaireOuvert, setFormulaireOuvert] = useState(false);
  const [form, setForm] = useState({
    etudiantId: "",
    titre: "",
    date: "",
    duree: 30,
    ordreDuJour: "",
  });

  const charger = () => {
    meetingService.getUpcomingMeetings().then(setReunions).catch(console.error);
  };

  useEffect(() => {
    charger();
    dashboardService.getSupervisorDashboard().then((d) => setEtudiants(d.students));
  }, []);

  const creerReunion = async () => {
    const etudiant = etudiants.find((e) => e.studentId === Number(form.etudiantId));
    if (!etudiant || !form.titre || !form.date) {
      toast.error("Merci de remplir tous les champs obligatoires");
      return;
    }

    try {
      await meetingService.createMeeting({
        pfeId: etudiant.pfeId,
        participantId: etudiant.studentId,
        title: form.titre,
        description: form.ordreDuJour,
        meetingDate: new Date(form.date).toISOString(),
        duration: form.duree,
      });
      toast.success("Réunion planifiée");
      setFormulaireOuvert(false);
      setForm({ etudiantId: "", titre: "", date: "", duree: 30, ordreDuJour: "" });
      charger();
    } catch {
      toast.error("Échec de la planification de la réunion");
    }
  };

  const statusLabel: Record<string, string> = {
    PENDING: "En attente",
    ACCEPTED: "Confirmée",
    REFUSED: "Refusée",
    CANCELLED: "Annulée",
    COMPLETED: "Terminée",
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h2 className="text-2xl font-bold">Réunions</h2>
        <button
          onClick={() => setFormulaireOuvert(!formulaireOuvert)}
          className="px-4 py-2 bg-[#1F4E79] text-white rounded-lg hover:bg-[#163A5C]"
        >
          {formulaireOuvert ? "Annuler" : "Planifier une réunion"}
        </button>
      </div>

      {formulaireOuvert && (
        <div className="bg-white p-6 rounded-lg border border-gray-200 space-y-4">
          <select
            value={form.etudiantId}
            onChange={(e) => setForm({ ...form, etudiantId: e.target.value })}
            className="w-full border border-gray-300 rounded-lg px-3 py-2"
          >
            <option value="">Choisir un étudiant</option>
            {etudiants.map((e) => (
              <option key={e.studentId} value={e.studentId}>
                {e.studentName}
              </option>
            ))}
          </select>
          <input
            placeholder="Titre"
            value={form.titre}
            onChange={(e) => setForm({ ...form, titre: e.target.value })}
            className="w-full border border-gray-300 rounded-lg px-3 py-2"
          />
          <div className="flex gap-4">
            <input
              type="datetime-local"
              value={form.date}
              onChange={(e) => setForm({ ...form, date: e.target.value })}
              className="flex-1 border border-gray-300 rounded-lg px-3 py-2"
            />
            <input
              type="number"
              min={15}
              step={15}
              value={form.duree}
              onChange={(e) => setForm({ ...form, duree: Number(e.target.value) })}
              className="w-32 border border-gray-300 rounded-lg px-3 py-2"
              placeholder="Durée (min)"
            />
          </div>
          <textarea
            placeholder="Ordre du jour"
            value={form.ordreDuJour}
            onChange={(e) => setForm({ ...form, ordreDuJour: e.target.value })}
            className="w-full border border-gray-300 rounded-lg px-3 py-2"
            rows={3}
          />
          <button
            onClick={creerReunion}
            className="px-4 py-2 bg-[#1D9E75] text-white rounded-lg hover:bg-[#17805F]"
          >
            Confirmer
          </button>
        </div>
      )}

      <div className="space-y-3">
        {reunions.length === 0 && (
          <p className="text-gray-500 text-sm">Aucune réunion à venir.</p>
        )}
        {reunions.map((reunion) => (
          <div key={reunion.id} className="bg-white p-4 rounded-lg border border-gray-200">
            <div className="flex items-start justify-between">
              <div>
                <p className="font-semibold text-gray-800">{reunion.title}</p>
                <p className="text-sm text-gray-600 flex items-center gap-1 mt-1">
                  <Calendar size={14} /> {new Date(reunion.meetingDate).toLocaleString("fr-FR")}
                </p>
                <p className="text-sm text-gray-500 mt-1">Avec {reunion.participantName}</p>
                {reunion.description && (
                  <p className="text-sm text-gray-500 mt-1">{reunion.description}</p>
                )}
              </div>
              <span className="text-xs px-2 py-1 bg-gray-100 rounded-full text-gray-700">
                {statusLabel[reunion.status] || reunion.status}
              </span>
            </div>
            {reunion.meetingLink && (
              <a
                href={reunion.meetingLink}
                target="_blank"
                rel="noreferrer"
                className="inline-flex items-center gap-1 mt-3 text-sm text-[#1F4E79] hover:underline"
              >
                <Video size={14} /> Rejoindre la réunion
              </a>
            )}
            {reunion.location && (
              <p className="text-sm text-gray-500 flex items-center gap-1 mt-2">
                <MapPin size={14} /> {reunion.location}
              </p>
            )}
          </div>
        ))}
      </div>
    </div>
  );
}
