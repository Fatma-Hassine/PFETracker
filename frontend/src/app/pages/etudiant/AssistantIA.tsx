import { useEffect, useState } from 'react';
import {
  Sparkles,
  Wand2,
  FileText,
  CheckSquare,
  Users,
  Loader2,
  Milestone as MilestoneIcon,
} from 'lucide-react';
import { toast } from 'sonner';
import { module2Api } from '../../api/module2Api';
import {
  AiGeneratedItem,
  AiGenerationType,
  Milestone,
  Pfe,
  Priority,
} from '../../types/module2.types';

type GenerationTypeUi = 'jalons' | 'taches' | 'user-stories' | 'specifications';

interface EditableGeneratedItem extends AiGeneratedItem {
  id: number;
}

function mapGenerationTypeToApi(type: GenerationTypeUi): AiGenerationType {
  if (type === 'jalons') return 'MILESTONES';
  if (type === 'user-stories') return 'USER_STORIES';
  if (type === 'specifications') return 'SPECIFICATIONS';
  return 'TASKS';
}

function getPriorityLabel(priority: Priority) {
  if (priority === 'CRITICAL') return 'Critique';
  if (priority === 'HIGH') return 'Haute';
  if (priority === 'LOW') return 'Faible';
  return 'Normale';
}

function getTypeColor(priorityOrType: string) {
  if (priorityOrType === 'MILESTONE') return 'bg-teal-100 text-teal-700';
  if (priorityOrType === 'Haute' || priorityOrType === 'Critique') return 'bg-red-100 text-red-700';
  if (priorityOrType === 'Normale') return 'bg-blue-100 text-blue-700';
  if (priorityOrType === 'Faible') return 'bg-gray-100 text-gray-700';
  if (priorityOrType === 'USER_STORY') return 'bg-purple-100 text-purple-700';
  if (priorityOrType === 'SPECIFICATION') return 'bg-green-100 text-green-700';
  return 'bg-gray-100 text-gray-700';
}

function formatDate(date?: string) {
  if (!date) return '';
  return new Date(date).toLocaleDateString('fr-FR');
}

export function EtudiantAssistantIA() {
  const [prompt, setPrompt] = useState('');
  const [generationType, setGenerationType] = useState<GenerationTypeUi>('jalons');
  const [isGenerating, setIsGenerating] = useState(false);
  const [isAddingAll, setIsAddingAll] = useState(false);
  const [generatedItems, setGeneratedItems] = useState<EditableGeneratedItem[]>([]);

  const [pfe, setPfe] = useState<Pfe | null>(null);
  const [milestones, setMilestones] = useState<Milestone[]>([]);
  const [selectedMilestoneId, setSelectedMilestoneId] = useState<number | null>(null);

  const [editingId, setEditingId] = useState<number | null>(null);
  const [editTitle, setEditTitle] = useState('');
  const [editDescription, setEditDescription] = useState('');
  const [editExpectedDeliverable, setEditExpectedDeliverable] = useState('');
  const [editStartDate, setEditStartDate] = useState('');
  const [editEndDate, setEditEndDate] = useState('');
  const [editWeight, setEditWeight] = useState('');

  useEffect(() => {
    loadProjectContext();
  }, []);

  const loadProjectContext = async () => {
    try {
      const pfes = await module2Api.getMyPfes();

      if (pfes.length === 0) {
        setPfe(null);
        setMilestones([]);
        setSelectedMilestoneId(null);
        return;
      }

      const firstPfe = pfes[0];
      setPfe(firstPfe);

      const loadedMilestones = await module2Api.getMilestones(firstPfe.id);
      setMilestones(loadedMilestones);

      if (loadedMilestones.length > 0) {
        setSelectedMilestoneId(loadedMilestones[0].id);
      }
    } catch (error) {
      console.error(error);
      toast.error('Erreur lors du chargement du contexte PFE');
    }
  };

  const handleGenerate = async () => {
    if (!prompt.trim()) {
      toast.error('Veuillez saisir une description');
      return;
    }

    if (!pfe) {
      toast.error('Aucun PFE chargé');
      return;
    }

    if (generationType !== 'jalons' && !selectedMilestoneId) {
      toast.error('Ajoute d’abord des jalons avant de générer des tâches');
      return;
    }

    setIsGenerating(true);
    setGeneratedItems([]);

    try {
      const response = await module2Api.generateAiItems({
        type: mapGenerationTypeToApi(generationType),
        prompt,
        pfeId: pfe.id,
        milestoneId: selectedMilestoneId || undefined,
      });

      const items = response.items.map((item, index) => ({
        ...item,
        id: index + 1,
      }));

      setGeneratedItems(items);
      toast.success(response.message || 'Génération terminée avec succès');
    } catch (error) {
      console.error(error);
      toast.error('Erreur pendant la génération IA');
    } finally {
      setIsGenerating(false);
    }
  };

  const startEdit = (item: EditableGeneratedItem) => {
    setEditingId(item.id);
    setEditTitle(item.title);
    setEditDescription(item.description || '');
    setEditExpectedDeliverable(item.expectedDeliverable || '');
    setEditStartDate(item.plannedStartDate || '');
    setEditEndDate(item.plannedEndDate || '');
    setEditWeight(item.weight ? String(item.weight) : '');
  };

  const saveEdit = (itemId: number) => {
    setGeneratedItems((currentItems) =>
      currentItems.map((item) =>
        item.id === itemId
          ? {
              ...item,
              title: editTitle,
              description: editDescription,
              expectedDeliverable:
                item.itemType === 'MILESTONE' ? editExpectedDeliverable : item.expectedDeliverable,
              plannedStartDate:
                item.itemType === 'MILESTONE' ? editStartDate : item.plannedStartDate,
              plannedEndDate:
                item.itemType === 'MILESTONE' ? editEndDate : item.plannedEndDate,
              weight:
                item.itemType === 'MILESTONE' && editWeight
                  ? Number(editWeight)
                  : item.weight,
            }
          : item
      )
    );

    setEditingId(null);
    setEditTitle('');
    setEditDescription('');
    setEditExpectedDeliverable('');
    setEditStartDate('');
    setEditEndDate('');
    setEditWeight('');

    toast.success('Suggestion modifiée');
  };

  const handleAddOne = async (item: EditableGeneratedItem) => {
    if (!pfe) {
      toast.error('Aucun PFE sélectionné');
      return;
    }

    try {
      if (generationType === 'jalons') {
        await module2Api.createMilestone(pfe.id, {
          title: item.title,
          description: item.description,
          expectedDeliverable: item.expectedDeliverable,
          plannedStartDate: item.plannedStartDate,
          plannedEndDate: item.plannedEndDate,
          weight: item.weight,
        });

        toast.success('Jalon ajouté au PFE');
        await loadProjectContext();
      } else {
        if (!selectedMilestoneId) {
          toast.error('Aucun jalon sélectionné');
          return;
        }

        await module2Api.addAiItem({
          milestoneId: selectedMilestoneId,
          title: item.title,
          description: item.description,
          priority: item.priority,
          estimatedHours: item.estimatedHours,
          itemType: item.itemType,
        });

        toast.success('Élément ajouté au projet');
      }
    } catch (error) {
      console.error(error);
      toast.error('Erreur lors de l’ajout au projet');
    }
  };

  const handleAddAll = async () => {
    if (!pfe) {
      toast.error('Aucun PFE sélectionné');
      return;
    }

    if (generatedItems.length === 0) {
      toast.error('Aucun élément à ajouter');
      return;
    }

    setIsAddingAll(true);

    try {
      if (generationType === 'jalons') {
        await module2Api.createMilestonesBulk(
          pfe.id,
          generatedItems.map((item) => ({
            title: item.title,
            description: item.description,
            expectedDeliverable: item.expectedDeliverable,
            plannedStartDate: item.plannedStartDate,
            plannedEndDate: item.plannedEndDate,
            weight: item.weight,
          }))
        );

        toast.success('Les jalons ont été ajoutés au PFE');
        await loadProjectContext();
      } else {
        if (!selectedMilestoneId) {
          toast.error('Aucun jalon sélectionné');
          return;
        }

        await module2Api.addAllAiItems(
          generatedItems.map((item) => ({
            milestoneId: selectedMilestoneId,
            title: item.title,
            description: item.description,
            priority: item.priority,
            estimatedHours: item.estimatedHours,
            itemType: item.itemType,
          }))
        );

        toast.success('Tous les éléments ont été ajoutés au projet');
      }
    } catch (error) {
      console.error(error);
      toast.error('Erreur lors de l’ajout des éléments');
    } finally {
      setIsAddingAll(false);
    }
  };

  const technologies = pfe?.technologies
    ? pfe.technologies.split(',').map((tech) => tech.trim()).filter(Boolean)
    : [];

  const selectedMilestone = milestones.find((milestone) => milestone.id === selectedMilestoneId);

  return (
    <div className="space-y-6">
      <div className="bg-gradient-to-r from-[#1F4E79] to-[#1D9E75] rounded-lg p-6 text-white">
        <div className="flex items-center gap-3 mb-3">
          <Sparkles size={32} />
          <h2 className="text-2xl font-bold">Assistant IA pour votre PFE</h2>
        </div>
        <p className="text-blue-100">
          Générez automatiquement des jalons, tâches, user stories et spécifications pour accélérer votre projet
        </p>
      </div>

      <div className="bg-white rounded-lg p-6 border border-gray-200">
        <h3 className="font-semibold mb-3">Contexte du projet</h3>

        <div className="bg-gray-50 rounded-lg p-4 mb-4">
          <p className="font-medium text-gray-800">
            {pfe?.title || 'Aucun PFE chargé'}
          </p>

          {pfe && (
            <>
              <p className="text-sm text-gray-600 mt-1">
                Étudiant : {pfe.studentName || pfe.studentId}
              </p>
              <p className="text-sm text-gray-600 mt-1">
                Encadrant : {pfe.supervisorName || pfe.supervisorId}
              </p>
              <p className="text-sm text-gray-600 mt-1">
                Département : {pfe.department || 'Non défini'}
              </p>
            </>
          )}

          {generationType !== 'jalons' && (
            <p className="text-sm text-gray-600 mt-1">
              Jalon sélectionné: {selectedMilestone?.title || 'Aucun jalon'}
            </p>
          )}

          <div className="flex flex-wrap gap-2 mt-2">
            {technologies.length > 0 ? (
              technologies.map((tech) => (
                <span key={tech} className="px-2 py-1 bg-blue-100 text-blue-700 rounded text-xs">
                  {tech}
                </span>
              ))
            ) : (
              <span className="text-xs text-gray-500">Aucune technologie renseignée</span>
            )}
          </div>

          {generationType !== 'jalons' && milestones.length > 0 && (
            <div className="mt-4">
              <label className="block text-sm text-gray-600 mb-1">Choisir le jalon d’ajout</label>
              <select
                value={selectedMilestoneId || ''}
                onChange={(e) => setSelectedMilestoneId(Number(e.target.value))}
                className="px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#1F4E79]"
              >
                {milestones.map((milestone) => (
                  <option key={milestone.id} value={milestone.id}>
                    {milestone.orderIndex}. {milestone.title}
                  </option>
                ))}
              </select>
            </div>
          )}

          {generationType !== 'jalons' && milestones.length === 0 && (
            <div className="mt-4 bg-amber-50 border border-amber-200 rounded-lg p-3">
              <p className="text-sm text-amber-800">
                Aucun jalon trouvé. Génère d’abord les jalons avec l’IA, puis ajoute les tâches.
              </p>
            </div>
          )}
        </div>
      </div>

      <div className="bg-white rounded-lg p-6 border border-gray-200">
        <h3 className="font-semibold mb-4">Générer avec l'IA</h3>

        <div className="grid grid-cols-4 gap-3 mb-6">
          <button
            onClick={() => setGenerationType('jalons')}
            className={`p-4 rounded-lg border-2 transition-all ${
              generationType === 'jalons'
                ? 'border-[#1F4E79] bg-blue-50'
                : 'border-gray-200 hover:border-gray-300'
            }`}
          >
            <MilestoneIcon size={24} className={generationType === 'jalons' ? 'text-[#1F4E79]' : 'text-gray-600'} />
            <p className="font-medium mt-2">Jalons</p>
            <p className="text-xs text-gray-500">Générer le planning</p>
          </button>

          <button
            onClick={() => setGenerationType('taches')}
            className={`p-4 rounded-lg border-2 transition-all ${
              generationType === 'taches'
                ? 'border-[#1F4E79] bg-blue-50'
                : 'border-gray-200 hover:border-gray-300'
            }`}
          >
            <CheckSquare size={24} className={generationType === 'taches' ? 'text-[#1F4E79]' : 'text-gray-600'} />
            <p className="font-medium mt-2">Tâches</p>
            <p className="text-xs text-gray-500">Générer des tâches</p>
          </button>

          <button
            onClick={() => setGenerationType('user-stories')}
            className={`p-4 rounded-lg border-2 transition-all ${
              generationType === 'user-stories'
                ? 'border-[#1F4E79] bg-blue-50'
                : 'border-gray-200 hover:border-gray-300'
            }`}
          >
            <Users size={24} className={generationType === 'user-stories' ? 'text-[#1F4E79]' : 'text-gray-600'} />
            <p className="font-medium mt-2">User Stories</p>
            <p className="text-xs text-gray-500">Créer des stories</p>
          </button>

          <button
            onClick={() => setGenerationType('specifications')}
            className={`p-4 rounded-lg border-2 transition-all ${
              generationType === 'specifications'
                ? 'border-[#1F4E79] bg-blue-50'
                : 'border-gray-200 hover:border-gray-300'
            }`}
          >
            <FileText size={24} className={generationType === 'specifications' ? 'text-[#1F4E79]' : 'text-gray-600'} />
            <p className="font-medium mt-2">Spécifications</p>
            <p className="text-xs text-gray-500">Rédiger des specs</p>
          </button>
        </div>

        <div className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Décrivez ce que vous voulez générer
            </label>

            <textarea
              value={prompt}
              onChange={(e) => setPrompt(e.target.value)}
              rows={4}
              placeholder={
                generationType === 'jalons'
                  ? 'Ex: Générer les jalons pour un projet React Spring Boot de suivi des PFE'
                  : generationType === 'taches'
                  ? 'Ex: Générer les tâches pour développer le backend Spring Boot'
                  : generationType === 'user-stories'
                  ? 'Ex: Créer des user stories pour un système de suivi PFE'
                  : 'Ex: Rédiger les spécifications pour une API REST de gestion des tâches'
              }
              className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#1F4E79]"
            ></textarea>
          </div>

          <button
            onClick={handleGenerate}
            disabled={isGenerating}
            className="w-full flex items-center justify-center gap-2 px-6 py-3 bg-[#1F4E79] text-white rounded-lg hover:bg-[#163A5C] disabled:opacity-50 disabled:cursor-not-allowed"
          >
            {isGenerating ? (
              <>
                <Loader2 size={20} className="animate-spin" />
                Génération en cours...
              </>
            ) : (
              <>
                <Wand2 size={20} />
                Générer avec l'IA
              </>
            )}
          </button>
        </div>
      </div>

      {generatedItems.length > 0 && (
        <div className="bg-white rounded-lg p-6 border border-gray-200">
          <div className="flex items-center justify-between mb-4">
            <h3 className="font-semibold">
              Résultats générés ({generatedItems.length})
            </h3>

            <button
              onClick={handleAddAll}
              disabled={isAddingAll}
              className="px-4 py-2 bg-green-500 text-white rounded-lg hover:bg-green-600 disabled:opacity-50"
            >
              {isAddingAll ? 'Ajout...' : generationType === 'jalons' ? 'Ajouter tous les jalons' : 'Ajouter toutes au projet'}
            </button>
          </div>

          <div className="space-y-3">
            {generatedItems.map((item) => {
              const badgeLabel =
                item.itemType === 'TASK'
                  ? getPriorityLabel(item.priority)
                  : item.itemType;

              return (
                <div key={item.id} className="p-4 border border-gray-200 rounded-lg hover:shadow-md transition-shadow">
                  <div className="flex items-start justify-between gap-4">
                    <div className="flex-1">
                      <div className="flex items-center gap-2 mb-2">
                        <span className={`px-2 py-1 rounded text-xs font-medium ${getTypeColor(badgeLabel)}`}>
                          {badgeLabel}
                        </span>

                        {item.estimatedHours && (
                          <span className="text-xs text-gray-500">{item.estimatedHours}h estimées</span>
                        )}
                      </div>

                      {editingId === item.id ? (
                        <div className="space-y-2">
                          <input
                            value={editTitle}
                            onChange={(e) => setEditTitle(e.target.value)}
                            className="w-full px-3 py-2 border border-gray-300 rounded-lg"
                            placeholder="Titre"
                          />

                          <textarea
                            value={editDescription}
                            onChange={(e) => setEditDescription(e.target.value)}
                            rows={3}
                            className="w-full px-3 py-2 border border-gray-300 rounded-lg"
                            placeholder="Description"
                          />

                          {item.itemType === 'MILESTONE' && (
                            <div className="grid grid-cols-2 gap-2">
                              <input
                                value={editExpectedDeliverable}
                                onChange={(e) => setEditExpectedDeliverable(e.target.value)}
                                className="px-3 py-2 border border-gray-300 rounded-lg col-span-2"
                                placeholder="Livrable attendu"
                              />

                              <input
                                type="date"
                                value={editStartDate}
                                onChange={(e) => setEditStartDate(e.target.value)}
                                className="px-3 py-2 border border-gray-300 rounded-lg"
                              />

                              <input
                                type="date"
                                value={editEndDate}
                                onChange={(e) => setEditEndDate(e.target.value)}
                                className="px-3 py-2 border border-gray-300 rounded-lg"
                              />

                              <input
                                type="number"
                                value={editWeight}
                                onChange={(e) => setEditWeight(e.target.value)}
                                className="px-3 py-2 border border-gray-300 rounded-lg col-span-2"
                                placeholder="Poids"
                              />
                            </div>
                          )}
                        </div>
                      ) : (
                        <>
                          <p className="font-medium text-gray-800">{item.title}</p>

                          {item.description && (
                            <p className="text-sm text-gray-600 mt-1">{item.description}</p>
                          )}

                          {item.itemType === 'MILESTONE' && (
                            <div className="text-sm text-gray-600 mt-2 space-y-1">
                              {item.expectedDeliverable && (
                                <p>
                                  <span className="font-medium">Livrable attendu :</span>{' '}
                                  {item.expectedDeliverable}
                                </p>
                              )}

                              {item.plannedStartDate && item.plannedEndDate && (
                                <p>
                                  <span className="font-medium">Période :</span>{' '}
                                  {formatDate(item.plannedStartDate)} - {formatDate(item.plannedEndDate)}
                                </p>
                              )}

                              {item.weight && (
                                <p>
                                  <span className="font-medium">Poids :</span> {item.weight}%
                                </p>
                              )}
                            </div>
                          )}
                        </>
                      )}
                    </div>

                    <div className="flex gap-2">
                      {editingId === item.id ? (
                        <button
                          onClick={() => saveEdit(item.id)}
                          className="px-3 py-1 text-sm bg-blue-100 text-blue-700 rounded hover:bg-blue-200"
                        >
                          Enregistrer
                        </button>
                      ) : (
                        <button
                          onClick={() => startEdit(item)}
                          className="px-3 py-1 text-sm bg-blue-100 text-blue-700 rounded hover:bg-blue-200"
                        >
                          Modifier
                        </button>
                      )}

                      <button
                        onClick={() => handleAddOne(item)}
                        className="px-3 py-1 text-sm bg-green-100 text-green-700 rounded hover:bg-green-200"
                      >
                        Ajouter
                      </button>
                    </div>
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      )}

      <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
        <h4 className="font-medium text-blue-900 mb-2">💡 Conseils</h4>
        <ul className="text-sm text-blue-800 space-y-1 list-disc list-inside">
          <li>Importe d’abord le PFE depuis le fichier Excel côté backend.</li>
          <li>Génère ensuite les jalons depuis l’assistant IA.</li>
          <li>Ajoute les jalons au projet.</li>
          <li>Après les jalons, tu peux générer des tâches pour chaque jalon.</li>
        </ul>
      </div>
    </div>
  );
}