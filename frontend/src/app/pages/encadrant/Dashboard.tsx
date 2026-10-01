import { useEffect, useState } from "react";
import { Users, ClipboardCheck, Calendar, AlertTriangle } from "lucide-react";
import dashboardService, { DashboardSupervisorDTO } from "../../../api/dashboardService";

export function EncadrantDashboard() {
  const [dashboard, setDashboard] = useState<DashboardSupervisorDTO | null>(null);
  const [erreur, setErreur] = useState(false);

  useEffect(() => {
    dashboardService
      .getSupervisorDashboard()
      .then(setDashboard)
      .catch(() => setErreur(true));
  }, []);

  if (erreur) {
    return <p className="text-red-600">Impossible de charger le tableau de bord.</p>;
  }

  if (!dashboard) {
    return <p className="text-gray-600">Chargement...</p>;
  }

  return (
    <div className="space-y-6">
      <h2 className="text-2xl font-bold">Tableau de bord encadrant</h2>

      <div className="grid grid-cols-4 gap-6">
        <div className="bg-white rounded-lg p-6 border border-gray-200">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-gray-600">Étudiants encadrés</h3>
            <Users className="text-[#1F4E79]" size={24} />
          </div>
          <p className="text-3xl font-bold text-gray-800">{dashboard.totalStudents}</p>
        </div>

        <div className="bg-white rounded-lg p-6 border border-gray-200">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-gray-600">PFE actifs</h3>
            <ClipboardCheck className="text-[#1D9E75]" size={24} />
          </div>
          <p className="text-3xl font-bold text-gray-800">{dashboard.activePFEs}</p>
        </div>

        <div className="bg-white rounded-lg p-6 border border-gray-200">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-gray-600">Tâches à valider</h3>
            <ClipboardCheck className="text-amber-500" size={24} />
          </div>
          <p className="text-3xl font-bold text-gray-800">{dashboard.pendingValidations.length}</p>
        </div>

        <div className="bg-white rounded-lg p-6 border border-gray-200">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-gray-600">Progression moyenne</h3>
            <Calendar className="text-[#1F4E79]" size={24} />
          </div>
          <p className="text-3xl font-bold text-gray-800">{Math.round(dashboard.averageProgress)}%</p>
        </div>
      </div>

      {dashboard.priorityAlerts.length > 0 && (
        <div className="bg-red-50 rounded-lg p-6 border border-red-200">
          <h3 className="text-lg font-semibold mb-4 flex items-center gap-2 text-red-700">
            <AlertTriangle size={20} /> Alertes prioritaires
          </h3>
          <div className="space-y-2">
            {dashboard.priorityAlerts.map((a, i) => (
              <p key={i} className="text-sm text-red-700">{a.message}</p>
            ))}
          </div>
        </div>
      )}

      <div className="bg-white rounded-lg p-6 border border-gray-200">
        <h3 className="text-lg font-semibold mb-4">Mes étudiants</h3>
        <div className="space-y-3">
          {dashboard.students.length === 0 && (
            <p className="text-gray-500 text-sm">Aucun étudiant affecté pour le moment.</p>
          )}
          {dashboard.students.map((s) => (
            <div
              key={s.studentId}
              className={`p-4 rounded-lg border flex items-center justify-between ${
                s.isInactive ? "bg-red-50 border-red-200" : "bg-gray-50 border-gray-200"
              }`}
            >
              <div>
                <p className="font-medium text-gray-800">{s.studentName}</p>
                <p className="text-sm text-gray-500">{s.pfeTitle}</p>
              </div>
              <div className="flex items-center gap-6 text-sm">
                <span className="text-gray-600">{Math.round(s.pfeProgress)}% complété</span>
                {s.overdueTasks > 0 && (
                  <span className="px-2 py-0.5 bg-red-500 text-white rounded-full text-xs">
                    {s.overdueTasks} en retard
                  </span>
                )}
                {s.isInactive && (
                  <span className="px-2 py-0.5 bg-gray-500 text-white rounded-full text-xs">Inactif</span>
                )}
              </div>
            </div>
          ))}
        </div>
      </div>

      <div className="grid grid-cols-2 gap-6">
        <div className="bg-white rounded-lg p-6 border border-gray-200">
          <h3 className="text-lg font-semibold mb-4">File d'attente de validation</h3>
          <div className="space-y-2">
            {dashboard.pendingValidations.length === 0 && (
              <p className="text-gray-500 text-sm">Aucune tâche en attente.</p>
            )}
            {dashboard.pendingValidations.map((t) => (
              <div key={t.id} className="p-3 bg-gray-50 rounded-lg">
                <p className="text-sm font-medium text-gray-800">{t.title}</p>
                <p className="text-xs text-gray-500">{t.studentName}</p>
              </div>
            ))}
          </div>
        </div>

        <div className="bg-white rounded-lg p-6 border border-gray-200">
          <h3 className="text-lg font-semibold mb-4">Réunions à venir</h3>
          <div className="space-y-2">
            {dashboard.upcomingMeetings.length === 0 && (
              <p className="text-gray-500 text-sm">Aucune réunion planifiée.</p>
            )}
            {dashboard.upcomingMeetings.map((m) => (
              <div key={m.id} className="p-3 bg-gray-50 rounded-lg">
                <p className="text-sm font-medium text-gray-800">{m.title}</p>
                <p className="text-xs text-gray-500">
                  {new Date(m.meetingDate).toLocaleString("fr-FR")} — {m.participantName}
                </p>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}
