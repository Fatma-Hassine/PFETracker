import { useEffect, useState } from "react";
import { apiRequest } from "../../../services/api";
import { toast } from "sonner";

type Utilisateur = {
  id: number;
  nomComplet?: string;
  email?: string;
  role?: string;
  enabled?: boolean;
  accountLocked?: boolean;
};

const ROLES = [
  "ROLE_ETUDIANT",
  "ROLE_ENCADRANT",
  "ROLE_CHEF_DEPARTEMENT",
  "ROLE_DIRECTEUR",
  "ROLE_SERVICE_STAGE",
  "ROLE_ADMIN",
];

export function AdminComptes() {
  const [comptes, setComptes] = useState<Utilisateur[]>([]);
  const [filtreRole, setFiltreRole] = useState("Tous");
  const [filtreVerrouilles, setFiltreVerrouilles] = useState(false);
  const [formulaireOuvert, setFormulaireOuvert] = useState(false);
  const [form, setForm] = useState({ email: "", nomComplet: "", role: "ROLE_ETUDIANT" });

  const charger = () => {
    const url = filtreVerrouilles
      ? "/admin/comptes/verrouilles"
      : filtreRole === "Tous"
      ? "/admin/comptes"
      : `/admin/comptes/role/${filtreRole}`;

    apiRequest<Utilisateur[]>(url).then(setComptes).catch(console.error);
  };

  useEffect(() => {
    charger();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [filtreRole, filtreVerrouilles]);

  const creerCompte = async () => {
    if (!form.email || !form.nomComplet) {
      toast.error("Email et nom complet obligatoires");
      return;
    }
    try {
      await apiRequest<void>("/admin/comptes", {
        method: "POST",
        body: JSON.stringify(form),
      });
      toast.success("Compte créé — mot de passe temporaire envoyé par email");
      setForm({ email: "", nomComplet: "", role: "ROLE_ETUDIANT" });
      setFormulaireOuvert(false);
      charger();
    } catch (e) {
      toast.error(e instanceof Error ? e.message : "Échec de la création du compte");
    }
  };

  const toggle = async (id: number, activer: boolean) => {
    await apiRequest<void>(`/admin/comptes/${id}/activation?activer=${activer}`, {
      method: "PATCH",
    });
    charger();
  };

  const deverrouiller = async (id: number) => {
    await apiRequest<void>(`/admin/comptes/${id}/deverrouiller`, { method: "POST" });
    toast.success("Compte déverrouillé");
    charger();
  };

  const reinitialiserMdp = async (id: number) => {
    await apiRequest<void>(`/admin/comptes/${id}/reinitialiser-mdp`, { method: "POST" });
    toast.success("Nouveau mot de passe temporaire envoyé par email");
  };

  const supprimer = async (id: number) => {
    if (!confirm("Supprimer définitivement ce compte ?")) return;
    await apiRequest<void>(`/admin/comptes/${id}`, { method: "DELETE" });
    charger();
  };

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold">Comptes</h1>
        <button
          onClick={() => setFormulaireOuvert(!formulaireOuvert)}
          className="bg-[#1F4E79] text-white px-4 py-2 rounded"
        >
          {formulaireOuvert ? "Annuler" : "Créer un compte"}
        </button>
      </div>

      {formulaireOuvert && (
        <div className="bg-white border rounded p-4 space-y-3">
          <input
            className="border rounded px-3 py-2 w-full"
            placeholder="Email institutionnel"
            value={form.email}
            onChange={(e) => setForm({ ...form, email: e.target.value })}
          />
          <input
            className="border rounded px-3 py-2 w-full"
            placeholder="Nom complet"
            value={form.nomComplet}
            onChange={(e) => setForm({ ...form, nomComplet: e.target.value })}
          />
          <select
            className="border rounded px-3 py-2 w-full"
            value={form.role}
            onChange={(e) => setForm({ ...form, role: e.target.value })}
          >
            {ROLES.map((r) => (
              <option key={r} value={r}>{r}</option>
            ))}
          </select>
          <button onClick={creerCompte} className="bg-[#1D9E75] text-white px-4 py-2 rounded">
            Créer
          </button>
        </div>
      )}

      <div className="flex gap-2">
        <select
          className="border rounded px-3 py-2"
          value={filtreRole}
          onChange={(e) => {
            setFiltreVerrouilles(false);
            setFiltreRole(e.target.value);
          }}
        >
          <option>Tous</option>
          {ROLES.map((r) => (
            <option key={r} value={r}>{r}</option>
          ))}
        </select>
        <button
          onClick={() => setFiltreVerrouilles(!filtreVerrouilles)}
          className={`px-3 py-2 rounded border ${filtreVerrouilles ? "bg-red-500 text-white" : "bg-white"}`}
        >
          Comptes verrouillés uniquement
        </button>
      </div>

      {comptes.map((c) => (
        <div key={c.id} className="bg-white border rounded p-4 flex justify-between items-start">
          <div>
            <p className="font-semibold">
              {c.nomComplet}
              {c.accountLocked && (
                <span className="ml-2 text-xs px-2 py-0.5 bg-red-100 text-red-700 rounded-full">Verrouillé</span>
              )}
            </p>
            <p>{c.email}</p>
            <p className="text-sm text-gray-500">{c.role}</p>
          </div>

          <div className="flex flex-wrap gap-2 justify-end max-w-md">
            {c.enabled ? (
              <button onClick={() => toggle(c.id, false)} className="bg-amber-500 text-white px-3 py-1 rounded text-sm">
                Désactiver
              </button>
            ) : (
              <button onClick={() => toggle(c.id, true)} className="bg-green-500 text-white px-3 py-1 rounded text-sm">
                Activer
              </button>
            )}
            {c.accountLocked && (
              <button onClick={() => deverrouiller(c.id)} className="bg-blue-500 text-white px-3 py-1 rounded text-sm">
                Déverrouiller
              </button>
            )}
            <button onClick={() => reinitialiserMdp(c.id)} className="bg-gray-500 text-white px-3 py-1 rounded text-sm">
              Réinitialiser mdp
            </button>
            <button onClick={() => supprimer(c.id)} className="bg-red-500 text-white px-3 py-1 rounded text-sm">
              Supprimer
            </button>
          </div>
        </div>
      ))}

      {comptes.length === 0 && <p className="text-gray-500">Aucun compte ne correspond aux filtres.</p>}
    </div>
  );
}
