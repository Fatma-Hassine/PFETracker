import { useState } from "react";
import { apiRequest } from "../../../services/api";

export function ForgotPasswordPage() {
  const [email, setEmail] = useState("");

  const submit = async (e: React.FormEvent) => {
    e.preventDefault();

    await apiRequest<void>("/api/auth/mot-de-passe-oublie", {
      method: "POST",
      body: JSON.stringify({ email }),
    });

    alert("Email envoyé");
  };

  return (
    <form onSubmit={submit} className="max-w-md mx-auto bg-white p-6 rounded border">
      <h1 className="text-xl font-bold mb-4">Mot de passe oublié</h1>

      <input
        className="w-full border p-2 rounded mb-4"
        placeholder="Email"
        value={email}
        onChange={(e) => setEmail(e.target.value)}
      />

      <button className="bg-[#1F4E79] text-white px-4 py-2 rounded">
        Envoyer
      </button>
    </form>
  );
}