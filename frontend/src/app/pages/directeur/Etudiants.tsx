import { useEffect, useState } from "react";
import { apiRequest } from "../../../services/api";

type Etudiant = {
  id: number;
  nomComplet?: string;
  email?: string;
  encadrantNom?: string;
  dateDebutStage?: string;
  dateFinStage?: string;
};

export function DirecteurEtudiants() {
  const [etudiants, setEtudiants] = useState<Etudiant[]>([]);
  const [filtre, setFiltre] = useState<"tous" | "non-affectes" | "alerte">("tous");

  const charger = () => {
    const endpoint =
      filtre === "non-affectes"
        ? "/directeur/etudiants/non-affectes"
        : filtre === "alerte"
        ? "/directeur/etudiants/alerte"
        : "/directeur/etudiants";

    apiRequest<Etudiant[]>(endpoint).then(setEtudiants).catch(console.error);
  };

  useEffect(() => {
    charger();
  }, [filtre]);

  return (
    <div className="space-y-4">
      <h1 className="text-2xl font-bold">Étudiants</h1>

      <div className="flex gap-2">
        <button onClick={() => setFiltre("tous")} className="px-4 py-2 bg-gray-200 rounded">Tous</button>
        <button onClick={() => setFiltre("non-affectes")} className="px-4 py-2 bg-amber-500 text-white rounded">Non affectés</button>
        <button onClick={() => setFiltre("alerte")} className="px-4 py-2 bg-red-500 text-white rounded">En alerte</button>
      </div>

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