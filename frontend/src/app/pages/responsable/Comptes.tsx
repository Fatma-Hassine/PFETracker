import { useEffect, useState } from "react";
import { apiRequest } from "../../../services/api";

type Utilisateur = {
  id: number;
  nomComplet?: string;
  email?: string;
  role?: string;
};

export function ResponsableComptes() {
  const [comptes, setComptes] = useState<Utilisateur[]>([]);
  const deptId = localStorage.getItem("deptId") || "1";

  const charger = () => {
    apiRequest<Utilisateur[]>(`/api/responsable/${deptId}/comptes/en-attente`)
      .then(setComptes)
      .catch(console.error);
  };

  useEffect(() => {
    charger();
  }, []);

  const valider = async (id: number) => {
    await apiRequest<void>(`/api/responsable/comptes/${id}/valider`, {
      method: "POST",
    });

    alert("Compte validé");
    charger();
  };

  const refuser = async (id: number) => {
    const motif = prompt("Motif du refus") || "Refusé";

    await apiRequest<void>(
      `/api/responsable/comptes/${id}/refuser?motif=${encodeURIComponent(motif)}`,
      { method: "POST" }
    );

    alert("Compte refusé");
    charger();
  };

  return (
    <div className="space-y-4">
      <h1 className="text-2xl font-bold">Comptes en attente</h1>

      {comptes.map((c) => (
        <div key={c.id} className="bg-white border rounded p-4 flex justify-between">
          <div>
            <p className="font-semibold">{c.nomComplet}</p>
            <p>{c.email}</p>
            <p className="text-sm text-gray-500">{c.role}</p>
          </div>

          <div className="flex gap-2">
            <button onClick={() => valider(c.id)} className="bg-green-500 text-white px-3 py-1 rounded">
              Valider
            </button>
            <button onClick={() => refuser(c.id)} className="bg-red-500 text-white px-3 py-1 rounded">
              Refuser
            </button>
          </div>
        </div>
      ))}

      {comptes.length === 0 && <p className="text-gray-600">Aucun compte en attente</p>}
    </div>
  );
}