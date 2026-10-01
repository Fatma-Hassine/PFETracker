import { useEffect, useState } from "react";
import { apiRequest } from "../../../services/api";

type Etudiant = {
  id: number;
  nomComplet?: string;
  email?: string;
  departementNom?: string;
  encadrantNom?: string;
  dateDebutStage?: string;
  dateFinStage?: string;
  enAlerte?: boolean;
};

type Filtre = "tous" | "non-affectes" | "alerte" | "proches-expiration" | "expires";

const ENDPOINTS: Record<Filtre, string> = {
  tous: "/directeur/etudiants",
  "non-affectes": "/directeur/etudiants/non-affectes",
  alerte: "/directeur/etudiants/alerte",
  "proches-expiration": "/directeur/stages/proches-expiration",
  expires: "/directeur/stages/expires",
};

const LABELS: Record<Filtre, string> = {
  tous: "Tous",
  "non-affectes": "Non affectés",
  alerte: "En alerte",
  "proches-expiration": "Stages proches expiration",
  expires: "Stages expirés",
};

export function DirecteurEtudiants() {
  const [etudiants, setEtudiants] = useState<Etudiant[]>([]);
  const [filtre, setFiltre] = useState<Filtre>("tous");

  useEffect(() => {
    apiRequest<Etudiant[]>(ENDPOINTS[filtre]).then(setEtudiants).catch(console.error);
  }, [filtre]);

  return (
    <div className="space-y-4">
      <h1 className="text-2xl font-bold">Étudiants</h1>

      <div className="flex gap-2 flex-wrap">
        {(Object.keys(LABELS) as Filtre[]).map((f) => (
          <button
            key={f}
            onClick={() => setFiltre(f)}
            className={`px-4 py-2 rounded ${
              filtre === f ? "bg-[#1F4E79] text-white" : "bg-gray-200 text-gray-700"
            }`}
          >
            {LABELS[f]}
          </button>
        ))}
      </div>

      {etudiants.map((e) => (
        <div key={e.id} className="bg-white border rounded p-4 flex justify-between items-start">
          <div>
            <p className="font-semibold">
              {e.nomComplet}
              {e.enAlerte && (
                <span className="ml-2 text-xs px-2 py-0.5 bg-red-100 text-red-700 rounded-full">En alerte</span>
              )}
            </p>
            <p>{e.email}</p>
            <p className="text-sm text-gray-600">Département : {e.departementNom || "—"}</p>
            <p className="text-sm text-gray-600">Encadrant : {e.encadrantNom || "Non affecté"}</p>
            <p className="text-sm text-gray-500">Stage : {e.dateDebutStage || "—"} → {e.dateFinStage || "—"}</p>
          </div>
        </div>
      ))}

      {etudiants.length === 0 && <p className="text-gray-500">Aucun résultat.</p>}
    </div>
  );
}
