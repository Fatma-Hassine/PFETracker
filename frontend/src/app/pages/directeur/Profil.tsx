import { useEffect, useState } from "react";
import { apiRequest } from "../../../services/api";

type Profil = {
  id: number;
  nomComplet?: string;
  email?: string;
  role?: string;
};

export function DirecteurProfil() {
  const [profil, setProfil] = useState<Profil | null>(null);

  useEffect(() => {
    apiRequest<Profil>("/utilisateurs/moi")
      .then(setProfil)
      .catch(console.error);
  }, []);

  if (!profil) {
    return <p className="text-gray-600">Chargement du profil...</p>;
  }

  return (
    <div className="bg-white rounded-lg p-6 border border-gray-200">
      <h2 className="text-2xl font-bold mb-4">Profil Directeur</h2>

      <p><strong>Nom :</strong> {profil.nomComplet}</p>
      <p><strong>Email :</strong> {profil.email}</p>
      <p><strong>Rôle :</strong> {profil.role}</p>
    </div>
  );
}