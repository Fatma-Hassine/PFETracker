import { useEffect, useState } from "react";
import { apiRequest } from "../../../services/api";

type DashboardGlobal = {
  totalEtudiants: number;
  totalEncadrants: number;
  totalDepartements: number;
  etudiantsNonAffectes: number;
  etudiantsAffectes: number;
  affectationsForcees: number;
  stagesEnCours: number;
  stagesTermines: number;
  stagesEnRetard: number;
  stagesProchesExpiration: number;
  pfeActifs: number;
  pfeTermines: number;
  pfeEnRetard: number;
  pfeNonDemarres: number;
  progressionMoyenneGlobale: number;
  comptesVerrouilles: number;
  pfesStagnants: number;
  notificationsNonLues: number;
};

export function AdminDashboard() {
  const [data, setData] = useState<DashboardGlobal | null>(null);

  useEffect(() => {
    apiRequest<DashboardGlobal>("/admin/dashboard").then(setData).catch(console.error);
  }, []);

  if (!data) return <p>Chargement...</p>;

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold">Dashboard Admin</h1>

      <div className="grid grid-cols-4 gap-4">
        <Card title="Étudiants" value={data.totalEtudiants} />
        <Card title="Encadrants" value={data.totalEncadrants} />
        <Card title="Départements" value={data.totalDepartements} />
        <Card title="Progression moyenne" value={`${Math.round(data.progressionMoyenneGlobale)}%`} />
      </div>

      <div className="grid grid-cols-4 gap-4">
        <Card title="Étudiants affectés" value={data.etudiantsAffectes} />
        <Card title="Étudiants non affectés" value={data.etudiantsNonAffectes} />
        <Card title="Affectations forcées" value={data.affectationsForcees} />
        <Card title="Comptes verrouillés" value={data.comptesVerrouilles} accent="red" />
      </div>

      <div className="grid grid-cols-4 gap-4">
        <Card title="PFE actifs" value={data.pfeActifs} />
        <Card title="PFE terminés" value={data.pfeTermines} />
        <Card title="PFE en retard" value={data.pfeEnRetard} accent="red" />
        <Card title="PFE non démarrés" value={data.pfeNonDemarres} />
      </div>

      <div className="grid grid-cols-4 gap-4">
        <Card title="Stages en cours" value={data.stagesEnCours} />
        <Card title="Stages terminés" value={data.stagesTermines} />
        <Card title="Stages en retard" value={data.stagesEnRetard} accent="red" />
        <Card title="Stages proches expiration" value={data.stagesProchesExpiration} accent="amber" />
      </div>

      <div className="grid grid-cols-2 gap-4">
        <Card title="PFE stagnants" value={data.pfesStagnants} accent="amber" />
        <Card title="Notifications non lues (système)" value={data.notificationsNonLues} />
      </div>
    </div>
  );
}

function Card({
  title,
  value,
  accent,
}: {
  title: string;
  value: number | string;
  accent?: "red" | "amber";
}) {
  const color = accent === "red" ? "text-red-600" : accent === "amber" ? "text-amber-600" : "text-gray-800";
  return (
    <div className="bg-white border rounded-lg p-6">
      <p className="text-gray-600">{title}</p>
      <p className={`text-3xl font-bold ${color}`}>{value}</p>
    </div>
  );
}
