import { useEffect, useState } from "react";
import { apiRequest } from "../../../services/api";

type DirecteurDashboard = {
  totalEtudiants?: number;
  totalNonAffectes?: number;
  totalEnAlerte?: number;
  stagesExpires?: number;
  stagesProchesExpiration?: number;
};

export function DirecteurDashboard() {
  const [dashboard, setDashboard] = useState<DirecteurDashboard | null>(null);

  useEffect(() => {
    apiRequest<DirecteurDashboard>("/directeur/dashboard")
      .then(setDashboard)
      .catch(console.error);
  }, []);

  if (!dashboard) {
    return <p className="text-gray-600">Chargement...</p>;
  }

  return (
    <div className="space-y-6">
      <h2 className="text-2xl font-bold">Dashboard Directeur</h2>

      <div className="grid grid-cols-4 gap-6">
        <Card title="Étudiants" value={dashboard.totalEtudiants ?? 0} />
        <Card title="Non affectés" value={dashboard.totalNonAffectes ?? 0} />
        <Card title="En alerte" value={dashboard.totalEnAlerte ?? 0} />
        <Card title="Stages expirés" value={dashboard.stagesExpires ?? 0} />
      </div>
    </div>
  );
}

function Card({ title, value }: { title: string; value: number }) {
  return (
    <div className="bg-white rounded-lg p-6 border border-gray-200">
      <h3 className="text-gray-600">{title}</h3>
      <p className="text-3xl font-bold text-gray-800 mt-3">{value}</p>
    </div>
  );
}