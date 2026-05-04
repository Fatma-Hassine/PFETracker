import { useEffect, useState } from "react";
import { apiRequest } from "../../../services/api";

type Dashboard = {
  totalEtudiants?: number;
  totalEncadrants?: number;
  etudiantsSansEncadrant?: number;
  comptesEnAttente?: number;
};

export function ResponsableDashboard() {
  const [data, setData] = useState<Dashboard | null>(null);
  const deptId = localStorage.getItem("deptId") || "1";

  useEffect(() => {
    apiRequest<Dashboard>(`/api/responsable/${deptId}/dashboard`)
      .then(setData)
      .catch(console.error);
  }, [deptId]);

  if (!data) return <p>Chargement...</p>;

  return (
    <div className="space-y-4">
      <h1 className="text-2xl font-bold">Dashboard Responsable</h1>

      <div className="grid grid-cols-4 gap-4">
        <Card title="Étudiants" value={data.totalEtudiants ?? 0} />
        <Card title="Encadrants" value={data.totalEncadrants ?? 0} />
        <Card title="Sans encadrant" value={data.etudiantsSansEncadrant ?? 0} />
        <Card title="Comptes en attente" value={data.comptesEnAttente ?? 0} />
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