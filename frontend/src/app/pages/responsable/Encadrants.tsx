import { useEffect, useState } from "react";
import { apiRequest } from "../../../services/api";
import { useMonDepartementId } from "../../hooks/useMonDepartementId";
import { toast } from "sonner";

type Encadrant = {
  id: number;
  nomComplet?: string;
  email?: string;
  chargeActuelle?: number;
  limite?: number;
};

export function ResponsableEncadrants() {
  const deptId = useMonDepartementId();
  const [encadrants, setEncadrants] = useState<Encadrant[]>([]);
  const [limite, setLimite] = useState(5);

  const charger = () => {
    if (!deptId) return;
    apiRequest<Encadrant[]>(`/responsable/${deptId}/encadrants/charge`)
      .then(setEncadrants)
      .catch(console.error);
  };

  useEffect(() => {
    charger();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [deptId]);

  const configurerLimite = async () => {
    if (!deptId) return;
    await apiRequest<void>(`/responsable/${deptId}/limite-etudiants?limite=${limite}`, {
      method: "PATCH",
    });

    toast.success("Limite modifiée");
    charger();
  };

  const regenererCode = async (encadrantId: number) => {
    await apiRequest<void>(`/responsable/encadrants/${encadrantId}/regenerer-code`, {
      method: "POST",
    });

    toast.success("Code régénéré");
  };

  return (
    <div className="space-y-4">
      <h1 className="text-2xl font-bold">Encadrants</h1>

      <div className="bg-white border rounded p-4 flex gap-2">
        <input
          type="number"
          className="border rounded p-2"
          value={limite}
          onChange={(e) => setLimite(Number(e.target.value))}
        />
        <button onClick={configurerLimite} className="bg-[#1F4E79] text-white px-4 rounded">
          Configurer limite
        </button>
      </div>

      {encadrants.map((enc) => (
        <div key={enc.id} className="bg-white border rounded p-4 flex justify-between">
          <div>
            <p className="font-semibold">{enc.nomComplet}</p>
            <p>{enc.email}</p>
            <p className="text-sm text-gray-600">
              Charge : {enc.chargeActuelle ?? 0}/{enc.limite ?? limite}
            </p>
          </div>

          <button onClick={() => regenererCode(enc.id)} className="bg-amber-500 text-white px-3 py-1 rounded">
            Régénérer code
          </button>
        </div>
      ))}
    </div>
  );
}