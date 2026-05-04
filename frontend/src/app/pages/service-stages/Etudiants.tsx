import { useEffect, useState } from "react";
import { apiRequest } from "../../../services/api";

type Etudiant = {
  id: number;
  nomComplet?: string;
  email?: string;
  dateDebutStage?: string;
  dateFinStage?: string;
  encadrantNom?: string;
};

export function ServiceStagesEtudiants() {
  const [etudiants, setEtudiants] = useState<Etudiant[]>([]);

  useEffect(() => {
    apiRequest<Etudiant[]>("/service-stages/etudiants")
      .then(setEtudiants)
      .catch(console.error);
  }, []);

  return (
    <div className="space-y-4">
      <h1 className="text-2xl font-bold">Étudiants</h1>

      {etudiants.map((e) => (
        <div key={e.id} className="bg-white border rounded p-4">
          <p className="font-semibold">{e.nomComplet}</p>
          <p>{e.email}</p>
          <p className="text-sm text-gray-600">Encadrant : {e.encadrantNom || "Non affecté"}</p>
          <p className="text-sm text-gray-500">Stage : {e.dateDebutStage} → {e.dateFinStage}</p>
        </div>
      ))}
    </div>
  );
}