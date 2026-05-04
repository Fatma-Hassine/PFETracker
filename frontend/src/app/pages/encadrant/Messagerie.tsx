import { useEffect, useState } from "react";
import { apiRequest } from "../../../services/api";

type Message = {
  id: number;
  contenu: string;
  expediteurNom?: string;
  dateEnvoi?: string;
};

export function EncadrantMessagerie() {
  const [messages, setMessages] = useState<Message[]>([]);
  const [contenu, setContenu] = useState("");

  useEffect(() => {
    chargerMessages();
  }, []);

  const chargerMessages = () => {
    apiRequest<Message[]>("/encadrant/messages")
      .then(setMessages)
      .catch(console.error);
  };

  const envoyerMessage = async () => {
    if (!contenu.trim()) return;

    await apiRequest<void>("/encadrant/messages", {
      method: "POST",
      body: JSON.stringify({ contenu }),
    });

    setContenu("");
    chargerMessages();
  };

  return (
    <div className="space-y-6">
      <h2 className="text-2xl font-bold">Messagerie</h2>

      <div className="bg-white p-6 rounded-lg border border-gray-200 space-y-3">
        {messages.map((message) => (
          <div key={message.id} className="p-3 bg-gray-50 rounded-lg">
            <p className="font-semibold">{message.expediteurNom}</p>
            <p>{message.contenu}</p>
            <p className="text-xs text-gray-500">{message.dateEnvoi}</p>
          </div>
        ))}

        <div className="flex gap-2 pt-4">
          <input
            value={contenu}
            onChange={(e) => setContenu(e.target.value)}
            className="flex-1 border border-gray-300 rounded-lg px-4 py-2"
            placeholder="Écrire un message..."
          />
          <button
            onClick={envoyerMessage}
            className="px-4 py-2 bg-[#1F4E79] text-white rounded-lg"
          >
            Envoyer
          </button>
        </div>
      </div>
    </div>
  );
}