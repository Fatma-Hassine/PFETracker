import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { apiRequest } from "../../../services/api";

type Etudiant = {
  id: number;
  nomComplet?: string;
  email?: string;
  sujet?: string;
  progression?: number;
};

export function EncadrantEtudiants() {
  const navigate = useNavigate();
  const [etudiants, setEtudiants] = useState<Etudiant[]>([]);

  useEffect(() => {
    apiRequest<Etudiant[]>("/encadrant/etudiants")
      .then(setEtudiants)
      .catch(console.error);
  }, []);

  return (
    <div className="space-y-6">
      <h2 className="text-2xl font-bold">Mes étudiants</h2>

      <div className="space-y-3">
        {etudiants.map((etudiant) => (
          <div key={etudiant.id} className="bg-white p-4 rounded-lg border border-gray-200 flex justify-between">
            <div>
              <p className="font-semibold">{etudiant.nomComplet}</p>
              <p className="text-sm text-gray-600">{etudiant.email}</p>
              <p className="text-sm text-gray-500">{etudiant.sujet}</p>
            </div>

            <button
              onClick={() => navigate(`/encadrant/etudiants/${etudiant.id}`)}
              className="px-4 py-2 bg-[#1F4E79] text-white rounded-lg"
            >
              Détails
            </button>
          </div>
        ))}
      </div>
    </div>
  );
}