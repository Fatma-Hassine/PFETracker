import { useEffect, useState } from 'react';
import { ChevronDown, ChevronRight, Download, Loader2, PlusCircle } from 'lucide-react';
import { toast } from 'sonner';
import { module2Api } from '../../api/module2Api';
import { Milestone, Pfe, Task } from '../../types/module2.types';

interface MilestoneWithTasks extends Milestone {
  tasks: Task[];
}

interface ManualPfeForm {
  studentName: string;
  studentEmail: string;
  supervisorId: string;
  supervisorName: string;
  supervisorEmail: string;
  department: string;
  title: string;
  description: string;
  problemStatement: string;
  objectives: string;
  technologies: string;
  startDate: string;
  defenseDate: string;
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

function mapTaskStatus(status: string) {
  if (status === 'VALIDATED') return 'Validé';
  if (status === 'SUBMITTED') return 'Soumis';
  if (status === 'IN_PROGRESS') return 'En cours';
  if (status === 'TO_CORRECT') return 'À corriger';
  if (status === 'CANCELLED') return 'Annulé';
  return 'Non commencé';
}

function getCurrentStudentId() {
  const value = localStorage.getItem('demoUserId') || '1';
  return Number(value);
}

export function EtudiantPFE() {
  const [expandedMilestone, setExpandedMilestone] = useState<number | null>(null);
  const [pfe, setPfe] = useState<Pfe | null>(null);
  const [milestones, setMilestones] = useState<MilestoneWithTasks[]>([]);
  const [loading, setLoading] = useState(true);
  const [creating, setCreating] = useState(false);

  const [form, setForm] = useState<ManualPfeForm>({
    studentName: '',
    studentEmail: '',
    supervisorId: '2',
    supervisorName: '',
    supervisorEmail: '',
    department: 'Génie Informatique',
    title: '',
    description: '',
    problemStatement: '',
    objectives: '',
    technologies: '',
    startDate: '',
    defenseDate: '',
  });

  useEffect(() => {
    loadPfeData();
  }, []);

  const loadPfeData = async () => {
    try {
      setLoading(true);

      const pfes = await module2Api.getMyPfes();

      if (pfes.length === 0) {
        setPfe(null);
        setMilestones([]);
        return;
      }

      const currentPfe = pfes[0];
      setPfe(currentPfe);

      const loadedMilestones = await module2Api.getMilestones(currentPfe.id);

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
      toast.error('Erreur lors du chargement du PFE');
    } finally {
      setLoading(false);
    }
  };

  const handleFormChange = (field: keyof ManualPfeForm, value: string) => {
    setForm((current) => ({
      ...current,
      [field]: value,
    }));
  };

  const handleCreatePfe = async () => {
    if (!form.title.trim()) {
      toast.error('Le titre du PFE est obligatoire');
      return;
    }

    if (!form.supervisorId.trim()) {
      toast.error('L’identifiant de l’encadrant est obligatoire');
      return;
    }

    const supervisorId = Number(form.supervisorId);

    if (Number.isNaN(supervisorId)) {
      toast.error('L’identifiant de l’encadrant doit être un nombre');
      return;
    }

    setCreating(true);

    try {
      const createdPfe = await module2Api.createPfe({
        studentId: getCurrentStudentId(),
        studentName: form.studentName,
        studentEmail: form.studentEmail,
        supervisorId,
        supervisorName: form.supervisorName,
        supervisorEmail: form.supervisorEmail,
        department: form.department,
        title: form.title,
        description: form.description,
        problemStatement: form.problemStatement,
        objectives: form.objectives,
        technologies: form.technologies,
        startDate: form.startDate || undefined,
        defenseDate: form.defenseDate || undefined,
      });

      toast.success('PFE créé avec succès');
      setPfe(createdPfe);
      setMilestones([]);
    } catch (error) {
      console.error(error);
      toast.error('Erreur lors de la création du PFE');
    } finally {
      setCreating(false);
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
      <div className="space-y-6">
        <div className="bg-white rounded-lg p-6 border border-gray-200">
          <div className="flex items-center gap-3 mb-4">
            <PlusCircle className="text-[#1F4E79]" size={28} />
            <div>
              <h2 className="text-xl font-bold text-gray-800">Aucun PFE trouvé</h2>
              <p className="text-gray-600 mt-1">
                Aucun PFE n’est encore importé pour cet étudiant. Vous pouvez saisir manuellement votre fiche PFE.
              </p>
            </div>
          </div>

          <div className="bg-blue-50 border border-blue-200 rounded-lg p-4 mb-6">
            <p className="text-sm text-blue-900">
              Normalement, les PFE sont importés automatiquement depuis un fichier Excel administratif.
              Cette saisie manuelle sert de solution de secours si l’étudiant n’existe pas dans le fichier.
            </p>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Nom étudiant
              </label>
              <input
                value={form.studentName}
                onChange={(e) => handleFormChange('studentName', e.target.value)}
                placeholder="Ex: Ahmed Ben Salem"
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#1F4E79]"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Email étudiant
              </label>
              <input
                type="email"
                value={form.studentEmail}
                onChange={(e) => handleFormChange('studentEmail', e.target.value)}
                placeholder="Ex: ahmed@email.com"
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#1F4E79]"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                ID encadrant *
              </label>
              <input
                value={form.supervisorId}
                onChange={(e) => handleFormChange('supervisorId', e.target.value)}
                placeholder="Ex: 2"
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#1F4E79]"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Nom encadrant
              </label>
              <input
                value={form.supervisorName}
                onChange={(e) => handleFormChange('supervisorName', e.target.value)}
                placeholder="Ex: Dr Sonia Trabelsi"
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#1F4E79]"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Email encadrant
              </label>
              <input
                type="email"
                value={form.supervisorEmail}
                onChange={(e) => handleFormChange('supervisorEmail', e.target.value)}
                placeholder="Ex: encadrant@email.com"
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#1F4E79]"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Département
              </label>
              <select
                value={form.department}
                onChange={(e) => handleFormChange('department', e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#1F4E79]"
              >
                <option>Génie Informatique</option>
                <option>Génie électrique</option>
                <option>Génie industriel</option>
              </select>
            </div>

            <div className="col-span-2">
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Titre du PFE *
              </label>
              <input
                value={form.title}
                onChange={(e) => handleFormChange('title', e.target.value)}
                placeholder="Ex: Plateforme de suivi des PFE"
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#1F4E79]"
              />
            </div>

            <div className="col-span-2">
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Description
              </label>
              <textarea
                value={form.description}
                onChange={(e) => handleFormChange('description', e.target.value)}
                rows={3}
                placeholder="Décrivez brièvement votre projet..."
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#1F4E79]"
              />
            </div>

            <div className="col-span-2">
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Problématique
              </label>
              <textarea
                value={form.problemStatement}
                onChange={(e) => handleFormChange('problemStatement', e.target.value)}
                rows={3}
                placeholder="Quel problème le projet cherche à résoudre ?"
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#1F4E79]"
              />
            </div>

            <div className="col-span-2">
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Objectifs
              </label>
              <textarea
                value={form.objectives}
                onChange={(e) => handleFormChange('objectives', e.target.value)}
                rows={3}
                placeholder="Quels sont les objectifs principaux ?"
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#1F4E79]"
              />
            </div>

            <div className="col-span-2">
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Technologies
              </label>
              <input
                value={form.technologies}
                onChange={(e) => handleFormChange('technologies', e.target.value)}
                placeholder="Ex: React, Spring Boot, MySQL, Gemini"
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#1F4E79]"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Date début
              </label>
              <input
                type="date"
                value={form.startDate}
                onChange={(e) => handleFormChange('startDate', e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#1F4E79]"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Date soutenance
              </label>
              <input
                type="date"
                value={form.defenseDate}
                onChange={(e) => handleFormChange('defenseDate', e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#1F4E79]"
              />
            </div>
          </div>

          <div className="flex justify-end mt-6">
            <button
              onClick={handleCreatePfe}
              disabled={creating}
              className="flex items-center gap-2 px-6 py-3 bg-[#1F4E79] text-white rounded-lg hover:bg-[#163A5C] disabled:opacity-50"
            >
              {creating ? (
                <>
                  <Loader2 size={18} className="animate-spin" />
                  Création...
                </>
              ) : (
                <>
                  <PlusCircle size={18} />
                  Créer mon PFE
                </>
              )}
            </button>
          </div>
        </div>
      </div>
    );
  }

  const progress = Math.round(pfe.progress || 0);

  return (
    <div className="space-y-6">
      <div className="bg-white rounded-lg p-6 border border-gray-200">
        <h2 className="text-2xl font-bold text-gray-800 mb-4">
          {pfe.title}
        </h2>

        <div className="grid grid-cols-2 gap-4 mb-6">
          <div>
            <p className="text-sm text-gray-600">Étudiant</p>
            <p className="font-medium">
              {pfe.studentName || `ID étudiant : ${pfe.studentId}`}
            </p>
            {pfe.studentEmail && (
              <p className="text-sm text-gray-500">{pfe.studentEmail}</p>
            )}
          </div>

          <div>
            <p className="text-sm text-gray-600">Encadrant</p>
            <p className="font-medium">
              {pfe.supervisorName || `ID encadrant : ${pfe.supervisorId}`}
            </p>
            {pfe.supervisorEmail && (
              <p className="text-sm text-gray-500">{pfe.supervisorEmail}</p>
            )}
          </div>

          <div>
            <p className="text-sm text-gray-600">Département</p>
            <p className="font-medium">{pfe.department || 'Non défini'}</p>
          </div>

          <div>
            <p className="text-sm text-gray-600">Statut</p>
            <p className="font-medium">{pfe.status}</p>
          </div>

          <div>
            <p className="text-sm text-gray-600">Début</p>
            <p className="font-medium">{formatDate(pfe.startDate)}</p>
          </div>

          <div>
            <p className="text-sm text-gray-600">Soutenance prévue</p>
            <p className="font-medium">{formatDate(pfe.defenseDate)}</p>
          </div>
        </div>

        {pfe.description && (
          <div className="mb-6 bg-gray-50 rounded-lg p-4">
            <p className="text-sm text-gray-600 mb-1">Description</p>
            <p className="text-gray-800">{pfe.description}</p>
          </div>
        )}

        <div>
          <div className="flex items-center justify-between mb-2">
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
        <h3 className="text-lg font-semibold mb-6">Timeline des jalons</h3>

        {milestones.length === 0 ? (
          <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
            <p className="text-blue-900 font-medium">Aucun jalon pour le moment</p>
            <p className="text-sm text-blue-800 mt-1">
              Va dans l’Assistant IA, choisis “Jalons”, génère une proposition puis clique sur “Ajouter toutes au projet”.
            </p>
          </div>
        ) : (
          <div className="space-y-4">
            {milestones.map((milestone) => {
              const statusLabel = mapMilestoneStatus(milestone.status);
              const milestoneProgress = Math.round(milestone.progress || 0);

              return (
                <div key={milestone.id} className="border-l-4 border-[#1F4E79] pl-6 relative">
                  <div
                    className={`absolute left-[-8px] top-2 w-4 h-4 rounded-full border-2 border-[#1F4E79] ${
                      statusLabel === 'Terminé'
                        ? 'bg-green-500'
                        : statusLabel === 'En cours'
                        ? 'bg-amber-500'
                        : 'bg-gray-300'
                    }`}
                  ></div>

                  <div className="bg-gray-50 rounded-lg p-4">
                    <button
                      onClick={() =>
                        setExpandedMilestone(expandedMilestone === milestone.id ? null : milestone.id)
                      }
                      className="w-full flex items-center justify-between"
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

                      <div className="flex items-center gap-4">
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

                        <div className="relative w-12 h-12">
                          <svg className="transform -rotate-90 w-12 h-12">
                            <circle cx="24" cy="24" r="20" stroke="#E5E7EB" strokeWidth="4" fill="none" />
                            <circle
                              cx="24"
                              cy="24"
                              r="20"
                              stroke={statusLabel === 'Terminé' ? '#1D9E75' : '#BA7517'}
                              strokeWidth="4"
                              fill="none"
                              strokeDasharray={`${milestoneProgress * 1.26} ${100 * 1.26}`}
                              strokeLinecap="round"
                            />
                          </svg>
                          <div className="absolute inset-0 flex items-center justify-center text-xs font-bold">
                            {milestoneProgress}%
                          </div>
                        </div>
                      </div>
                    </button>

                    {expandedMilestone === milestone.id && (
                      <div className="mt-4 pt-4 border-t border-gray-200 space-y-3">
                        {milestone.description && (
                          <p className="text-sm text-gray-700">{milestone.description}</p>
                        )}

                        {milestone.expectedDeliverable && (
                          <p className="text-sm text-gray-600">
                            Livrable attendu : <span className="font-medium">{milestone.expectedDeliverable}</span>
                          </p>
                        )}

                        {milestone.weight && (
                          <p className="text-sm text-gray-600">
                            Poids : <span className="font-medium">{milestone.weight}%</span>
                          </p>
                        )}

                        {milestone.tasks.length > 0 ? (
                          <div className="space-y-2">
                            {milestone.tasks.map((task) => (
                              <div
                                key={task.id}
                                className="flex items-center justify-between p-2 bg-white rounded border border-gray-200"
                              >
                                <span className="text-sm text-gray-800">{task.title}</span>
                                <span
                                  className={`text-xs px-2 py-1 rounded-full ${
                                    task.status === 'VALIDATED'
                                      ? 'bg-green-100 text-green-700'
                                      : task.status === 'SUBMITTED'
                                      ? 'bg-blue-100 text-blue-700'
                                      : task.status === 'IN_PROGRESS'
                                      ? 'bg-amber-100 text-amber-700'
                                      : 'bg-gray-100 text-gray-700'
                                  }`}
                                >
                                  {mapTaskStatus(task.status)}
                                </span>
                              </div>
                            ))}
                          </div>
                        ) : (
                          <p className="text-sm text-gray-500">Aucune tâche pour ce jalon</p>
                        )}

                        {statusLabel === 'Terminé' && (
                          <button className="flex items-center gap-2 text-sm text-[#1F4E79] hover:underline">
                            <Download size={16} />
                            Télécharger le livrable
                          </button>
                        )}
                      </div>
                    )}
                  </div>
                </div>
              );
            })}
          </div>
        )}
      </div>

      <div className="bg-white rounded-lg p-6 border border-gray-200">
        <button className="flex items-center justify-between w-full mb-4">
          <h3 className="text-lg font-semibold">Fiche Projet</h3>
          <ChevronRight size={20} />
        </button>

        <div className="bg-gray-50 p-4 rounded-lg space-y-2">
          {pfe.objectives && (
            <p className="text-sm text-gray-700">
              <span className="font-medium">Objectifs :</span> {pfe.objectives}
            </p>
          )}

          {pfe.problemStatement && (
            <p className="text-sm text-gray-700">
              <span className="font-medium">Problématique :</span> {pfe.problemStatement}
            </p>
          )}

          {pfe.technologies && (
            <p className="text-sm text-gray-700">
              <span className="font-medium">Technologies :</span> {pfe.technologies}
            </p>
          )}
        </div>
      </div>
    </div>
  );
}