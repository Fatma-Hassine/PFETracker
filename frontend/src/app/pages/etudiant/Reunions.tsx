import { useEffect, useState } from 'react';
import { Calendar, Clock, Video } from 'lucide-react';
import { toast } from 'sonner';
import { module2Api } from '../../api/module2Api';
import { Pfe } from '../../types/module2.types';
import meetingService, { MeetingDTO } from '../../../api/meetingService';

export function EtudiantReunions() {
  const [pfe, setPfe] = useState<Pfe | null>(null);
  const [meetings, setMeetings] = useState<MeetingDTO[]>([]);
  const [chargement, setChargement] = useState(true);
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [newMeeting, setNewMeeting] = useState({
    title: '',
    date: '',
    time: '',
    duration: 45,
    agenda: '',
  });

  const charger = (currentPfe: Pfe) => {
    meetingService
      .getMeetingsForPfe(currentPfe.id)
      .then((page) => setMeetings(page.content))
      .catch(() => toast.error('Impossible de charger les réunions'));
  };

  useEffect(() => {
    module2Api
      .getMyPfes()
      .then((pfes) => {
        if (pfes.length === 0) {
          setChargement(false);
          return;
        }
        setPfe(pfes[0]);
        charger(pfes[0]);
      })
      .finally(() => setChargement(false));
  }, []);

  const handleCreateMeeting = async () => {
    if (!pfe?.supervisorId || !newMeeting.title || !newMeeting.date || !newMeeting.time) {
      toast.error('Merci de remplir tous les champs obligatoires');
      return;
    }

    try {
      await meetingService.createMeeting({
        pfeId: pfe.id,
        participantId: pfe.supervisorId,
        title: newMeeting.title,
        description: newMeeting.agenda,
        meetingDate: new Date(`${newMeeting.date}T${newMeeting.time}`).toISOString(),
        duration: newMeeting.duration,
      });
      toast.success('Demande de réunion envoyée à votre encadrant');
      setShowCreateModal(false);
      setNewMeeting({ title: '', date: '', time: '', duration: 45, agenda: '' });
      charger(pfe);
    } catch {
      toast.error('Échec de la demande de réunion');
    }
  };

  const statusLabel: Record<string, string> = {
    PENDING: 'En attente',
    ACCEPTED: 'Confirmée',
    REFUSED: 'Refusée',
    CANCELLED: 'Annulée',
    COMPLETED: 'Terminée',
  };

  const statusClass: Record<string, string> = {
    PENDING: 'bg-amber-100 text-amber-700',
    ACCEPTED: 'bg-green-100 text-green-700',
    REFUSED: 'bg-red-100 text-red-700',
    CANCELLED: 'bg-gray-100 text-gray-700',
    COMPLETED: 'bg-blue-100 text-blue-700',
  };

  if (chargement) {
    return <p className="text-gray-600">Chargement...</p>;
  }

  if (!pfe) {
    return (
      <div className="bg-white rounded-lg p-6 border border-gray-200 text-center text-gray-600">
        Aucun PFE trouvé pour le moment.
      </div>
    );
  }

  const upcomingMeetings = meetings.filter((m) => new Date(m.meetingDate) >= new Date());
  const pastMeetings = meetings.filter((m) => new Date(m.meetingDate) < new Date());

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h2 className="text-xl font-semibold">Réunions</h2>
        <button
          onClick={() => setShowCreateModal(true)}
          disabled={!pfe.supervisorId}
          className="px-4 py-2 bg-[#1F4E79] text-white rounded-lg hover:bg-[#163A5C] disabled:opacity-50"
        >
          Proposer une réunion
        </button>
      </div>

      <div className="space-y-4">
        {upcomingMeetings.length === 0 && pastMeetings.length === 0 && (
          <div className="bg-white rounded-lg p-6 border border-gray-200 text-center text-gray-500">
            Aucune réunion pour le moment.
          </div>
        )}

        {upcomingMeetings.map((meeting) => (
          <div key={meeting.id} className="bg-white rounded-lg p-6 border border-gray-200">
            <div className="flex items-start justify-between mb-4">
              <div>
                <h3 className="text-lg font-semibold mb-2">{meeting.title}</h3>
                <div className="flex items-center gap-4 text-sm text-gray-600">
                  <div className="flex items-center gap-1">
                    <Calendar size={16} />
                    {new Date(meeting.meetingDate).toLocaleDateString('fr-FR', {
                      day: 'numeric',
                      month: 'long',
                      year: 'numeric',
                    })}
                  </div>
                  <div className="flex items-center gap-1">
                    <Clock size={16} />
                    {new Date(meeting.meetingDate).toLocaleTimeString('fr-FR', {
                      hour: '2-digit',
                      minute: '2-digit',
                    })}
                    {meeting.duration ? ` · ${meeting.duration} min` : ''}
                  </div>
                </div>
              </div>
              <span className={`px-3 py-1 rounded-full text-sm ${statusClass[meeting.status]}`}>
                {statusLabel[meeting.status]}
              </span>
            </div>

            {meeting.meetingLink && (
              <a
                href={meeting.meetingLink}
                target="_blank"
                rel="noopener noreferrer"
                className="inline-flex items-center gap-2 px-4 py-2 bg-green-500 text-white rounded-lg hover:bg-green-600 mb-4"
              >
                <Video size={18} />
                Rejoindre la réunion
              </a>
            )}

            {meeting.description && (
              <div>
                <h4 className="font-medium mb-2">Ordre du jour :</h4>
                <p className="text-sm text-gray-700">{meeting.description}</p>
              </div>
            )}
          </div>
        ))}

        {pastMeetings.map((meeting) => (
          <div key={meeting.id} className="bg-gray-50 rounded-lg p-6 border border-gray-200">
            <div className="flex items-start justify-between mb-4">
              <div>
                <h3 className="text-lg font-semibold mb-2">{meeting.title}</h3>
                <div className="flex items-center gap-4 text-sm text-gray-600">
                  <div className="flex items-center gap-1">
                    <Calendar size={16} />
                    {new Date(meeting.meetingDate).toLocaleDateString('fr-FR', {
                      day: 'numeric',
                      month: 'long',
                      year: 'numeric',
                    })}
                  </div>
                </div>
              </div>
              <span className={`px-3 py-1 rounded-full text-sm ${statusClass[meeting.status]}`}>
                {statusLabel[meeting.status]}
              </span>
            </div>

            {meeting.report && (
              <div className="bg-white p-4 rounded-lg mb-3">
                <h4 className="font-medium mb-2">Compte-rendu :</h4>
                <p className="text-sm text-gray-700">{meeting.report}</p>
              </div>
            )}
          </div>
        ))}
      </div>

      {showCreateModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-6 w-full max-w-md">
            <h3 className="text-xl font-semibold mb-4">Proposer une réunion</h3>
            <div className="space-y-4">
              <div>
                <label className="block text-sm font-medium mb-1">Titre</label>
                <input
                  type="text"
                  value={newMeeting.title}
                  onChange={(e) => setNewMeeting({ ...newMeeting, title: e.target.value })}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg"
                />
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium mb-1">Date</label>
                  <input
                    type="date"
                    value={newMeeting.date}
                    onChange={(e) => setNewMeeting({ ...newMeeting, date: e.target.value })}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium mb-1">Heure</label>
                  <input
                    type="time"
                    value={newMeeting.time}
                    onChange={(e) => setNewMeeting({ ...newMeeting, time: e.target.value })}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg"
                  />
                </div>
              </div>
              <div>
                <label className="block text-sm font-medium mb-1">Durée (minutes)</label>
                <select
                  value={newMeeting.duration}
                  onChange={(e) => setNewMeeting({ ...newMeeting, duration: Number(e.target.value) })}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg"
                >
                  <option value={30}>30 min</option>
                  <option value={45}>45 min</option>
                  <option value={60}>1h</option>
                  <option value={90}>1h30</option>
                </select>
              </div>
              <div>
                <label className="block text-sm font-medium mb-1">Ordre du jour</label>
                <textarea
                  value={newMeeting.agenda}
                  onChange={(e) => setNewMeeting({ ...newMeeting, agenda: e.target.value })}
                  rows={3}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg"
                ></textarea>
              </div>
              <div className="bg-blue-50 border border-blue-200 rounded-lg p-3">
                <p className="text-sm text-blue-800">
                  Un lien Google Meet sera généré automatiquement si votre encadrant a autorisé l'intégration.
                </p>
              </div>
            </div>
            <div className="flex gap-3 mt-6">
              <button
                onClick={() => setShowCreateModal(false)}
                className="flex-1 px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50"
              >
                Annuler
              </button>
              <button
                onClick={handleCreateMeeting}
                className="flex-1 px-4 py-2 bg-[#1F4E79] text-white rounded-lg hover:bg-[#163A5C]"
              >
                Proposer la réunion
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
