import { useEffect, useState } from "react";
import { apiRequest } from "../../../services/api";
import { useMonDepartementId } from "../../hooks/useMonDepartementId";
import { toast } from "sonner";

type Utilisateur = {
  id: number;
  nomComplet?: string;
  email?: string;
  role?: string;
  enabled?: boolean;
  accountLocked?: boolean;
};

export function ResponsableComptes() {
  const deptId = useMonDepartementId();
  const [enAttente, setEnAttente] = useState<Utilisateur[]>([]);
  const [tousLesComptes, setTousLesComptes] = useState<Utilisateur[]>([]);

  const charger = () => {
    if (!deptId) return;
    apiRequest<Utilisateur[]>(`/responsable/${deptId}/comptes/en-attente`)
      .then(setEnAttente)
      .catch(console.error);
    apiRequest<Utilisateur[]>(`/responsable/${deptId}/comptes`)
      .then(setTousLesComptes)
      .catch(console.error);
  };

  useEffect(() => {
    charger();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [deptId]);

  const valider = async (id: number) => {
    await apiRequest<void>(`/responsable/comptes/${id}/valider`, { method: "POST" });
    toast.success("Compte validé");
    charger();
  };

  const refuser = async (id: number) => {
    const motif = prompt("Motif du refus") || "Refusé";
    await apiRequest<void>(
      `/responsable/comptes/${id}/refuser?motif=${encodeURIComponent(motif)}`,
      { method: "POST" }
    );
    toast.success("Compte refusé");
    charger();
  };

  const toggle = async (id: number, activer: boolean) => {
    await apiRequest<void>(`/responsable/comptes/${id}/activation?activer=${activer}`, {
      method: "PATCH",
    });
    charger();
  };

  const deverrouiller = async (id: number) => {
    await apiRequest<void>(`/responsable/comptes/${id}/deverrouiller`, { method: "POST" });
    toast.success("Compte déverrouillé");
    charger();
  };

  const reinitialiserMdp = async (id: number) => {
    await apiRequest<void>(`/responsable/comptes/${id}/reinitialiser-mdp`, { method: "POST" });
    toast.success("Nouveau mot de passe temporaire envoyé par email");
  };

  if (!deptId) return <p>Chargement du département...</p>;

  return (
    <div className="space-y-8">
      <div className="space-y-4">
        <h1 className="text-2xl font-bold">Comptes en attente de validation</h1>
        <p className="text-sm text-gray-600">
          Étudiants et encadrants ne peuvent accéder à l'application qu'après validation ici (confidentialité — cahier §3.1).
        </p>

        {enAttente.map((c) => (
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

        {enAttente.length === 0 && <p className="text-gray-600">Aucun compte en attente</p>}
      </div>

      <div className="space-y-4">
        <h2 className="text-xl font-bold">Tous les comptes du département</h2>

        {tousLesComptes.map((c) => (
          <div key={c.id} className="bg-white border rounded p-4 flex justify-between items-start">
            <div>
              <p className="font-semibold">
                {c.nomComplet}
                {c.accountLocked && (
                  <span className="ml-2 text-xs px-2 py-0.5 bg-red-100 text-red-700 rounded-full">Verrouillé</span>
                )}
                {!c.enabled && (
                  <span className="ml-2 text-xs px-2 py-0.5 bg-gray-100 text-gray-700 rounded-full">Inactif</span>
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
            </div>
          </div>
        ))}

        {tousLesComptes.length === 0 && <p className="text-gray-600">Aucun compte dans ce département</p>}
      </div>
    </div>
  );
}
