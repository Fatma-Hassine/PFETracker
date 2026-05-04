import { useEffect, useState } from 'react';
import { Loader2, Search } from 'lucide-react';
import { toast } from 'sonner';
import { module2Api } from '../../api/module2Api';
import { Milestone, Pfe, Priority, Task, TaskStatus } from '../../types/module2.types';

interface TaskWithMilestone extends Task {
  jalon: string;
}

const priorityColors: Record<string, string> = {
  Faible: 'bg-gray-500',
  Normale: 'bg-blue-500',
  Haute: 'bg-amber-500',
  Critique: 'bg-red-500',
};

const statusColors: Record<string, string> = {
  'Non commencé': 'bg-gray-100 text-gray-700',
  'En cours': 'bg-amber-100 text-amber-700',
  Soumis: 'bg-blue-100 text-blue-700',
  Validé: 'bg-green-100 text-green-700',
  'À corriger': 'bg-red-100 text-red-700',
  Annulé: 'bg-gray-100 text-gray-700',
};

function mapPriority(priority: Priority) {
  if (priority === 'LOW') return 'Faible';
  if (priority === 'HIGH') return 'Haute';
  if (priority === 'CRITICAL') return 'Critique';
  return 'Normale';
}

function mapStatus(status: TaskStatus) {
  if (status === 'IN_PROGRESS') return 'En cours';
  if (status === 'SUBMITTED') return 'Soumis';
  if (status === 'VALIDATED') return 'Validé';
  if (status === 'TO_CORRECT') return 'À corriger';
  if (status === 'CANCELLED') return 'Annulé';
  return 'Non commencé';
}

function nextStatus(status: TaskStatus): TaskStatus | null {
  if (status === 'NOT_STARTED') return 'IN_PROGRESS';
  if (status === 'IN_PROGRESS') return 'SUBMITTED';
  return null;
}

function actionLabel(status: TaskStatus) {
  if (status === 'NOT_STARTED') return 'Démarrer';
  if (status === 'IN_PROGRESS') return 'Soumettre';
  return null;
}

export function EtudiantTaches() {
  const [searchQuery, setSearchQuery] = useState('');
  const [jalonFilter, setJalonFilter] = useState('Tous');
  const [statutFilter, setStatutFilter] = useState('Tous');
  const [prioriteFilter, setPrioriteFilter] = useState('Tous');

  const [pfe, setPfe] = useState<Pfe | null>(null);
  const [milestones, setMilestones] = useState<Milestone[]>([]);
  const [allTasks, setAllTasks] = useState<TaskWithMilestone[]>([]);
  const [loading, setLoading] = useState(true);
  const [updatingTaskId, setUpdatingTaskId] = useState<number | null>(null);

  useEffect(() => {
    loadTasks();
  }, []);

  const loadTasks = async () => {
    try {
      setLoading(true);

      const pfes = await module2Api.getMyPfes();

      if (pfes.length === 0) {
        setPfe(null);
        setMilestones([]);
        setAllTasks([]);
        return;
      }

      const currentPfe = pfes[0];
      setPfe(currentPfe);

      const loadedMilestones = await module2Api.getMilestones(currentPfe.id);
      setMilestones(loadedMilestones);

      const tasksByMilestone = await Promise.all(
        loadedMilestones.map(async (milestone) => {
          const tasks = await module2Api.getTasksByMilestone(milestone.id);

          return tasks.map((task) => ({
            ...task,
            jalon: milestone.title,
          }));
        })
      );

      setAllTasks(tasksByMilestone.flat());
    } catch (error) {
      console.error(error);
      toast.error('Erreur lors du chargement des tâches');
    } finally {
      setLoading(false);
    }
  };

  const handleStatusAction = async (task: TaskWithMilestone) => {
    const status = nextStatus(task.status);

    if (!status) {
      return;
    }

    setUpdatingTaskId(task.id);

    try {
      await module2Api.updateTaskStatus(
        task.id,
        status,
        status === 'IN_PROGRESS'
          ? 'Tâche démarrée depuis l’interface étudiant.'
          : 'Tâche soumise depuis l’interface étudiant.'
      );

      toast.success('Statut mis à jour');
      await loadTasks();
    } catch (error) {
      console.error(error);
      toast.error('Erreur lors de la mise à jour du statut');
    } finally {
      setUpdatingTaskId(null);
    }
  };

  const tasks = allTasks.filter((task) => {
    const statusLabel = mapStatus(task.status);
    const priorityLabel = mapPriority(task.priority);

    const matchesSearch = task.title.toLowerCase().includes(searchQuery.toLowerCase());
    const matchesJalon = jalonFilter === 'Tous' || task.jalon === jalonFilter;
    const matchesStatut = statutFilter === 'Tous' || statusLabel === statutFilter;
    const matchesPriorite = prioriteFilter === 'Tous' || priorityLabel === prioriteFilter;

    return matchesSearch && matchesJalon && matchesStatut && matchesPriorite;
  });

  const isOverdue = (deadline: string | undefined, status: TaskStatus) => {
    if (!deadline) return false;
    return new Date(deadline) < new Date() && status !== 'VALIDATED';
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center py-20">
        <Loader2 className="animate-spin text-[#1F4E79]" size={32} />
      </div>
    );
  }

  if (!pfe) {
    return (
      <div className="bg-white rounded-lg p-6 border border-gray-200">
        <h2 className="text-xl font-bold text-gray-800">Aucun PFE trouvé</h2>
        <p className="text-gray-600 mt-2">
          Aucun PFE n’est encore importé pour cet étudiant.
        </p>
      </div>
    );
  }

  const uniqueJalons = ['Tous', ...milestones.map((milestone) => milestone.title)];

  return (
    <div className="space-y-6">
      <div className="bg-white rounded-lg p-6 border border-gray-200">
        <div className="mb-4">
          <h2 className="text-xl font-bold text-gray-800">Mes tâches</h2>
          <p className="text-sm text-gray-600 mt-1">{pfe.title}</p>
        </div>

        {milestones.length === 0 ? (
          <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
            <p className="text-blue-900 font-medium">Aucun jalon trouvé</p>
            <p className="text-sm text-blue-800 mt-1">
              Ajoute d’abord les jalons depuis l’Assistant IA. Ensuite les tâches pourront être ajoutées dans chaque jalon.
            </p>
          </div>
        ) : (
          <>
            <div className="flex items-center gap-4 mb-4">
              <div className="relative flex-1">
                <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" size={20} />
                <input
                  type="text"
                  placeholder="Rechercher une tâche..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#1F4E79]"
                />
              </div>

              <select
                value={jalonFilter}
                onChange={(e) => setJalonFilter(e.target.value)}
                className="px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#1F4E79]"
              >
                {uniqueJalons.map((jalon) => (
                  <option key={jalon}>{jalon}</option>
                ))}
              </select>

              <select
                value={statutFilter}
                onChange={(e) => setStatutFilter(e.target.value)}
                className="px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#1F4E79]"
              >
                <option>Tous</option>
                <option>Non commencé</option>
                <option>En cours</option>
                <option>Soumis</option>
                <option>Validé</option>
                <option>À corriger</option>
                <option>Annulé</option>
              </select>

              <select
                value={prioriteFilter}
                onChange={(e) => setPrioriteFilter(e.target.value)}
                className="px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#1F4E79]"
              >
                <option>Tous</option>
                <option>Faible</option>
                <option>Normale</option>
                <option>Haute</option>
                <option>Critique</option>
              </select>
            </div>

            <div className="overflow-x-auto">
              <table className="w-full">
                <thead>
                  <tr className="border-b border-gray-200">
                    <th className="text-left py-3 px-4 font-medium text-gray-700">Tâche</th>
                    <th className="text-left py-3 px-4 font-medium text-gray-700">Jalon</th>
                    <th className="text-left py-3 px-4 font-medium text-gray-700">Priorité</th>
                    <th className="text-left py-3 px-4 font-medium text-gray-700">Deadline</th>
                    <th className="text-left py-3 px-4 font-medium text-gray-700">Statut</th>
                    <th className="text-left py-3 px-4 font-medium text-gray-700">Actions</th>
                  </tr>
                </thead>

                <tbody>
                  {tasks.length === 0 ? (
                    <tr>
                      <td colSpan={6} className="py-8 text-center text-gray-500">
                        Aucune tâche trouvée
                      </td>
                    </tr>
                  ) : (
                    tasks.map((task) => {
                      const statusLabel = mapStatus(task.status);
                      const priorityLabel = mapPriority(task.priority);
                      const action = actionLabel(task.status);
                      const overdue = isOverdue(task.deadline, task.status);

                      return (
                        <tr
                          key={task.id}
                          className={`border-b border-gray-100 ${
                            overdue
                              ? 'bg-red-50'
                              : task.status === 'VALIDATED'
                              ? 'bg-green-50'
                              : ''
                          }`}
                        >
                          <td className="py-3 px-4 text-gray-800">{task.title}</td>

                          <td className="py-3 px-4 text-gray-600">{task.jalon}</td>

                          <td className="py-3 px-4">
                            <span className={`px-3 py-1 rounded-full text-white text-sm ${priorityColors[priorityLabel]}`}>
                              {priorityLabel}
                            </span>
                          </td>

                          <td className={`py-3 px-4 ${overdue ? 'text-red-600 font-medium' : 'text-gray-600'}`}>
                            {task.deadline ? new Date(task.deadline).toLocaleDateString('fr-FR') : 'Non définie'}
                          </td>

                          <td className="py-3 px-4">
                            <span className={`px-3 py-1 rounded-full text-sm ${statusColors[statusLabel]}`}>
                              {statusLabel}
                            </span>
                          </td>

                          <td className="py-3 px-4">
                            <div className="flex gap-2">
                              <button className="px-3 py-1 bg-[#1F4E79] text-white rounded hover:bg-[#163A5C] text-sm">
                                Voir
                              </button>

                              {action && (
                                <button
                                  onClick={() => handleStatusAction(task)}
                                  disabled={updatingTaskId === task.id}
                                  className="px-3 py-1 bg-[#1D9E75] text-white rounded hover:bg-[#177A5D] text-sm disabled:opacity-50"
                                >
                                  {updatingTaskId === task.id ? '...' : action}
                                </button>
                              )}
                            </div>
                          </td>
                        </tr>
                      );
                    })
                  )}
                </tbody>
              </table>
            </div>
          </>
        )}
      </div>
    </div>
  );
}