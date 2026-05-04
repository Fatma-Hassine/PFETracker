import { useEffect, useState } from "react";
import { apiRequest } from "../../../services/api";

type Stage = {
  id: number;
  nomComplet?: string;
  email?: string;
  dateDebutStage?: string;
  dateFinStage?: string;
  encadrantNom?: string;
};

export function ServiceStagesStages() {
  const [stages, setStages] = useState<Stage[]>([]);
  const [filtre, setFiltre] = useState<"en-cours" | "proches-expiration" | "expires">("en-cours");

  useEffect(() => {
    apiRequest<Stage[]>(`/service-stages/stages/${filtre}`)
      .then(setStages)
      .catch(console.error);
  }, [filtre]);

  return (
    <div className="space-y-4">
      <h1 className="text-2xl font-bold">Stages</h1>

      <div className="flex gap-2">
        <button onClick={() => setFiltre("en-cours")} className="bg-blue-500 text-white px-3 py-1 rounded">
          En cours
        </button>
        <button onClick={() => setFiltre("proches-expiration")} className="bg-amber-500 text-white px-3 py-1 rounded">
          Proches expiration
        </button>
        <button onClick={() => setFiltre("expires")} className="bg-red-500 text-white px-3 py-1 rounded">
          Expirés
        </button>
      </div>

      {stages.map((s) => (
        <div key={s.id} className="bg-white border rounded p-4">
          <p className="font-semibold">{s.nomComplet}</p>
          <p>{s.email}</p>
          <p className="text-sm text-gray-600">Encadrant : {s.encadrantNom || "Non affecté"}</p>
          <p className="text-sm text-gray-500">Stage : {s.dateDebutStage} → {s.dateFinStage}</p>
        </div>
      ))}
    </div>
  );
}