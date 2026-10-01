import { useEffect, useState } from "react";
import { apiRequest } from "../../../services/api";

type DashboardGlobal = {
  totalEtudiants: number;
  totalEncadrants: number;
  pfeActifs: number;
  pfeEnRetard: number;
  pfesStagnants: number;
  comptesVerrouilles: number;
};

type LogAudit = {
  id: number;
  typeAction?: string;
  resultat?: string;
  horodatage?: string;
  details?: string;
  adresseIp?: string;
  utilisateurEmail?: string;
};

export function AdminSupervision() {
  const [dashboard, setDashboard] = useState<DashboardGlobal | null>(null);
  const [logs, setLogs] = useState<LogAudit[]>([]);

  useEffect(() => {
    apiRequest<DashboardGlobal>("/admin/dashboard").then(setDashboard).catch(console.error);
    apiRequest<LogAudit[]>("/admin/logs").then(setLogs).catch(console.error);
  }, []);

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold">Supervision globale</h1>

      <div className="grid grid-cols-3 gap-4">
        <Card title="Étudiants" value={dashboard?.totalEtudiants ?? 0} />
        <Card title="Encadrants" value={dashboard?.totalEncadrants ?? 0} />
        <Card title="PFE actifs" value={dashboard?.pfeActifs ?? 0} />
      </div>
      <div className="grid grid-cols-3 gap-4">
        <Card title="PFE en retard" value={dashboard?.pfeEnRetard ?? 0} accent="red" />
        <Card title="PFE stagnants" value={dashboard?.pfesStagnants ?? 0} accent="amber" />
        <Card title="Comptes verrouillés" value={dashboard?.comptesVerrouilles ?? 0} accent="red" />
      </div>

      <div className="bg-white border rounded-lg p-6">
        <h2 className="text-lg font-semibold mb-4">Derniers logs (toute la plateforme)</h2>

        {logs.length === 0 && <p className="text-gray-600">Aucun log trouvé</p>}

        <div className="space-y-3">
          {logs.slice(0, 15).map((log) => (
            <div key={log.id} className="border rounded p-3">
              <div className="flex justify-between">
                <p className="font-semibold">{log.typeAction}</p>
                <span
                  className={`text-xs px-2 py-0.5 rounded-full ${
                    log.resultat === "SUCCES"
                      ? "bg-green-100 text-green-700"
                      : "bg-red-100 text-red-700"
                  }`}
                >
                  {log.resultat}
                </span>
              </div>
              <p className="text-sm text-gray-600">{log.utilisateurEmail} — {log.adresseIp}</p>
              <p className="text-xs text-gray-500">
                {log.horodatage ? new Date(log.horodatage).toLocaleString("fr-FR") : ""}
              </p>
              {log.details && <p className="text-sm mt-1">{log.details}</p>}
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}

function Card({ title, value, accent }: { title: string; value: number; accent?: "red" | "amber" }) {
  const color = accent === "red" ? "text-red-600" : accent === "amber" ? "text-amber-600" : "text-gray-800";
  return (
    <div className="bg-white border rounded-lg p-6">
      <p className="text-gray-600">{title}</p>
      <p className={`text-3xl font-bold ${color}`}>{value}</p>
    </div>
  );
}
