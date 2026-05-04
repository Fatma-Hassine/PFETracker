import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { apiRequest } from "../../../services/api";

type EtudiantDetail = {
  id: number;
  nomComplet?: string;
  email?: string;
  sujet?: string;
  progression?: number;
  dateDebutStage?: string;
  dateFinStage?: string;
};

export function EncadrantEtudiantDetail() {
  const { id } = useParams();
  const [etudiant, setEtudiant] = useState<EtudiantDetail | null>(null);

  useEffect(() => {
    apiRequest<EtudiantDetail>(`/encadrant/etudiants/${id}`)
      .then(setEtudiant)
      .catch(console.error);
  }, [id]);

  if (!etudiant) {
    return <p className="text-gray-600">Chargement...</p>;
  }

  return (
    <div className="bg-white rounded-lg p-6 border border-gray-200">
      <h2 className="text-2xl font-bold mb-4">{etudiant.nomComplet}</h2>

      <p><strong>Email :</strong> {etudiant.email}</p>
      <p><strong>Sujet :</strong> {etudiant.sujet}</p>
      <p><strong>Progression :</strong> {etudiant.progression ?? 0}%</p>
      <p><strong>Début :</strong> {etudiant.dateDebutStage}</p>
      <p><strong>Fin :</strong> {etudiant.dateFinStage}</p>
    </div>
  );
}