import { useEffect, useState } from "react";
import { apiRequest } from "../../../services/api";

type Utilisateur = {
  id: number;
  nomComplet?: string;
  email?: string;
  role?: string;
  enabled?: boolean;
};

export function AdminComptes() {
  const [comptes, setComptes] = useState<Utilisateur[]>([]);

  const charger = () => {
    apiRequest<Utilisateur[]>("/admin/comptes").then(setComptes).catch(console.error);
  };

  useEffect(() => {
    charger();
  }, []);

  const toggle = async (id: number, activer: boolean) => {
    await apiRequest<void>(`/admin/comptes/${id}/activation?activer=${activer}`, {
      method: "PATCH",
    });
    charger();
  };

  const supprimer = async (id: number) => {
    await apiRequest<void>(`/admin/comptes/${id}`, { method: "DELETE" });
    charger();
  };

  return (
    <div className="space-y-4">
      <h1 className="text-2xl font-bold">Comptes</h1>

      {comptes.map((c) => (
        <div key={c.id} className="bg-white border rounded p-4 flex justify-between">
          <div>
            <p className="font-semibold">{c.nomComplet}</p>
            <p>{c.email}</p>
            <p className="text-sm text-gray-500">{c.role}</p>
          </div>

          <div className="flex gap-2">
            <button onClick={() => toggle(c.id, true)} className="bg-green-500 text-white px-3 py-1 rounded">Activer</button>
            <button onClick={() => toggle(c.id, false)} className="bg-amber-500 text-white px-3 py-1 rounded">Désactiver</button>
            <button onClick={() => supprimer(c.id)} className="bg-red-500 text-white px-3 py-1 rounded">Supprimer</button>
          </div>
        </div>
      ))}
    </div>
  );
}