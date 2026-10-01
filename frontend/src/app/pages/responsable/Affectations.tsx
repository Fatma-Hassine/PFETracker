import { useEffect, useRef, useState } from "react";
import { apiRequest } from "../../../services/api";
import { useMonDepartementId } from "../../hooks/useMonDepartementId";
import { toast } from "sonner";

type Etudiant = {
  id: number;
  nomComplet?: string;
  email?: string;
};

type Encadrant = {
  id: number;
  nomComplet?: string;
  email?: string;
  chargeActuelle?: number;
  limite?: number;
};

type ImportResult = {
  totalRows: number;
  createdCount: number;
  skippedCount: number;
  errorCount: number;
  messages: string[];
};

export function ResponsableAffectations() {
  const deptId = useMonDepartementId();
  const [etudiants, setEtudiants] = useState<Etudiant[]>([]);
  const [encadrants, setEncadrants] = useState<Encadrant[]>([]);
  const [selected, setSelected] = useState<Record<number, number>>({});
  const [resultatImport, setResultatImport] = useState<ImportResult | null>(null);
  const fichierRef = useRef<HTMLInputElement>(null);

  const charger = () => {
    if (!deptId) return;
    apiRequest<Etudiant[]>(`/responsable/${deptId}/etudiants/orphelins`)
      .then(setEtudiants)
      .catch(console.error);

    apiRequest<Encadrant[]>(`/responsable/${deptId}/encadrants/charge`)
      .then(setEncadrants)
      .catch(console.error);
  };

  useEffect(() => {
    charger();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [deptId]);

  const affecter = async (etudiantId: number) => {
    const encadrantId = selected[etudiantId];

    if (!encadrantId) {
      toast.error("Choisis un encadrant");
      return;
    }

    await apiRequest<void>(
      `/responsable/affectations/forcer?etudiantId=${etudiantId}&encadrantId=${encadrantId}`,
      { method: "POST" }
    );

    toast.success("Affectation effectuée");
    charger();
  };

  const rompre = async (etudiantId: number) => {
    await apiRequest<void>(`/responsable/affectations/${etudiantId}`, {
      method: "DELETE",
    });

    toast.success("Affectation rompue");
    charger();
  };

  const importerFichier = async () => {
    const fichier = fichierRef.current?.files?.[0];
    if (!fichier || !deptId) {
      toast.error("Choisis un fichier .xlsx ou .csv");
      return;
    }

    const formData = new FormData();
    formData.append("fichier", fichier);

    try {
      const token = localStorage.getItem("token");
      const res = await fetch(`/api/responsable/${deptId}/affectations/import`, {
        method: "POST",
        headers: token ? { Authorization: `Bearer ${token}` } : {},
        body: formData,
      });

      if (!res.ok) {
        throw new Error(await res.text());
      }

      const resultat: ImportResult = await res.json();
      setResultatImport(resultat);
      toast.success(`${resultat.createdCount} affectation(s) créée(s)`);
      charger();
    } catch (e) {
      toast.error(e instanceof Error ? e.message : "Échec de l'import");
    } finally {
      if (fichierRef.current) fichierRef.current.value = "";
    }
  };

  if (!deptId) return <p>Chargement du département...</p>;

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold">Affectations département</h1>

      <div className="bg-white border rounded p-4 space-y-3">
        <h2 className="font-semibold">Import en masse (fichier .xlsx ou .csv)</h2>
        <p className="text-sm text-gray-600">
          2 colonnes : email étudiant, email ou code encadrant.
        </p>
        <div className="flex gap-2">
          <input ref={fichierRef} type="file" accept=".xlsx,.csv" className="border rounded p-2 flex-1" />
          <button onClick={importerFichier} className="bg-[#1F4E79] text-white px-4 py-2 rounded">
            Importer
          </button>
        </div>

        {resultatImport && (
          <div className="text-sm bg-gray-50 rounded p-3 space-y-1">
            <p>
              {resultatImport.totalRows} ligne(s) — {resultatImport.createdCount} créée(s),{" "}
              {resultatImport.skippedCount} ignorée(s), {resultatImport.errorCount} erreur(s)
            </p>
            {resultatImport.messages.slice(0, 10).map((m, i) => (
              <p key={i} className="text-gray-600">{m}</p>
            ))}
          </div>
        )}
      </div>

      <div className="space-y-4">
        <h2 className="font-semibold">Affectation manuelle — étudiants sans encadrant</h2>

        {etudiants.map((e) => (
          <div key={e.id} className="bg-white border rounded p-4 flex justify-between">
            <div>
              <p className="font-semibold">{e.nomComplet}</p>
              <p>{e.email}</p>
            </div>

            <div className="flex gap-2">
              <select
                className="border rounded px-2"
                onChange={(event) =>
                  setSelected({ ...selected, [e.id]: Number(event.target.value) })
                }
              >
                <option value="">Encadrant</option>
                {encadrants.map((enc) => (
                  <option key={enc.id} value={enc.id}>
                    {enc.nomComplet || enc.email}
                  </option>
                ))}
              </select>

              <button onClick={() => affecter(e.id)} className="bg-green-500 text-white px-3 py-1 rounded">
                Affecter
              </button>

              <button onClick={() => rompre(e.id)} className="bg-red-500 text-white px-3 py-1 rounded">
                Rompre
              </button>
            </div>
          </div>
        ))}

        {etudiants.length === 0 && <p className="text-gray-500">Aucun étudiant sans encadrant.</p>}
      </div>
    </div>
  );
}
