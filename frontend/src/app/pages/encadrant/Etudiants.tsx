import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router';
import { Loader2, Search, Users } from 'lucide-react';
import { toast } from 'sonner';
import { module2Api } from '../../api/module2Api';
import { Pfe } from '../../types/module2.types';

function getProgressColor(progress: number) {
  if (progress < 40) return 'bg-red-500';
  if (progress < 70) return 'bg-amber-500';
  return 'bg-green-500';
}

function formatDate(date?: string) {
  if (!date) return 'Non définie';
  return new Date(date).toLocaleDateString('fr-FR');
}

export function EncadrantEtudiants() {
  const navigate = useNavigate();
  const [etudiants, setEtudiants] = useState<Etudiant[]>([]);

  const [pfes, setPfes] = useState<Pfe[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');

  useEffect(() => {
    loadStudents();
  }, []);

  const loadStudents = async () => {
    try {
      setLoading(true);

      const data = await module2Api.getMyPfes();
      setPfes(data);
    } catch (error) {
      console.error(error);
      toast.error('Erreur lors du chargement des étudiants encadrés');
    } finally {
      setLoading(false);
    }
  };

  const filteredPfes = pfes.filter((pfe) => {
    const text = `${pfe.studentName || ''} ${pfe.title || ''} ${pfe.department || ''}`.toLowerCase();
    return text.includes(searchQuery.toLowerCase());
  });

  if (loading) {
    return (
      <div className="flex items-center justify-center py-20">
        <Loader2 className="animate-spin text-[#1F4E79]" size={32} />
      </div>
    );
  }

  if (pfes.length === 0) {
    return (
      <div className="bg-white rounded-lg p-12 border border-gray-200 text-center">
        <Users size={64} className="mx-auto text-gray-400 mb-4" />
        <h3 className="text-xl font-semibold mb-2">Aucun étudiant trouvé</h3>
        <p className="text-gray-600">
          Aucun PFE n’est affecté à cet encadrant pour le moment.
        </p>
        <p className="text-gray-500 text-sm mt-2">
          Vérifie que le fichier Excel contient un PFE avec supervisorId égal à l’utilisateur encadrant connecté.
        </p>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div className="bg-white rounded-lg p-6 border border-gray-200">
        <div className="flex items-center justify-between mb-6">
          <div>
            <h2 className="text-xl font-bold text-gray-800">Mes étudiants encadrés</h2>
            <p className="text-sm text-gray-600 mt-1">
              {pfes.length} PFE affecté(s) à votre encadrement
            </p>
          </div>

          <button
            onClick={loadStudents}
            className="px-4 py-2 bg-[#1F4E79] text-white rounded-lg hover:bg-[#163A5C]"
          >
            Actualiser
          </button>
        </div>

        <div className="relative mb-6">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" size={20} />
          <input
            type="text"
            placeholder="Rechercher par étudiant, PFE ou département..."
            value={searchQuery}
            onChange={(event) => setSearchQuery(event.target.value)}
            className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#1F4E79]"
          />
        </div>

        <div className="grid gap-4">
          {filteredPfes.map((pfe) => {
            const progress = Math.round(pfe.progress || 0);

            return (
              <div
                key={pfe.id}
                className="p-4 border border-gray-200 rounded-lg hover:shadow-md transition-shadow"
              >
                <div className="flex items-center justify-between gap-4">
                  <div className="flex-1">
                    <div className="flex items-center gap-3 mb-1">
                      <h3 className="font-semibold text-lg">
                        {pfe.studentName || `Étudiant ${pfe.studentId}`}
                      </h3>

                      {pfe.department && (
                        <span className="px-2 py-1 bg-blue-100 text-blue-700 rounded text-xs">
                          {pfe.department}
                        </span>
                      )}
                    </div>

                    {pfe.studentEmail && (
                      <p className="text-sm text-gray-500">{pfe.studentEmail}</p>
                    )}

                    <p className="text-gray-700 mt-2 font-medium">{pfe.title}</p>

                    <div className="grid grid-cols-2 gap-3 mt-3 text-sm text-gray-600">
                      <p>
                        <span className="font-medium">Début :</span> {formatDate(pfe.startDate)}
                      </p>
                      <p>
                        <span className="font-medium">Soutenance :</span> {formatDate(pfe.defenseDate)}
                      </p>
                      <p>
                        <span className="font-medium">Statut :</span> {pfe.status}
                      </p>
                      <p>
                        <span className="font-medium">Technologies :</span> {pfe.technologies || 'Non définies'}
                      </p>
                    </div>

                    <div className="flex items-center gap-2 mt-4">
                      <div className="flex-1 h-2 bg-gray-200 rounded-full overflow-hidden max-w-md">
                        <div
                          className={`h-full rounded-full ${getProgressColor(progress)}`}
                          style={{ width: `${progress}%` }}
                        ></div>
                      </div>
                      <span className="text-sm text-gray-600">{progress}%</span>
                    </div>
                  </div>

                  <button
                    onClick={() => navigate(`/encadrant/etudiants/${pfe.id}`)}
                    className="px-4 py-2 bg-[#1F4E79] text-white rounded-lg hover:bg-[#163A5C]"
                  >
                    Voir détails
                  </button>
                </div>
              </div>
            );
          })}

          {filteredPfes.length === 0 && (
            <div className="text-center py-10 text-gray-500">
              Aucun résultat pour cette recherche.
            </div>
          )}
        </div>
      </div>
    </div>
  );
}