import { useEffect, useRef, useState } from 'react';
import { Send, Paperclip } from 'lucide-react';
import { toast } from 'sonner';
import { module2Api } from '../../api/module2Api';
import { Pfe } from '../../types/module2.types';
import { apiRequest } from '../../../services/api';
import messageService, { MessageDTO } from '../../../api/messageService';

export function EtudiantMessagerie() {
  const [pfe, setPfe] = useState<Pfe | null>(null);
  const [messages, setMessages] = useState<MessageDTO[]>([]);
  const [newMessage, setNewMessage] = useState('');
  const [monId, setMonId] = useState<number | null>(null);
  const [chargement, setChargement] = useState(true);
  const finRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    apiRequest<{ id: number }>('/utilisateurs/moi').then((u) => setMonId(u.id));
    module2Api
      .getMyPfes()
      .then(async (pfes) => {
        if (pfes.length === 0 || !pfes[0].supervisorId) {
          setChargement(false);
          return;
        }
        setPfe(pfes[0]);
        const page = await messageService.getConversation(pfes[0].id, pfes[0].supervisorId!);
        setMessages(page.content.reverse());
      })
      .catch(() => toast.error('Impossible de charger la conversation'))
      .finally(() => setChargement(false));
  }, []);

  useEffect(() => {
    finRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  // Réception des nouveaux messages (rafraîchissement toutes les 5 s)
  useEffect(() => {
    if (!pfe?.supervisorId) return;
    const timer = setInterval(() => {
      messageService
        .getConversation(pfe.id, pfe.supervisorId!)
        .then((page) => setMessages([...page.content].reverse()))
        .catch(() => {});
    }, 5000);
    return () => clearInterval(timer);
  }, [pfe]);

  const handleSend = async () => {
    if (!newMessage.trim() || !pfe?.supervisorId) return;

    try {
      await messageService.sendMessage({
        pfeId: pfe.id,
        receiverId: pfe.supervisorId,
        content: newMessage,
      });
      setNewMessage('');
      const page = await messageService.getConversation(pfe.id, pfe.supervisorId);
      setMessages(page.content.reverse());
    } catch {
      toast.error("Échec de l'envoi du message");
    }
  };

  const handleAttachment = () => {
    toast.info("L'envoi de pièces jointes se fait via le champ dédié lors de l'ajout d'un livrable");
  };

  if (chargement) {
    return <p className="text-gray-600">Chargement...</p>;
  }

  if (!pfe || !pfe.supervisorId) {
    return (
      <div className="bg-white rounded-lg p-6 border border-gray-200 text-center text-gray-600">
        Aucun encadrant affecté pour le moment — la messagerie sera disponible une fois ton PFE affecté.
      </div>
    );
  }

  const initiales = (pfe.supervisorName || '?')
    .split(' ')
    .map((m) => m[0])
    .join('')
    .slice(0, 2)
    .toUpperCase();

  return (
    <div className="grid grid-cols-4 gap-6 h-[calc(100vh-12rem)]">
      <div className="col-span-1 bg-white rounded-lg border border-gray-200 overflow-y-auto">
        <div className="p-4 border-b border-gray-200">
          <h3 className="font-semibold">Conversations</h3>
        </div>
        <div className="p-2">
          <div className="p-3 bg-[#1F4E79] text-white rounded-lg cursor-pointer">
            <div className="flex items-center gap-3 mb-1">
              <div className="w-10 h-10 rounded-full bg-white text-[#1F4E79] flex items-center justify-center font-medium">
                {initiales}
              </div>
              <div className="flex-1">
                <p className="font-medium">{pfe.supervisorName}</p>
                <p className="text-sm opacity-90 truncate">{pfe.title}</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div className="col-span-3 bg-white rounded-lg border border-gray-200 flex flex-col">
        <div className="p-4 border-b border-gray-200 flex items-center gap-3">
          <div className="w-10 h-10 rounded-full bg-[#1F4E79] flex items-center justify-center text-white">
            {initiales}
          </div>
          <div className="flex-1">
            <h3 className="font-semibold">{pfe.supervisorName}</h3>
          </div>
        </div>

        <div className="flex-1 overflow-y-auto p-4 space-y-4">
          {messages.length === 0 && (
            <p className="text-center text-sm text-gray-500">Aucun message pour le moment — dis bonjour !</p>
          )}
          {messages.map((message) => (
            <div
              key={message.id}
              className={`flex ${message.senderId === monId ? 'justify-end' : 'justify-start'}`}
            >
              <div
                className={`max-w-md rounded-lg p-3 ${
                  message.senderId === monId ? 'bg-[#1F4E79] text-white' : 'bg-gray-100 text-gray-800'
                }`}
              >
                <p>{message.content}</p>
                <p
                  className={`text-xs mt-1 ${message.senderId === monId ? 'text-blue-100' : 'text-gray-500'}`}
                >
                  {new Date(message.createdAt).toLocaleTimeString('fr-FR', {
                    hour: '2-digit',
                    minute: '2-digit',
                  })}
                </p>
              </div>
            </div>
          ))}
          <div ref={finRef} />
        </div>

        <div className="p-4 border-t border-gray-200">
          <div className="flex items-center gap-2">
            <button
              onClick={handleAttachment}
              className="p-2 text-gray-600 hover:bg-gray-100 rounded-lg"
            >
              <Paperclip size={20} />
            </button>
            <input
              type="text"
              value={newMessage}
              onChange={(e) => setNewMessage(e.target.value)}
              onKeyDown={(e) => e.key === 'Enter' && handleSend()}
              placeholder="Tapez votre message..."
              className="flex-1 px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#1F4E79]"
            />
            <button
              onClick={handleSend}
              className="p-2 bg-[#1F4E79] text-white rounded-lg hover:bg-[#163A5C]"
            >
              <Send size={20} />
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
