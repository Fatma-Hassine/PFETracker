import { useEffect, useState } from "react";
import { apiRequest } from "../../../services/api";

type Reunion = {
  id: number;
  titre: string;
  dateReunion?: string;
  description?: string;
};

export function EncadrantReunions() {
  const [reunions, setReunions] = useState<Reunion[]>([]);

  useEffect(() => {
    apiRequest<Reunion[]>("/encadrant/reunions")
      .then(setReunions)
      .catch(console.error);
  }, []);

  return (
    <div className="space-y-6">
      <h2 className="text-2xl font-bold">Réunions</h2>

      <div className="space-y-3">
        {reunions.map((reunion) => (
          <div key={reunion.id} className="bg-white p-4 rounded-lg border border-gray-200">
            <p className="font-semibold">{reunion.titre}</p>
            <p className="text-sm text-gray-600">{reunion.dateReunion}</p>
            <p className="text-sm text-gray-500">{reunion.description}</p>
          </div>
        ))}
      </div>
    </div>
  );
}