import { useEffect, useState } from "react";
import { apiRequest } from "../../../services/api";
import dashboardService, { StudentSummaryDTO } from "../../../api/dashboardService";
import messageService, { MessageDTO } from "../../../api/messageService";

export function EncadrantMessagerie() {
  const [etudiants, setEtudiants] = useState<StudentSummaryDTO[]>([]);
  const [selection, setSelection] = useState<StudentSummaryDTO | null>(null);
  const [messages, setMessages] = useState<MessageDTO[]>([]);
  const [contenu, setContenu] = useState("");
  const [monId, setMonId] = useState<number | null>(null);

  useEffect(() => {
    apiRequest<{ id: number }>("/utilisateurs/moi").then((u) => setMonId(u.id));
    dashboardService
      .getSupervisorDashboard()
      .then((d) => setEtudiants(d.students))
      .catch(console.error);
  }, []);

  const chargerConversation = (etudiant: StudentSummaryDTO) => {
    setSelection(etudiant);
    messageService
      .getConversation(etudiant.pfeId, etudiant.studentId)
      .then((page) => setMessages(page.content.reverse()))
      .catch(console.error);
  };

  // Réception des nouveaux messages (rafraîchissement toutes les 5 s)
  useEffect(() => {
    if (!selection) return;
    const timer = setInterval(() => {
      messageService
        .getConversation(selection.pfeId, selection.studentId)
        .then((page) => setMessages([...page.content].reverse()))
        .catch(() => {});
    }, 5000);
    return () => clearInterval(timer);
  }, [selection]);

  const envoyerMessage = async () => {
    if (!contenu.trim() || !selection) return;

    await messageService.sendMessage({
      pfeId: selection.pfeId,
      receiverId: selection.studentId,
      content: contenu,
    });

    setContenu("");
    chargerConversation(selection);
  };

  return (
    <div className="grid grid-cols-3 gap-6 h-[calc(100vh-160px)]">
      <div className="bg-white rounded-lg border border-gray-200 overflow-y-auto">
        <h3 className="text-lg font-semibold p-4 border-b border-gray-200">Mes étudiants</h3>
        {etudiants.length === 0 && (
          <p className="text-gray-500 text-sm p-4">Aucun étudiant affecté.</p>
        )}
        {etudiants.map((e) => (
          <button
            key={e.studentId}
            onClick={() => chargerConversation(e)}
            className={`w-full text-left p-4 border-b border-gray-100 hover:bg-gray-50 ${
              selection?.studentId === e.studentId ? "bg-blue-50" : ""
            }`}
          >
            <p className="font-medium text-gray-800">{e.studentName}</p>
            <p className="text-xs text-gray-500">{e.pfeTitle}</p>
          </button>
        ))}
      </div>

      <div className="col-span-2 bg-white rounded-lg border border-gray-200 flex flex-col">
        {!selection ? (
          <div className="flex-1 flex items-center justify-center text-gray-500">
            Sélectionnez un étudiant pour afficher la conversation
          </div>
        ) : (
          <>
            <h3 className="text-lg font-semibold p-4 border-b border-gray-200">
              {selection.studentName} — {selection.pfeTitle}
            </h3>
            <div className="flex-1 overflow-y-auto p-4 space-y-3">
              {messages.map((m) => (
                <div
                  key={m.id}
                  className={`max-w-[70%] p-3 rounded-lg ${
                    m.senderId === monId
                      ? "ml-auto bg-[#1F4E79] text-white"
                      : "bg-gray-100 text-gray-800"
                  }`}
                >
                  <p className="text-sm">{m.content}</p>
                  <p className={`text-xs mt-1 ${m.senderId === monId ? "text-blue-100" : "text-gray-500"}`}>
                    {new Date(m.createdAt).toLocaleString("fr-FR")}
                  </p>
                </div>
              ))}
            </div>
            <div className="flex gap-2 p-4 border-t border-gray-200">
              <input
                value={contenu}
                onChange={(e) => setContenu(e.target.value)}
                onKeyDown={(e) => e.key === "Enter" && envoyerMessage()}
                className="flex-1 border border-gray-300 rounded-lg px-4 py-2"
                placeholder="Écrire un message..."
              />
              <button
                onClick={envoyerMessage}
                className="px-4 py-2 bg-[#1F4E79] text-white rounded-lg hover:bg-[#163A5C]"
              >
                Envoyer
              </button>
            </div>
          </>
        )}
      </div>
    </div>
  );
}
