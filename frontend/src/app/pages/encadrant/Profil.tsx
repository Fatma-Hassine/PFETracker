import { useEffect, useState } from "react";
import { apiRequest } from "../../../services/api";

type Profil = {
  id: number;
  nomComplet?: string;
  email?: string;
  role?: string;
  specialite?: string;
  grade?: string;
};

export function EncadrantProfil() {
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
      <h2 className="text-2xl font-bold mb-4">Profil Encadrant</h2>

      <p><strong>Nom :</strong> {profil.nomComplet}</p>
      <p><strong>Email :</strong> {profil.email}</p>
      <p><strong>Rôle :</strong> {profil.role}</p>
      <p><strong>Spécialité :</strong> {profil.specialite}</p>
      <p><strong>Grade :</strong> {profil.grade}</p>
    </div>
  );
}