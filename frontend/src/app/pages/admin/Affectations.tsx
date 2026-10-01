import { useEffect, useState } from "react";
import { apiRequest } from "../../../services/api";
import { toast } from "sonner";

type Utilisateur = {
  id: number;
  nomComplet?: string;
  email?: string;
  role?: string;
};

type Departement = {
  id: number;
  nom?: string;
};

export function AdminAffectations() {
  const [comptes, setComptes] = useState<Utilisateur[]>([]);
  const [departements, setDepartements] = useState<Departement[]>([]);
  const [selected, setSelected] = useState<Record<number, number>>({});
  const [deptChoisi, setDeptChoisi] = useState<Record<number, number>>({});

  const charger = () => {
    apiRequest<Utilisateur[]>("/admin/comptes").then(setComptes).catch(console.error);
    apiRequest<Departement[]>("/admin/departements").then(setDepartements).catch(console.error);
  };

  useEffect(() => {
    charger();
  }, []);

  const etudiants = comptes.filter((c) => c.role === "ROLE_ETUDIANT");
  const encadrants = comptes.filter((c) => c.role === "ROLE_ENCADRANT");

  const affecter = async (etudiantId: number) => {
    const encadrantId = selected[etudiantId];
    if (!encadrantId) {
      toast.error("Choisis un encadrant");
      return;
    }

    await apiRequest<void>(
      `/admin/affectations/forcer?etudiantId=${etudiantId}&encadrantId=${encadrantId}`,
      { method: "POST" }
    );

    toast.success("Affectation effectuée");
  };

  const rompre = async (etudiantId: number) => {
    if (!confirm("Rompre l'affectation de cet étudiant ?")) return;
    await apiRequest<void>(`/admin/affectations/${etudiantId}`, { method: "DELETE" });
    toast.success("Affectation rompue");
  };

  const transferer = async (etudiantId: number) => {
    const deptId = deptChoisi[etudiantId];
    if (!deptId) {
      toast.error("Choisis un département de destination");
      return;
    }
    await apiRequest<void>(
      `/admin/etudiants/${etudiantId}/transfert?nouveauDeptId=${deptId}`,
      { method: "PATCH" }
    );
    toast.success("Étudiant transféré");
    charger();
  };

  return (
    <div className="space-y-4">
      <h1 className="text-2xl font-bold">Affectations</h1>

      {etudiants.map((e) => (
        <div key={e.id} className="bg-white border rounded p-4 space-y-3">
          <div>
            <p className="font-semibold">{e.nomComplet}</p>
            <p>{e.email}</p>
          </div>

          <div className="flex gap-2 flex-wrap">
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

            <button onClick={() => rompre(e.id)} className="bg-red-500 text-white px-3 py-1 rounded">
              Rompre
            </button>

            <select
              className="border rounded px-2"
              onChange={(ev) => setDeptChoisi({ ...deptChoisi, [e.id]: Number(ev.target.value) })}
            >
              <option value="">Transférer vers...</option>
              {departements.map((d) => (
                <option key={d.id} value={d.id}>{d.nom}</option>
              ))}
            </select>

            <button onClick={() => transferer(e.id)} className="bg-gray-600 text-white px-3 py-1 rounded">
              Transférer
            </button>
          </div>
        </div>
      ))}
    </div>
  );
}
