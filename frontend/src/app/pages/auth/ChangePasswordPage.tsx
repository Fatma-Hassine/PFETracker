import { useState } from "react";
import { apiRequest } from "../../../services/api";

export function ChangePasswordPage() {
  const [ancienMotDePasse, setAncienMotDePasse] = useState("");
  const [nouveauMotDePasse, setNouveauMotDePasse] = useState("");

  const submit = async (e: React.FormEvent) => {
    e.preventDefault();

    await apiRequest<void>("/auth/changer-mot-de-passe", {
      method: "POST",
      body: JSON.stringify({ ancienMotDePasse, nouveauMotDePasse }),
    });

    alert("Mot de passe changé");
  };

  return (
    <form onSubmit={submit} className="max-w-md mx-auto bg-white p-6 rounded border">
      <h1 className="text-xl font-bold mb-4">Changer mot de passe</h1>

      <input className="w-full border p-2 rounded mb-4" type="password" placeholder="Ancien mot de passe" value={ancienMotDePasse} onChange={(e) => setAncienMotDePasse(e.target.value)} />
      <input className="w-full border p-2 rounded mb-4" type="password" placeholder="Nouveau mot de passe" value={nouveauMotDePasse} onChange={(e) => setNouveauMotDePasse(e.target.value)} />

      <button className="bg-[#1F4E79] text-white px-4 py-2 rounded">
        Modifier
      </button>
    </form>
  );
}