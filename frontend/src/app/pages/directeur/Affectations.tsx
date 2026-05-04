import { useEffect, useState } from "react";
import { apiRequest } from "../../../services/api";

type Etudiant = {
  id: number;
  nomComplet?: string;
  email?: string;
};

type Utilisateur = {
  id: number;
  nomComplet?: string;
  email?: string;
  role?: string;
};

export function DirecteurAffectations() {
  const [etudiants, setEtudiants] = useState<Etudiant[]>([]);
  const [comptes, setComptes] = useState<Utilisateur[]>([]);
  const [selected, setSelected] = useState<Record<number, number>>({});

  const charger = () => {
    apiRequest<Etudiant[]>("/directeur/etudiants/non-affectes")
      .then(setEtudiants)
      .catch(console.error);

    apiRequest<Utilisateur[]>("/admin/comptes")
      .then(setComptes)
      .catch(console.error);
  };

  useEffect(() => {
    charger();
  }, []);

  const encadrants = comptes.filter((c) => c.role === "ROLE_ENCADRANT");

  const affecter = async (etudiantId: number) => {
    const encadrantId = selected[etudiantId];

    if (!encadrantId) {
      alert("Choisis un encadrant");
      return;
    }

    await apiRequest<void>(
      `/directeur/affectations/affecter?etudiantId=${etudiantId}&encadrantId=${encadrantId}`,
      { method: "POST" }
    );

    alert("Affectation effectuée");
    charger();
  };

  const verifierAuto = async () => {
    await apiRequest<void>("/directeur/affectations/verifier", {
      method: "POST",
    });

    alert("Vérification automatique lancée");
    charger();
  };

  return (
    <div className="space-y-4">
      <div className="flex justify-between items-center">
        <h1 className="text-2xl font-bold">Affectations Directeur</h1>

        <button onClick={verifierAuto} className="px-4 py-2 bg-[#1F4E79] text-white rounded">
          Vérifier affectations automatiques
        </button>
      </div>

      {etudiants.map((e) => (
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
                  {enc.nomComplet || enc.email}
                </option>
              ))}
            </select>

            <button onClick={() => affecter(e.id)} className="px-4 py-2 bg-green-500 text-white rounded">
              Affecter
            </button>
          </div>
        </div>
      ))}
    </div>
  );
}