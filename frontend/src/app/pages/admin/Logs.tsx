import { useEffect, useState } from "react";
import { apiRequest } from "../../../services/api";

type Log = {
  id: number;
  typeAction?: string;
  resultat?: string;
  horodatage?: string;
  details?: string;
};

export function AdminLogs() {
  const [logs, setLogs] = useState<Log[]>([]);

  useEffect(() => {
    apiRequest<Log[]>("/admin/logs").then(setLogs).catch(console.error);
  }, []);

  return (
    <div className="space-y-4">
      <h1 className="text-2xl font-bold">Logs audit</h1>

      {logs.map((log) => (
        <div key={log.id} className="bg-white border rounded p-4">
          <p className="font-semibold">{log.typeAction}</p>
          <p>{log.resultat}</p>
          <p className="text-sm text-gray-500">{log.horodatage}</p>
          <p>{log.details}</p>
        </div>
      ))}
    </div>
  );
}