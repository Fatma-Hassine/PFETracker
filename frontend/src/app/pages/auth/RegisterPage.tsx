import { useState } from "react";
import { apiRequest } from "../../../services/api";

export function RegisterPage() {
  const [nomComplet, setNomComplet] = useState("");
  const [email, setEmail] = useState("");
  const [role, setRole] = useState("ROLE_ETUDIANT");

  const register = async (e: React.FormEvent) => {
    e.preventDefault();

    try {
      await apiRequest<void>("/auth/inscription", {
        method: "POST",
        body: JSON.stringify({
          nomComplet,
          email,
          role,
        }),
      });

      alert("Inscription envoyée. Un mot de passe temporaire sera envoyé par email.");
      window.location.href = "/auth/login";
    } catch (error) {
      console.error("Erreur inscription :", error);

      if (error instanceof Error) {
        alert(error.message);
      } else {
        alert("Erreur lors de l'inscription.");
      }
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-100">
      <form
        onSubmit={register}
        className="bg-white p-8 rounded-lg shadow w-full max-w-md"
      >
        <h1 className="text-2xl font-bold mb-6 text-center">Inscription</h1>

        <input
          id="nomComplet"
          name="nomComplet"
          autoComplete="name"
          className="w-full border p-2 rounded mb-4"
          placeholder="Nom complet"
          value={nomComplet}
          onChange={(e) => setNomComplet(e.target.value)}
          required
        />

        <input
          id="email"
          name="email"
          autoComplete="email"
          className="w-full border p-2 rounded mb-4"
          placeholder="Email institutionnel"
          type="email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
        />

        <select
          id="role"
          name="role"
          autoComplete="off"
          className="w-full border p-2 rounded mb-4"
          value={role}
          onChange={(e) => setRole(e.target.value)}
          required
        >
          <option value="ROLE_ETUDIANT">Étudiant</option>
          <option value="ROLE_ENCADRANT">Encadrant</option>
        </select>

        <button
          type="submit"
          className="w-full bg-[#1F4E79] text-white p-2 rounded"
        >
          S'inscrire
        </button>
      </form>
    </div>
  );
}