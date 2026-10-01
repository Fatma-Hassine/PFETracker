import { useEffect, useState } from "react";
import { apiRequest } from "../../../services/api";

type EtudiantStage = {
  id: number;
  nomComplet?: string;
  departementNom?: string;
  dateFinStage?: string;
};

type DirecteurDashboard = {
  totalEtudiants: number;
  etudiantsAffectes: number;
  etudiantsNonAffectes: number;
  etudiantsEnAlerte: number;
  stagesProchesExpiration: EtudiantStage[];
};

export function DirecteurDashboard() {
  const [dashboard, setDashboard] = useState<DirecteurDashboard | null>(null);

  useEffect(() => {
    apiRequest<DirecteurDashboard>("/directeur/dashboard")
      .then(setDashboard)
      .catch(console.error);
  }, []);

  const telecharger = async (type: "excel" | "pdf") => {
    const token = localStorage.getItem("token");
    const res = await fetch(`/api/directeur/export/${type}`, {
      headers: token ? { Authorization: `Bearer ${token}` } : {},
    });
    if (!res.ok) return;
    const blob = await res.blob();
    const url = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = type === "excel" ? "export-global.xlsx" : "rapport-global.pdf";
    a.click();
    URL.revokeObjectURL(url);
  };

  if (!dashboard) {
    return <p className="text-gray-600">Chargement...</p>;
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h2 className="text-2xl font-bold">Dashboard Directeur</h2>
        <div className="flex gap-2">
          <button onClick={() => telecharger("excel")} className="border px-4 py-2 rounded bg-white hover:bg-gray-50">
            Export Excel
          </button>
          <button onClick={() => telecharger("pdf")} className="border px-4 py-2 rounded bg-white hover:bg-gray-50">
            Rapport PDF
          </button>
        </div>
      </div>

      <div className="grid grid-cols-4 gap-6">
        <Card title="Étudiants" value={dashboard.totalEtudiants} />
        <Card title="Affectés" value={dashboard.etudiantsAffectes} />
        <Card title="Non affectés" value={dashboard.etudiantsNonAffectes} accent="amber" />
        <Card title="En alerte" value={dashboard.etudiantsEnAlerte} accent="red" />
      </div>

      <div className="bg-white rounded-lg p-6 border border-gray-200">
        <h3 className="text-lg font-semibold mb-4">Stages proches de l'expiration</h3>
        {dashboard.stagesProchesExpiration.length === 0 && (
          <p className="text-gray-500 text-sm">Aucun stage proche de l'expiration.</p>
        )}
        <div className="space-y-2">
          {dashboard.stagesProchesExpiration.map((e) => (
            <div key={e.id} className="p-3 bg-amber-50 border border-amber-200 rounded-lg flex justify-between">
              <span>{e.nomComplet} — {e.departementNom}</span>
              <span className="text-sm text-amber-700">{e.dateFinStage}</span>
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
    <div className="bg-white rounded-lg p-6 border border-gray-200">
      <h3 className="text-gray-600">{title}</h3>
      <p className={`text-3xl font-bold mt-3 ${color}`}>{value}</p>
    </div>
  );
}
