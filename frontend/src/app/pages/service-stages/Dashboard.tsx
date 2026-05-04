import { useEffect, useState } from "react";
import { apiRequest } from "../../../services/api";

type Dashboard = {
  totalEtudiants?: number;
  stagesEnCours?: number;
  stagesProchesExpiration?: number;
  stagesExpires?: number;
};

export function ServiceStagesDashboard() {
  const [data, setData] = useState<Dashboard | null>(null);

  useEffect(() => {
    apiRequest<Dashboard>("/service-stages/dashboard")
      .then(setData)
      .catch(console.error);
  }, []);

  if (!data) return <p>Chargement...</p>;

  return (
    <div className="space-y-4">
      <h1 className="text-2xl font-bold">Dashboard Service des stages</h1>

      <div className="grid grid-cols-4 gap-4">
        <Card title="Étudiants" value={data.totalEtudiants ?? 0} />
        <Card title="Stages en cours" value={data.stagesEnCours ?? 0} />
        <Card title="Proches expiration" value={data.stagesProchesExpiration ?? 0} />
        <Card title="Expirés" value={data.stagesExpires ?? 0} />
      </div>
    </div>
  );
}

function Card({ title, value }: { title: string; value: number }) {
  return (
    <div className="bg-white border rounded p-6">
      <p className="text-gray-600">{title}</p>
      <p className="text-3xl font-bold">{value}</p>
    </div>
  );
}