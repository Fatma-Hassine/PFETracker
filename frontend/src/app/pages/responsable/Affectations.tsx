import { useEffect, useState } from "react";
import { apiRequest } from "../../../services/api";

type Etudiant = {
  id: number;
  nomComplet?: string;
  email?: string;
};

type Encadrant = {
  id: number;
  nomComplet?: string;
  email?: string;
  chargeActuelle?: number;
  limite?: number;
};

export function ResponsableAffectations() {
  const [etudiants, setEtudiants] = useState<Etudiant[]>([]);
  const [encadrants, setEncadrants] = useState<Encadrant[]>([]);
  const [selected, setSelected] = useState<Record<number, number>>({});
  const deptId = localStorage.getItem("deptId") || "1";

  const charger = () => {
    apiRequest<Etudiant[]>(`/api/responsable/${deptId}/etudiants/orphelins`)
      .then(setEtudiants)
      .catch(console.error);

    apiRequest<Encadrant[]>(`/api/responsable/${deptId}/encadrants/charge`)
      .then(setEncadrants)
      .catch(console.error);
  };

  useEffect(() => {
    charger();
  }, []);

  const affecter = async (etudiantId: number) => {
    const encadrantId = selected[etudiantId];

    if (!encadrantId) {
      alert("Choisis un encadrant");
      return;
    }

    await apiRequest<void>(
      `/api/responsable/affectations/forcer?etudiantId=${etudiantId}&encadrantId=${encadrantId}`,
      { method: "POST" }
    );

    alert("Affectation effectuée");
    charger();
  };

  const rompre = async (etudiantId: number) => {
    await apiRequest<void>(`/api/responsable/affectations/${etudiantId}`, {
      method: "DELETE",
    });

    alert("Affectation rompue");
    charger();
  };

  return (
    <div className="space-y-4">
      <h1 className="text-2xl font-bold">Affectations département</h1>

      {etudiants.map((e) => (
        <div key={e.id} className="bg-white border rounded p-4 flex justify-between">
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
              <option value="">Encadrant</option>
              {encadrants.map((enc) => (
                <option key={enc.id} value={enc.id}>
                  {enc.nomComplet || enc.email}
                </option>
              ))}
            </select>

            <button onClick={() => affecter(e.id)} className="bg-green-500 text-white px-3 py-1 rounded">
              Affecter
            </button>

            <button onClick={() => rompre(e.id)} className="bg-red-500 text-white px-3 py-1 rounded">
              Rompre
            </button>
          </div>
        </div>
      ))}
    </div>
  );
}