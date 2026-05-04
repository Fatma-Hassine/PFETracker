import { useEffect, useState } from "react";
import { apiRequest } from "../../../services/api";

type Dashboard = {
  totalEtudiants?: number;
  totalEncadrants?: number;
  totalPfeActifs?: number;
  totalPfeEnRetard?: number;
  comptesEnAttente?: number;
};

export function AdminDashboard() {
  const [data, setData] = useState<Dashboard | null>(null);

  useEffect(() => {
    apiRequest<Dashboard>("/admin/dashboard").then(setData).catch(console.error);
  }, []);

  if (!data) return <p>Chargement...</p>;

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold">Dashboard Admin</h1>

      <div className="grid grid-cols-4 gap-4">
        <Card title="Étudiants" value={data.totalEtudiants ?? 0} />
        <Card title="Encadrants" value={data.totalEncadrants ?? 0} />
        <Card title="PFE actifs" value={data.totalPfeActifs ?? 0} />
        <Card title="PFE en retard" value={data.totalPfeEnRetard ?? 0} />
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