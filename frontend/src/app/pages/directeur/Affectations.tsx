import { useEffect, useState } from "react";
import { apiRequest } from "../../../services/api";
import { toast } from "sonner";

type Etudiant = {
  id: number;
  nomComplet?: string;
  email?: string;
  encadrantNom?: string;
};

type Encadrant = {
  id: number;
  nomComplet?: string;
  email?: string;
  departementNom?: string;
};

export function DirecteurAffectations() {
  const [nonAffectes, setNonAffectes] = useState<Etudiant[]>([]);
  const [tousLesEtudiants, setTousLesEtudiants] = useState<Etudiant[]>([]);
  const [encadrants, setEncadrants] = useState<Encadrant[]>([]);
  const [selected, setSelected] = useState<Record<number, number>>({});

  const charger = () => {
    apiRequest<Etudiant[]>("/directeur/etudiants/non-affectes")
      .then(setNonAffectes)
      .catch(console.error);

    apiRequest<Etudiant[]>("/directeur/etudiants")
      .then((tous) => setTousLesEtudiants(tous.filter((e) => e.encadrantNom)))
      .catch(console.error);

    apiRequest<Encadrant[]>("/directeur/encadrants")
      .then(setEncadrants)
      .catch(console.error);
  };

  useEffect(() => {
    charger();
  }, []);

  const affecter = async (etudiantId: number) => {
    const encadrantId = selected[etudiantId];

    if (!encadrantId) {
      toast.error("Choisis un encadrant");
      return;
    }

    await apiRequest<void>(
      `/directeur/affectations/affecter?etudiantId=${etudiantId}&encadrantId=${encadrantId}`,
      { method: "POST" }
    );

    toast.success("Affectation effectuée");
    charger();
  };

  const reaffecter = async (etudiantId: number) => {
    const encadrantId = selected[etudiantId];

    if (!encadrantId) {
      toast.error("Choisis un nouvel encadrant");
      return;
    }

    await apiRequest<void>(
      `/directeur/affectations/reaffecter?etudiantId=${etudiantId}&nouvelEncadrantId=${encadrantId}`,
      { method: "POST" }
    );

    toast.success("Étudiant réaffecté");
    charger();
  };

  const verifierAuto = async () => {
    await apiRequest<void>("/directeur/affectations/verifier", {
      method: "POST",
    });

    toast.success("Vérification automatique lancée");
    charger();
  };

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h1 className="text-2xl font-bold">Affectations Directeur</h1>

        <button onClick={verifierAuto} className="px-4 py-2 bg-[#1F4E79] text-white rounded">
          Vérifier affectations automatiques (délai dépassé)
        </button>
      </div>

      <div className="space-y-4">
        <h2 className="font-semibold text-lg">Étudiants non affectés</h2>
        {nonAffectes.map((e) => (
          <div key={e.id} className="bg-white border rounded p-4 flex justify-between items-center">
            <div>
              <p className="font-semibold">{e.nomComplet}</p>
              <p>{e.email}</p>
            </div>

            <div className="flex gap-2">
              <select
                className="border rounded px-2"
                onChange={(event) =>
                  setSelected({ ...selected, [e.id]: Number(event.target.value) })
                }
              >
                <option value="">Choisir encadrant</option>
                {encadrants.map((enc) => (
                  <option key={enc.id} value={enc.id}>
                    {enc.nomComplet} — {enc.departementNom}
                  </option>
                ))}
              </select>

              <button onClick={() => affecter(e.id)} className="px-4 py-2 bg-green-500 text-white rounded">
                Affecter
              </button>
            </div>
          </div>
        ))}
        {nonAffectes.length === 0 && <p className="text-gray-500">Aucun étudiant non affecté.</p>}
      </div>

      <div className="space-y-4">
        <h2 className="font-semibold text-lg">Réaffecter un étudiant déjà affecté</h2>
        {tousLesEtudiants.map((e) => (
          <div key={e.id} className="bg-white border rounded p-4 flex justify-between items-center">
            <div>
              <p className="font-semibold">{e.nomComplet}</p>
              <p className="text-sm text-gray-600">Encadrant actuel : {e.encadrantNom}</p>
            </div>

            <div className="flex gap-2">
              <select
                className="border rounded px-2"
                onChange={(event) =>
                  setSelected({ ...selected, [e.id]: Number(event.target.value) })
                }
              >
                <option value="">Choisir nouvel encadrant</option>
                {encadrants.map((enc) => (
                  <option key={enc.id} value={enc.id}>
                    {enc.nomComplet} — {enc.departementNom}
                  </option>
                ))}
              </select>

              <button onClick={() => reaffecter(e.id)} className="px-4 py-2 bg-amber-500 text-white rounded">
                Réaffecter
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
