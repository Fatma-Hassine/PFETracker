import { useEffect, useState } from "react";
import { apiRequest } from "../../../services/api";

type Departement = {
  id: number;
  nom?: string;
  code?: string;
};

export function AdminDepartements() {
  const [departements, setDepartements] = useState<Departement[]>([]);
  const [nom, setNom] = useState("");
  const [code, setCode] = useState("");

  const charger = () => {
    apiRequest<Departement[]>("/admin/departements").then(setDepartements).catch(console.error);
  };

  useEffect(() => {
    charger();
  }, []);

  const ajouter = async () => {
    await apiRequest<void>("/admin/departements", {
      method: "POST",
      body: JSON.stringify({ nom, code }),
    });

    setNom("");
    setCode("");
    charger();
  };

  return (
    <div className="space-y-4">
      <h1 className="text-2xl font-bold">Départements</h1>

      <div className="bg-white border rounded p-4 flex gap-2">
        <input className="border p-2 rounded" placeholder="Nom" value={nom} onChange={(e) => setNom(e.target.value)} />
        <input className="border p-2 rounded" placeholder="Code" value={code} onChange={(e) => setCode(e.target.value)} />
        <button onClick={ajouter} className="bg-[#1F4E79] text-white px-4 rounded">Ajouter</button>
      </div>

      {departements.map((d) => (
        <div key={d.id} className="bg-white border rounded p-4">
          <p className="font-semibold">{d.nom}</p>
          <p>{d.code}</p>
        </div>
      ))}
    </div>
  );
}