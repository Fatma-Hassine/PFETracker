import { useEffect, useState } from "react";
import { apiRequest } from "../../../services/api";

type Utilisateur = {
  id: number;
  nomComplet?: string;
  email?: string;
  role?: string;
};

export function AdminAffectations() {
  const [comptes, setComptes] = useState<Utilisateur[]>([]);
  const [selected, setSelected] = useState<Record<number, number>>({});

  useEffect(() => {
    apiRequest<Utilisateur[]>("/admin/comptes").then(setComptes).catch(console.error);
  }, []);

  const etudiants = comptes.filter((c) => c.role === "ROLE_ETUDIANT");
  const encadrants = comptes.filter((c) => c.role === "ROLE_ENCADRANT");

  const affecter = async (etudiantId: number) => {
    const encadrantId = selected[etudiantId];

    await apiRequest<void>(
      `/admin/affectations/forcer?etudiantId=${etudiantId}&encadrantId=${encadrantId}`,
      { method: "POST" }
    );

    alert("Affectation effectuée");
  };

  return (
    <div className="space-y-4">
      <h1 className="text-2xl font-bold">Affectations</h1>

      {etudiants.map((e) => (
        <div key={e.id} className="bg-white border rounded p-4 flex justify-between">
          <div>
            <p className="font-semibold">{e.nomComplet}</p>
            <p>{e.email}</p>
          </div>

          <div className="flex gap-2">
            <select
              className="border rounded px-2"
              onChange={(ev) => setSelected({ ...selected, [e.id]: Number(ev.target.value) })}
            >
              <option value="">Encadrant</option>
              {encadrants.map((enc) => (
                <option key={enc.id} value={enc.id}>{enc.nomComplet}</option>
              ))}
            </select>

            <button onClick={() => affecter(e.id)} className="bg-[#1F4E79] text-white px-3 py-1 rounded">
              Affecter
            </button>
          </div>
        </div>
      ))}
    </div>
  );
}