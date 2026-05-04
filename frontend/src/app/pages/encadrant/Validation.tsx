import { useEffect, useState } from 'react';
import { ClipboardCheck, Loader2 } from 'lucide-react';
import { toast } from 'sonner';
import { module2Api } from '../../api/module2Api';
import { Milestone, Pfe, Task } from '../../types/module2.types';

interface SubmittedTaskRow {
  task: Task;
  pfe: Pfe;
  milestone: Milestone;
}

function formatDate(date?: string) {
  if (!date) return 'Non définie';
  return new Date(date).toLocaleDateString('fr-FR');
}

export function EncadrantValidation() {
  const [tasks, setTasks] = useState<SubmittedTaskRow[]>([]);
  const [loading, setLoading] = useState(true);
  const [updatingTaskId, setUpdatingTaskId] = useState<number | null>(null);

  useEffect(() => {
    loadSubmittedTasks();
  }, []);

  const loadSubmittedTasks = async () => {
    try {
      setLoading(true);

      const pfes = await module2Api.getMyPfes();
      const rows: SubmittedTaskRow[] = [];

      for (const pfe of pfes) {
        const milestones = await module2Api.getMilestones(pfe.id);

        for (const milestone of milestones) {
          const milestoneTasks = await module2Api.getTasksByMilestone(milestone.id);

          milestoneTasks
            .filter((task) => task.status === 'SUBMITTED')
            .forEach((task) => {
              rows.push({
                task,
                pfe,
                milestone,
              });
            });
        }
      }

      setTasks(rows);
    } catch (error) {
      console.error(error);
      toast.error('Erreur lors du chargement des tâches à valider');
    } finally {
      setLoading(false);
    }
  };

  const handleValidate = async (taskId: number) => {
    setUpdatingTaskId(taskId);

    try {
      await module2Api.updateTaskStatus(
        taskId,
        'VALIDATED',
        'Travail validé par l’encadrant.'
      );

      toast.success('Tâche validée');
      await loadSubmittedTasks();
    } catch (error) {
      console.error(error);
      toast.error('Erreur lors de la validation');
    } finally {
      setUpdatingTaskId(null);
    }
  };

  const handleReject = async (taskId: number) => {
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
      await loadSubmittedTasks();
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

  if (tasks.length === 0) {
    return (
      <div className="bg-white rounded-lg p-12 border border-gray-200 text-center">
        <ClipboardCheck size={64} className="mx-auto text-green-500 mb-4" />
        <h3 className="text-xl font-semibold mb-2">Toutes les tâches sont validées</h3>
        <p className="text-gray-600">Aucune tâche en attente de validation</p>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div className="bg-white rounded-lg p-6 border border-gray-200">
        <div className="flex items-center justify-between mb-4">
          <div>
            <h3 className="text-lg font-semibold">File de validation</h3>
            <p className="text-sm text-gray-600 mt-1">
              {tasks.length} tâche(s) soumise(s) en attente de validation
            </p>
          </div>

          <button
            onClick={loadSubmittedTasks}
            className="px-4 py-2 bg-[#1F4E79] text-white rounded-lg hover:bg-[#163A5C]"
          >
            Actualiser
          </button>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full">
            <thead>
              <tr className="border-b border-gray-200">
                <th className="text-left py-3 px-4 font-medium text-gray-700">Étudiant</th>
                <th className="text-left py-3 px-4 font-medium text-gray-700">PFE</th>
                <th className="text-left py-3 px-4 font-medium text-gray-700">Tâche</th>
                <th className="text-left py-3 px-4 font-medium text-gray-700">Jalon</th>
                <th className="text-left py-3 px-4 font-medium text-gray-700">Deadline</th>
                <th className="text-left py-3 px-4 font-medium text-gray-700">Priorité</th>
                <th className="text-left py-3 px-4 font-medium text-gray-700">Fichier</th>
                <th className="text-left py-3 px-4 font-medium text-gray-700">Actions</th>
              </tr>
            </thead>

            <tbody>
              {tasks.map(({ task, pfe, milestone }) => (
                <tr key={task.id} className="border-b border-gray-100">
                  <td className="py-3 px-4 text-gray-800">
                    <p className="font-medium">
                      {pfe.studentName || `Étudiant ${pfe.studentId}`}
                    </p>
                    {pfe.studentEmail && (
                      <p className="text-xs text-gray-500">{pfe.studentEmail}</p>
                    )}
                  </td>

                  <td className="py-3 px-4 text-gray-800">
                    <p className="max-w-xs truncate">{pfe.title}</p>
                  </td>

                  <td className="py-3 px-4 text-gray-800">
                    <p className="font-medium">{task.title}</p>
                    {task.description && (
                      <p className="text-xs text-gray-500 max-w-xs truncate">
                        {task.description}
                      </p>
                    )}
                  </td>

                  <td className="py-3 px-4 text-gray-600">{milestone.title}</td>

                  <td className="py-3 px-4 text-gray-600">{formatDate(task.deadline)}</td>

                  <td className="py-3 px-4 text-gray-600">{task.priority}</td>

                  <td className="py-3 px-4">
                    <button className="text-[#1F4E79] hover:underline text-sm">
                      Voir fichier
                    </button>
                  </td>

                  <td className="py-3 px-4">
                    <div className="flex gap-2">
                      <button
                        onClick={() => handleValidate(task.id)}
                        disabled={updatingTaskId === task.id}
                        className="px-3 py-1 bg-green-500 text-white rounded hover:bg-green-600 text-sm disabled:opacity-50"
                      >
                        {updatingTaskId === task.id ? '...' : 'Valider'}
                      </button>

                      <button
                        onClick={() => handleReject(task.id)}
                        disabled={updatingTaskId === task.id}
                        className="px-3 py-1 bg-red-500 text-white rounded hover:bg-red-600 text-sm disabled:opacity-50"
                      >
                        À corriger
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}