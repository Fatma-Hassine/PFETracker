import { useEffect, useState } from 'react';
import { useParams } from 'react-router';
import { ChevronDown, ChevronRight, Loader2 } from 'lucide-react';
import { toast } from 'sonner';
import { module2Api } from '../../api/module2Api';
import { Milestone, Pfe, Task, TaskStatus } from '../../types/module2.types';

type EtudiantDetail = {
  id: number;
  nomComplet?: string;
  email?: string;
  sujet?: string;
  progression?: number;
  dateDebutStage?: string;
  dateFinStage?: string;
};



interface MilestoneWithTasks extends Milestone {
  tasks: Task[];
}

function formatDate(date?: string) {
  if (!date) return 'Non définie';
  return new Date(date).toLocaleDateString('fr-FR');
}

function mapMilestoneStatus(status: string) {
  if (status === 'COMPLETED') return 'Terminé';
  if (status === 'IN_PROGRESS') return 'En cours';
  return 'Non commencé';
}

function mapTaskStatus(status: TaskStatus) {
  if (status === 'VALIDATED') return 'Validé';
  if (status === 'SUBMITTED') return 'Soumis';
  if (status === 'IN_PROGRESS') return 'En cours';
  if (status === 'TO_CORRECT') return 'À corriger';
  if (status === 'CANCELLED') return 'Annulé';
  return 'Non commencé';
}

function taskStatusClass(status: TaskStatus) {
  if (status === 'VALIDATED') return 'bg-green-100 text-green-700';
  if (status === 'SUBMITTED') return 'bg-blue-100 text-blue-700';
  if (status === 'IN_PROGRESS') return 'bg-amber-100 text-amber-700';
  if (status === 'TO_CORRECT') return 'bg-red-100 text-red-700';
  return 'bg-gray-100 text-gray-700';
}

export function EncadrantEtudiantDetail() {
  const params = useParams();
  const pfeId = Number(params.id);

  const [pfe, setPfe] = useState<Pfe | null>(null);
  const [milestones, setMilestones] = useState<MilestoneWithTasks[]>([]);
  const [expandedMilestone, setExpandedMilestone] = useState<number | null>(null);
  const [loading, setLoading] = useState(true);
  const [updatingTaskId, setUpdatingTaskId] = useState<number | null>(null);

  useEffect(() => {
    loadDetails();
  }, [pfeId]);

  const loadDetails = async () => {
    if (!pfeId || Number.isNaN(pfeId)) {
      toast.error('Identifiant PFE invalide');
      setLoading(false);
      return;
    }

    try {
      setLoading(true);

      const loadedPfe = await module2Api.getPfeById(pfeId);
      setPfe(loadedPfe);

      const loadedMilestones = await module2Api.getMilestones(pfeId);

      const milestonesWithTasks = await Promise.all(
        loadedMilestones.map(async (milestone) => {
          const tasks = await module2Api.getTasksByMilestone(milestone.id);

          return {
            ...milestone,
            tasks,
          };
        })
      );

      setMilestones(milestonesWithTasks);

      if (milestonesWithTasks.length > 0) {
        setExpandedMilestone(milestonesWithTasks[0].id);
      }
    } catch (error) {
      console.error(error);
      toast.error('Erreur lors du chargement du détail étudiant');
    } finally {
      setLoading(false);
    }
  };

  const handleValidateTask = async (taskId: number) => {
    setUpdatingTaskId(taskId);

    try {
      await module2Api.updateTaskStatus(
        taskId,
        'VALIDATED',
        'Travail validé par l’encadrant.'
      );

      toast.success('Tâche validée');
      await loadDetails();
    } catch (error) {
      console.error(error);
      toast.error('Erreur lors de la validation');
    } finally {
      setUpdatingTaskId(null);
    }
  };

  const handleRequestCorrection = async (taskId: number) => {
    const comment = window.prompt('Commentaire de correction :');

    if (comment === null) {
      return;
    }

    setUpdatingTaskId(taskId);

    try {
      await module2Api.updateTaskStatus(
        taskId,
        'TO_CORRECT',
        comment || 'Correction demandée par l’encadrant.'
      );

      toast.info('Correction demandée');
      await loadDetails();
    } catch (error) {
      console.error(error);
      toast.error('Erreur lors de la demande de correction');
    } finally {
      setUpdatingTaskId(null);
    }
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
        <h2 className="text-xl font-bold text-gray-800">PFE introuvable</h2>
        <p className="text-gray-600 mt-2">
          Impossible de charger les informations de cet étudiant.
        </p>
      </div>
    );
  }

  const progress = Math.round(pfe.progress || 0);
  const submittedTasksCount = milestones
    .flatMap((milestone) => milestone.tasks)
    .filter((task) => task.status === 'SUBMITTED').length;

  return (
    <div className="space-y-6">
      <div className="bg-white rounded-lg p-6 border border-gray-200">
        <div className="flex items-start justify-between gap-4 mb-6">
          <div>
            <h2 className="text-2xl font-bold mb-2">
              {pfe.studentName || `Étudiant ${pfe.studentId}`}
            </h2>

            {pfe.studentEmail && (
              <p className="text-gray-500 text-sm mb-1">{pfe.studentEmail}</p>
            )}

            <p className="text-gray-700 font-medium">{pfe.title}</p>

            <div className="flex flex-wrap gap-2 mt-3">
              {pfe.department && (
                <span className="px-2 py-1 bg-blue-100 text-blue-700 rounded text-xs">
                  {pfe.department}
                </span>
              )}

              <span className="px-2 py-1 bg-gray-100 text-gray-700 rounded text-xs">
                {pfe.status}
              </span>

              {submittedTasksCount > 0 && (
                <span className="px-2 py-1 bg-amber-100 text-amber-700 rounded text-xs">
                  {submittedTasksCount} tâche(s) à valider
                </span>
              )}
            </div>
          </div>

          <button
            onClick={loadDetails}
            className="px-4 py-2 bg-[#1F4E79] text-white rounded-lg hover:bg-[#163A5C]"
          >
            Actualiser
          </button>
        </div>

        <div className="grid grid-cols-2 gap-4 mb-6 text-sm">
          <div>
            <p className="text-gray-600">Encadrant</p>
            <p className="font-medium">{pfe.supervisorName || `ID ${pfe.supervisorId}`}</p>
            {pfe.supervisorEmail && (
              <p className="text-gray-500">{pfe.supervisorEmail}</p>
            )}
          </div>

          <div>
            <p className="text-gray-600">Technologies</p>
            <p className="font-medium">{pfe.technologies || 'Non définies'}</p>
          </div>

          <div>
            <p className="text-gray-600">Date début</p>
            <p className="font-medium">{formatDate(pfe.startDate)}</p>
          </div>

          <div>
            <p className="text-gray-600">Soutenance prévue</p>
            <p className="font-medium">{formatDate(pfe.defenseDate)}</p>
          </div>
        </div>

        {pfe.description && (
          <div className="bg-gray-50 rounded-lg p-4 mb-6">
            <p className="text-sm text-gray-600 mb-1">Description</p>
            <p className="text-gray-800">{pfe.description}</p>
          </div>
        )}

        <div>
          <div className="flex items-center gap-2 mb-2">
            <p className="text-sm text-gray-600">Progression globale</p>
            <p className="text-sm font-medium text-[#1D9E75]">{progress}%</p>
          </div>

          <div className="w-full h-3 bg-gray-200 rounded-full overflow-hidden">
            <div
              className="h-full bg-[#1D9E75] rounded-full"
              style={{ width: `${progress}%` }}
            ></div>
          </div>
        </div>
      </div>

      <div className="bg-white rounded-lg p-6 border border-gray-200">
        <h3 className="text-lg font-semibold mb-4">Jalons et tâches</h3>

        {milestones.length === 0 ? (
          <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
            <p className="text-blue-900 font-medium">Aucun jalon créé</p>
            <p className="text-sm text-blue-800 mt-1">
              L’étudiant doit encore générer ou ajouter les jalons de son PFE.
            </p>
          </div>
        ) : (
          <div className="space-y-4">
            {milestones.map((milestone) => {
              const statusLabel = mapMilestoneStatus(milestone.status);
              const milestoneProgress = Math.round(milestone.progress || 0);

              return (
                <div key={milestone.id} className="border border-gray-200 rounded-lg overflow-hidden">
                  <button
                    onClick={() =>
                      setExpandedMilestone(expandedMilestone === milestone.id ? null : milestone.id)
                    }
                    className="w-full flex items-center justify-between p-4 bg-gray-50"
                  >
                    <div className="flex items-center gap-3">
                      {expandedMilestone === milestone.id ? (
                        <ChevronDown size={20} />
                      ) : (
                        <ChevronRight size={20} />
                      )}

                      <div className="text-left">
                        <h4 className="font-semibold text-gray-800">
                          {milestone.orderIndex}. {milestone.title}
                        </h4>
                        <p className="text-sm text-gray-500">
                          {formatDate(milestone.plannedStartDate)} - {formatDate(milestone.plannedEndDate)}
                        </p>
                      </div>
                    </div>

                    <div className="flex items-center gap-3">
                      <span className="text-sm text-gray-600">{milestoneProgress}%</span>
                      <span
                        className={`px-3 py-1 rounded-full text-sm ${
                          statusLabel === 'Terminé'
                            ? 'bg-green-100 text-green-700'
                            : statusLabel === 'En cours'
                            ? 'bg-amber-100 text-amber-700'
                            : 'bg-gray-100 text-gray-700'
                        }`}
                      >
                        {statusLabel}
                      </span>
                    </div>
                  </button>

                  {expandedMilestone === milestone.id && (
                    <div className="p-4 space-y-3">
                      {milestone.description && (
                        <p className="text-sm text-gray-700">{milestone.description}</p>
                      )}

                      {milestone.expectedDeliverable && (
                        <p className="text-sm text-gray-600">
                          Livrable attendu : <span className="font-medium">{milestone.expectedDeliverable}</span>
                        </p>
                      )}

                      <div className="w-full h-2 bg-gray-200 rounded-full overflow-hidden">
                        <div
                          className="h-full bg-[#1D9E75] rounded-full"
                          style={{ width: `${milestoneProgress}%` }}
                        ></div>
                      </div>

                      {milestone.tasks.length === 0 ? (
                        <p className="text-sm text-gray-500">Aucune tâche pour ce jalon</p>
                      ) : (
                        <div className="overflow-x-auto">
                          <table className="w-full text-sm">
                            <thead>
                              <tr className="border-b border-gray-200">
                                <th className="text-left py-2 px-3 font-medium text-gray-700">Tâche</th>
                                <th className="text-left py-2 px-3 font-medium text-gray-700">Deadline</th>
                                <th className="text-left py-2 px-3 font-medium text-gray-700">Priorité</th>
                                <th className="text-left py-2 px-3 font-medium text-gray-700">Statut</th>
                                <th className="text-left py-2 px-3 font-medium text-gray-700">Actions</th>
                              </tr>
                            </thead>

                            <tbody>
                              {milestone.tasks.map((task) => (
                                <tr key={task.id} className="border-b border-gray-100">
                                  <td className="py-2 px-3 text-gray-800">
                                    <p className="font-medium">{task.title}</p>
                                    {task.description && (
                                      <p className="text-xs text-gray-500 mt-1">{task.description}</p>
                                    )}
                                  </td>

                                  <td className="py-2 px-3 text-gray-600">
                                    {formatDate(task.deadline)}
                                  </td>

                                  <td className="py-2 px-3 text-gray-600">
                                    {task.priority}
                                  </td>

                                  <td className="py-2 px-3">
                                    <span className={`px-2 py-1 rounded-full text-xs ${taskStatusClass(task.status)}`}>
                                      {mapTaskStatus(task.status)}
                                    </span>
                                  </td>

                                  <td className="py-2 px-3">
                                    {task.status === 'SUBMITTED' ? (
                                      <div className="flex gap-2">
                                        <button
                                          onClick={() => handleValidateTask(task.id)}
                                          disabled={updatingTaskId === task.id}
                                          className="px-3 py-1 bg-green-500 text-white rounded hover:bg-green-600 text-xs disabled:opacity-50"
                                        >
                                          Valider
                                        </button>

                                        <button
                                          onClick={() => handleRequestCorrection(task.id)}
                                          disabled={updatingTaskId === task.id}
                                          className="px-3 py-1 bg-red-500 text-white rounded hover:bg-red-600 text-xs disabled:opacity-50"
                                        >
                                          À corriger
                                        </button>
                                      </div>
                                    ) : (
                                      <span className="text-xs text-gray-400">Aucune action</span>
                                    )}
                                  </td>
                                </tr>
                              ))}
                            </tbody>
                          </table>
                        </div>
                      )}
                    </div>
                  )}
                </div>
              );
            })}
          </div>
        )}
      </div>
    </div>
  );
}