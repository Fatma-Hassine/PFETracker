import { useEffect, useState } from "react";
import { apiRequest } from "../../../services/api";

type DashboardEncadrant = {
  totalEtudiants?: number;
  reunionsProchaines?: number;
  tachesEnAttente?: number;
  messagesNonLus?: number;
};

export function EncadrantDashboard() {
  const [dashboard, setDashboard] = useState<DashboardEncadrant | null>(null);

  useEffect(() => {
    apiRequest<DashboardEncadrant>("/encadrant/dashboard")
      .then(setDashboard)
      .catch(console.error);
  }, []);

  if (!dashboard) {
    return <p className="text-gray-600">Chargement...</p>;
  }

  return (
    <div className="space-y-6">
      <h2 className="text-2xl font-bold">Dashboard Encadrant</h2>

      <div className="grid grid-cols-4 gap-6">
        <Card title="Étudiants" value={dashboard.totalEtudiants ?? 0} />
        <Card title="Réunions" value={dashboard.reunionsProchaines ?? 0} />
        <Card title="Tâches" value={dashboard.tachesEnAttente ?? 0} />
        <Card title="Messages" value={dashboard.messagesNonLus ?? 0} />
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