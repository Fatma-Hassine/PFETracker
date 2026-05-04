import { useEffect, useState } from "react";
import { apiRequest } from "../../../services/api";

type Profil = {
  id: number;
  nomComplet?: string;
  email?: string;
  role?: string;
};

export function ResponsableProfil() {
  const [profil, setProfil] = useState<Profil | null>(null);

  useEffect(() => {
    apiRequest<Profil>("/api/utilisateurs/moi").then(setProfil).catch(console.error);
  }, []);

  if (!profil) return <p>Chargement...</p>;

  return (
    <div className="bg-white border rounded p-6">
      <h1 className="text-2xl font-bold mb-4">Profil Responsable</h1>
      <p><strong>Nom :</strong> {profil.nomComplet}</p>
      <p><strong>Email :</strong> {profil.email}</p>
      <p><strong>Rôle :</strong> {profil.role}</p>
    </div>
  );
}