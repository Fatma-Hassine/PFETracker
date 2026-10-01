import { useEffect, useState } from "react";
import { apiRequest } from "../../../services/api";
import { useMonDepartementId } from "../../hooks/useMonDepartementId";

type Dashboard = {
  totalEtudiants: number;
  etudiantsSansEncadrant: number;
  totalEncadrants: number;
  limiteEtudiantsParEncadrant: number;
  pfeActifs: number;
  pfeTermines: number;
  pfeEnRetard: number;
  pfeNonDemarres: number;
  progressionMoyenne: number;
  pfeInactifsSept: number;
  pfeStagnants: number;
};

export function ResponsableDashboard() {
  const deptId = useMonDepartementId();
  const [data, setData] = useState<Dashboard | null>(null);

  useEffect(() => {
    if (!deptId) return;
    apiRequest<Dashboard>(`/responsable/${deptId}/dashboard`).then(setData).catch(console.error);
  }, [deptId]);

  const telecharger = async (type: "excel" | "pdf") => {
    if (!deptId) return;
    const token = localStorage.getItem("token");
    const res = await fetch(`/api/responsable/${deptId}/export/${type}`, {
      headers: token ? { Authorization: `Bearer ${token}` } : {},
    });
    if (!res.ok) return;
    const blob = await res.blob();
    const url = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = type === "excel" ? "departement.xlsx" : "rapport-departement.pdf";
    a.click();
    URL.revokeObjectURL(url);
  };

  if (!deptId) return <p>Chargement du département...</p>;
  if (!data) return <p>Chargement...</p>;

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold">Dashboard Responsable</h1>
        <div className="flex gap-2">
          <button onClick={() => telecharger("excel")} className="border px-4 py-2 rounded bg-white hover:bg-gray-50">
            Export Excel
          </button>
          <button onClick={() => telecharger("pdf")} className="border px-4 py-2 rounded bg-white hover:bg-gray-50">
            Export PDF
          </button>
        </div>
      </div>

      <div className="grid grid-cols-4 gap-4">
        <Card title="Étudiants" value={data.totalEtudiants} />
        <Card title="Encadrants" value={data.totalEncadrants} />
        <Card title="Sans encadrant" value={data.etudiantsSansEncadrant} accent="amber" />
        <Card title="Limite étudiants/encadrant" value={data.limiteEtudiantsParEncadrant} />
      </div>

      <div className="grid grid-cols-4 gap-4">
        <Card title="PFE actifs" value={data.pfeActifs} />
        <Card title="PFE terminés" value={data.pfeTermines} />
        <Card title="PFE en retard" value={data.pfeEnRetard} accent="red" />
        <Card title="PFE non démarrés" value={data.pfeNonDemarres} />
      </div>

      <div className="grid grid-cols-3 gap-4">
        <Card title="Progression moyenne" value={`${Math.round(data.progressionMoyenne)}%`} />
        <Card title="PFE inactifs > 7 jours" value={data.pfeInactifsSept} accent="amber" />
        <Card title="PFE stagnants" value={data.pfeStagnants} accent="amber" />
      </div>
    </div>
  );
}

function Card({ title, value, accent }: { title: string; value: number | string; accent?: "red" | "amber" }) {
  const color = accent === "red" ? "text-red-600" : accent === "amber" ? "text-amber-600" : "text-gray-800";
  return (
    <div className="bg-white border rounded p-6">
      <p className="text-gray-600">{title}</p>
      <p className={`text-3xl font-bold ${color}`}>{value}</p>
    </div>
  );
}
