import { useEffect, useState } from 'react';
import { Target, AlertTriangle, Calendar, Flag } from 'lucide-react';
import dashboardService, { DashboardStudentDTO } from '../../../api/dashboardService';

const statusLabels: Record<string, string> = {
  NOT_STARTED: 'À faire',
  IN_PROGRESS: 'En cours',
  SUBMITTED: 'Soumis',
  VALIDATED: 'Validé',
  TO_CORRECT: 'À corriger',
  CANCELLED: 'Annulé',
};

export function EtudiantDashboard() {
  const [dashboard, setDashboard] = useState<DashboardStudentDTO | null>(null);
  const [erreur, setErreur] = useState(false);

  useEffect(() => {
    dashboardService
      .getStudentDashboard()
      .then(setDashboard)
      .catch(() => setErreur(true));
  }, []);

  if (erreur) {
    return (
      <div className="bg-white rounded-lg p-6 border border-gray-200 text-center">
        <p className="text-gray-600">
          Aucun PFE trouvé. Ton PFE sera créé automatiquement une fois affecté à un encadrant.
        </p>
      </div>
    );
  }

  if (!dashboard) {
    return <p className="text-gray-600">Chargement...</p>;
  }

  const progress = Math.round(dashboard.globalProgress);
  const allTasks = [...dashboard.ongoingTasks];
  const tasksByStatus = {
    'À faire': allTasks.filter((t) => t.status === 'NOT_STARTED'),
    'En cours': allTasks.filter((t) => t.status === 'IN_PROGRESS'),
    Soumis: allTasks.filter((t) => t.status === 'SUBMITTED'),
    'À corriger': allTasks.filter((t) => t.status === 'TO_CORRECT'),
  };

  return (
    <div className="space-y-6">
      <div className="grid grid-cols-4 gap-6">
        <div className="bg-white rounded-lg p-6 border border-gray-200">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-gray-600">Progression globale</h3>
            <Target className="text-[#1D9E75]" size={24} />
          </div>
          <div className="flex items-center gap-4">
            <div className="relative w-20 h-20">
              <svg className="transform -rotate-90 w-20 h-20">
                <circle cx="40" cy="40" r="32" stroke="#E5E7EB" strokeWidth="8" fill="none" />
                <circle
                  cx="40"
                  cy="40"
                  r="32"
                  stroke="#1D9E75"
                  strokeWidth="8"
                  fill="none"
                  strokeDasharray={`${progress * 2} ${100 * 2}`}
                  strokeLinecap="round"
                />
              </svg>
              <div className="absolute inset-0 flex items-center justify-center text-xl font-bold text-[#1D9E75]">
                {progress}%
              </div>
            </div>
          </div>
        </div>

        <div className="bg-red-50 rounded-lg p-6 border border-red-200">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-gray-600">Tâches en retard</h3>
            <AlertTriangle className="text-red-600" size={24} />
          </div>
          <p className="text-3xl font-bold text-red-600">
            {dashboard.upcomingDeadlines.filter((t) => t.isOverdue).length}
          </p>
        </div>

        <div className="bg-white rounded-lg p-6 border border-gray-200">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-gray-600">Prochaine réunion</h3>
            <Calendar className="text-[#1F4E79]" size={24} />
          </div>
          {dashboard.nextMeeting ? (
            <>
              <p className="text-xl font-bold text-gray-800">{dashboard.nextMeeting.title}</p>
              <p className="text-sm text-gray-500 mt-1">
                {new Date(dashboard.nextMeeting.meetingDate).toLocaleString('fr-FR')}
              </p>
            </>
          ) : (
            <p className="text-sm text-gray-500 mt-1">Aucune réunion planifiée</p>
          )}
        </div>

        <div className="bg-white rounded-lg p-6 border border-gray-200">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-gray-600">Jalon actuel</h3>
            <Flag className="text-amber-500" size={24} />
          </div>
          <p className="text-xl font-bold text-gray-800">{dashboard.currentMilestone || '—'}</p>
          <p className="text-sm text-gray-500 mt-1">
            Suivant : {dashboard.nextMilestone || 'Aucun'}
          </p>
        </div>
      </div>

      <div className="grid grid-cols-3 gap-6">
        <div className="col-span-2">
          <div className="bg-white rounded-lg p-6 border border-gray-200">
            <h3 className="text-lg font-semibold mb-4">Tâches en cours — {dashboard.pfeTitle}</h3>
            <div className="grid grid-cols-4 gap-4">
              {Object.entries(tasksByStatus).map(([status, statusTasks]) => (
                <div key={status} className="space-y-3">
                  <div className="flex items-center justify-between">
                    <h4 className="font-medium text-gray-700">{status}</h4>
                    <span className="text-sm text-gray-500">({statusTasks.length})</span>
                  </div>
                  <div className="space-y-2">
                    {statusTasks.map((task) => (
                      <div
                        key={task.id}
                        className="bg-gray-50 rounded-lg p-3 border border-gray-200 cursor-pointer hover:shadow-md transition-shadow"
                      >
                        <p className="text-sm font-medium text-gray-800 mb-2">{task.title}</p>
                        {task.deadline && (
                          <div className="flex items-center justify-between">
                            <span className="text-xs text-gray-500">
                              {new Date(task.deadline).toLocaleDateString('fr-FR', {
                                day: 'numeric',
                                month: 'short',
                              })}
                            </span>
                          </div>
                        )}
                      </div>
                    ))}
                    {statusTasks.length === 0 && (
                      <p className="text-xs text-gray-400">Aucune tâche</p>
                    )}
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>

        <div className="space-y-6">
          <div className="bg-white rounded-lg p-6 border border-gray-200">
            <h3 className="text-lg font-semibold mb-4">Prochaines deadlines</h3>
            <div className="space-y-3">
              {dashboard.upcomingDeadlines.length === 0 && (
                <p className="text-sm text-gray-500">Aucune deadline à venir</p>
              )}
              {dashboard.upcomingDeadlines.map((task) => (
                <div
                  key={task.id}
                  className={`p-3 rounded-lg ${task.isOverdue ? 'bg-red-50 border border-red-200' : 'bg-gray-50'}`}
                >
                  <div className="flex items-start justify-between">
                    <div className="flex-1">
                      <p className="text-sm font-medium text-gray-800">{task.title}</p>
                      <p className="text-xs text-gray-500 mt-1">{statusLabels[task.status] || task.status}</p>
                    </div>
                    {task.isOverdue && (
                      <span className="text-xs px-2 py-0.5 bg-red-500 text-white rounded-full">EN RETARD</span>
                    )}
                  </div>
                  {task.deadline && (
                    <p className={`text-xs mt-2 ${task.isOverdue ? 'text-red-600' : 'text-gray-500'}`}>
                      {new Date(task.deadline).toLocaleDateString('fr-FR', {
                        day: 'numeric',
                        month: 'long',
                        year: 'numeric',
                      })}
                    </p>
                  )}
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>

      {dashboard.activeAlerts.length > 0 && (
        <div className="bg-amber-50 rounded-lg p-6 border border-amber-200">
          <h3 className="text-lg font-semibold mb-4 text-amber-800">Alertes actives</h3>
          <div className="space-y-2">
            {dashboard.activeAlerts.map((a, i) => (
              <p key={i} className="text-sm text-amber-800">{a.message}</p>
            ))}
          </div>
        </div>
      )}

      <div className="bg-white rounded-lg p-6 border border-gray-200">
        <h3 className="text-lg font-semibold mb-4">Notifications récentes</h3>
        <div className="space-y-3">
          {dashboard.recentNotifications.length === 0 && (
            <p className="text-sm text-gray-500">Aucune notification récente</p>
          )}
          {dashboard.recentNotifications.map((notif) => (
            <div key={notif.id} className="flex items-start gap-3 p-3 hover:bg-gray-50 rounded-lg">
              <div className="w-2 h-2 bg-[#1F4E79] rounded-full mt-2"></div>
              <div className="flex-1">
                <p className="text-sm text-gray-800">{notif.message}</p>
                <p className="text-xs text-gray-500 mt-1">
                  {new Date(notif.createdAt).toLocaleString('fr-FR')}
                </p>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
