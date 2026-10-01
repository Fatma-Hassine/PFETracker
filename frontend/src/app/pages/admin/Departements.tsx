import { useEffect, useState } from "react";
import { apiRequest } from "../../../services/api";
import { toast } from "sonner";

type Departement = {
  id: number;
  nom?: string;
  code?: string;
  chefNom?: string;
  nombreEtudiants: number;
  nombreEncadrants: number;
};

type Utilisateur = {
  id: number;
  nomComplet?: string;
  role?: string;
};

export function AdminDepartements() {
  const [departements, setDepartements] = useState<Departement[]>([]);
  const [chefs, setChefs] = useState<Utilisateur[]>([]);
  const [nom, setNom] = useState("");
  const [code, setCode] = useState("");
  const [edition, setEdition] = useState<Departement | null>(null);
  const [chefChoisi, setChefChoisi] = useState<Record<number, number>>({});

  const charger = () => {
    apiRequest<Departement[]>("/admin/departements").then(setDepartements).catch(console.error);
    apiRequest<Utilisateur[]>("/admin/comptes/role/ROLE_CHEF_DEPARTEMENT")
      .then(setChefs)
      .catch(console.error);
  };

  useEffect(() => {
    charger();
  }, []);

  const ajouter = async () => {
    if (!nom || !code) {
      toast.error("Nom et code obligatoires");
      return;
    }
    await apiRequest<void>("/admin/departements", {
      method: "POST",
      body: JSON.stringify({ nom, code }),
    });
    setNom("");
    setCode("");
    charger();
  };

  const modifier = async () => {
    if (!edition) return;
    await apiRequest<void>(`/admin/departements/${edition.id}`, {
      method: "PUT",
      body: JSON.stringify({ nom: edition.nom, code: edition.code }),
    });
    toast.success("Département modifié");
    setEdition(null);
    charger();
  };

  const supprimer = async (id: number) => {
    if (!confirm("Supprimer ce département ?")) return;
    try {
      await apiRequest<void>(`/admin/departements/${id}`, { method: "DELETE" });
      charger();
    } catch (e) {
      toast.error(e instanceof Error ? e.message : "Suppression impossible");
    }
  };

  const assignerChef = async (deptId: number) => {
    const userId = chefChoisi[deptId];
    if (!userId) {
      toast.error("Choisis un chef de département");
      return;
    }
    await apiRequest<void>(`/admin/departements/${deptId}/chef/${userId}`, { method: "POST" });
    toast.success("Chef de département assigné");
    charger();
  };

  return (
    <div className="space-y-4">
      <h1 className="text-2xl font-bold">Départements</h1>

      <div className="bg-white border rounded p-4 flex gap-2">
        <input className="border p-2 rounded flex-1" placeholder="Nom" value={nom} onChange={(e) => setNom(e.target.value)} />
        <input className="border p-2 rounded w-32" placeholder="Code" value={code} onChange={(e) => setCode(e.target.value)} />
        <button onClick={ajouter} className="bg-[#1F4E79] text-white px-4 rounded">Ajouter</button>
      </div>

      {departements.map((d) => (
        <div key={d.id} className="bg-white border rounded p-4 space-y-3">
          {edition?.id === d.id ? (
            <div className="flex gap-2">
              <input
                className="border p-2 rounded flex-1"
                value={edition.nom}
                onChange={(e) => setEdition({ ...edition, nom: e.target.value })}
              />
              <input
                className="border p-2 rounded w-32"
                value={edition.code}
                onChange={(e) => setEdition({ ...edition, code: e.target.value })}
              />
              <button onClick={modifier} className="bg-[#1D9E75] text-white px-3 py-1 rounded">Enregistrer</button>
              <button onClick={() => setEdition(null)} className="border px-3 py-1 rounded">Annuler</button>
            </div>
          ) : (
            <div className="flex justify-between items-start">
              <div>
                <p className="font-semibold">{d.nom} ({d.code})</p>
                <p className="text-sm text-gray-600">
                  Chef : {d.chefNom || "Aucun"} — {d.nombreEtudiants} étudiant(s), {d.nombreEncadrants} encadrant(s)
                </p>
              </div>
              <div className="flex gap-2">
                <button onClick={() => setEdition(d)} className="border px-3 py-1 rounded text-sm">Modifier</button>
                <button onClick={() => supprimer(d.id)} className="bg-red-500 text-white px-3 py-1 rounded text-sm">
                  Supprimer
                </button>
              </div>
            </div>
          )}

          <div className="flex gap-2 items-center pt-2 border-t">
            <select
              className="border rounded px-2 py-1 flex-1"
              onChange={(e) => setChefChoisi({ ...chefChoisi, [d.id]: Number(e.target.value) })}
            >
              <option value="">Assigner un chef de département...</option>
              {chefs.map((c) => (
                <option key={c.id} value={c.id}>{c.nomComplet}</option>
              ))}
            </select>
            <button onClick={() => assignerChef(d.id)} className="bg-[#1F4E79] text-white px-3 py-1 rounded text-sm">
              Assigner
            </button>
          </div>
        </div>
      ))}
    </div>
  );
}
