import { useEffect, useState } from "react";
import { apiRequest } from "../../../services/api";

type Dashboard = {
  totalEtudiants?: number;
  totalEncadrants?: number;
  totalPfeActifs?: number;
  totalPfeEnRetard?: number;
  comptesEnAttente?: number;
};

type LogAudit = {
  id: number;
  typeAction?: string;
  resultat?: string;
  horodatage?: string;
  details?: string;
};

export function AdminSupervision() {
  const [dashboard, setDashboard] = useState<Dashboard | null>(null);
  const [logs, setLogs] = useState<LogAudit[]>([]);

  useEffect(() => {
    apiRequest<Dashboard>("/admin/dashboard").then(setDashboard).catch(console.error);
    apiRequest<LogAudit[]>("/admin/logs").then(setLogs).catch(console.error);
  }, []);

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold">Supervision globale</h1>

      <div className="grid grid-cols-4 gap-4">
        <Card title="Étudiants" value={dashboard?.totalEtudiants ?? 0} />
        <Card title="Encadrants" value={dashboard?.totalEncadrants ?? 0} />
        <Card title="PFE actifs" value={dashboard?.totalPfeActifs ?? 0} />
        <Card title="PFE en retard" value={dashboard?.totalPfeEnRetard ?? 0} />
      </div>

      <div className="bg-white border rounded-lg p-6">
        <h2 className="text-lg font-semibold mb-4">Derniers logs</h2>

        {logs.length === 0 && <p className="text-gray-600">Aucun log trouvé</p>}

        <div className="space-y-3">
          {logs.slice(0, 10).map((log) => (
            <div key={log.id} className="border rounded p-3">
              <p className="font-semibold">{log.typeAction}</p>
              <p className="text-sm text-gray-600">{log.resultat}</p>
              <p className="text-xs text-gray-500">{log.horodatage}</p>
              <p className="text-sm">{log.details}</p>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}

function Card({ title, value }: { title: string; value: number }) {
  return (
    <div className="bg-white border rounded-lg p-6">
      <p className="text-gray-600">{title}</p>
      <p className="text-3xl font-bold">{value}</p>
    </div>
  );
}