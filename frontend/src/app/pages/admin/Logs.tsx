import { useEffect, useState } from "react";
import { apiRequest } from "../../../services/api";

type Log = {
  id: number;
  typeAction?: string;
  resultat?: string;
  horodatage?: string;
  details?: string;
  adresseIp?: string;
  utilisateurEmail?: string;
};

export function AdminLogs() {
  const [logs, setLogs] = useState<Log[]>([]);
  const [filtreResultat, setFiltreResultat] = useState("Tous");
  const [recherche, setRecherche] = useState("");

  useEffect(() => {
    apiRequest<Log[]>("/admin/logs").then(setLogs).catch(console.error);
  }, []);

  const filtres = logs.filter((log) => {
    const matchResultat = filtreResultat === "Tous" || log.resultat === filtreResultat;
    const texte = `${log.typeAction || ""} ${log.utilisateurEmail || ""} ${log.details || ""}`.toLowerCase();
    const matchRecherche = texte.includes(recherche.toLowerCase());
    return matchResultat && matchRecherche;
  });

  return (
    <div className="space-y-4">
      <h1 className="text-2xl font-bold">Logs audit</h1>

      <div className="flex gap-2">
        <input
          className="border rounded px-3 py-2 flex-1"
          placeholder="Rechercher (action, email, détails)..."
          value={recherche}
          onChange={(e) => setRecherche(e.target.value)}
        />
        <select
          className="border rounded px-3 py-2"
          value={filtreResultat}
          onChange={(e) => setFiltreResultat(e.target.value)}
        >
          <option>Tous</option>
          <option>SUCCES</option>
          <option>ECHEC</option>
        </select>
      </div>

      {filtres.map((log) => (
        <div key={log.id} className="bg-white border rounded p-4">
          <div className="flex justify-between">
            <p className="font-semibold">{log.typeAction}</p>
            <span
              className={`text-xs px-2 py-0.5 rounded-full ${
                log.resultat === "SUCCES" ? "bg-green-100 text-green-700" : "bg-red-100 text-red-700"
              }`}
            >
              {log.resultat}
            </span>
          </div>
          <p className="text-sm text-gray-600">{log.utilisateurEmail} — IP : {log.adresseIp || "—"}</p>
          <p className="text-sm text-gray-500">
            {log.horodatage ? new Date(log.horodatage).toLocaleString("fr-FR") : ""}
          </p>
          {log.details && <p className="mt-1">{log.details}</p>}
        </div>
      ))}

      {filtres.length === 0 && <p className="text-gray-500">Aucun log ne correspond aux filtres.</p>}
    </div>
  );
}
