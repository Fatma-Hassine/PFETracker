import { useEffect, useState } from "react";
import { apiRequest } from "../../../services/api";
import { toast } from "sonner";

type Validation = {
  id: number;
  titre?: string;
  etudiantNom?: string;
  statut?: string;
};

export function EncadrantValidation() {
  const [validations, setValidations] = useState<Validation[]>([]);

  useEffect(() => {
    chargerValidations();
  }, []);

  const chargerValidations = () => {
    apiRequest<Validation[]>("/encadrant/validations")
      .then(setValidations)
      .catch(console.error);
  };

  const valider = async (id: number) => {
    await apiRequest<void>(`/encadrant/validations/${id}/valider`, {
      method: "POST",
    });

    toast.success("Validation effectuée");
    chargerValidations();
  };

  return (
    <div className="space-y-6">
      <h2 className="text-2xl font-bold">Validations</h2>

      <div className="space-y-3">
        {validations.map((item) => (
          <div key={item.id} className="bg-white p-4 rounded-lg border border-gray-200 flex justify-between">
            <div>
              <p className="font-semibold">{item.titre}</p>
              <p className="text-sm text-gray-600">{item.etudiantNom}</p>
              <p className="text-sm text-gray-500">{item.statut}</p>
            </div>

            <button
              onClick={() => valider(item.id)}
              className="px-4 py-2 bg-green-500 text-white rounded-lg"
            >
              Valider
            </button>
          </div>
        ))}
      </div>
    </div>
  );
}