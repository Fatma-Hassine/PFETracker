import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { apiRequest } from "../../../services/api";

type AuthResponse = {
  accessToken?: string;
  token?: string;
  refreshToken?: string;
  role?: string;
  mustChangePassword?: boolean;
};

export function LoginPage() {
  const [email, setEmail] = useState("");
  const [motDePasse, setMotDePasse] = useState("");
  const [loading, setLoading] = useState(false);

  const navigate = useNavigate();

  const login = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);

    try {
      const data = await apiRequest<AuthResponse>("/auth/connexion", {
        method: "POST",
        body: JSON.stringify({
          email,
          motDePasse,
        }),
      });

      const token = data.accessToken || data.token;

      if (token) {
        localStorage.setItem("token", token);
      }

      if (data.refreshToken) {
        localStorage.setItem("refreshToken", data.refreshToken);
      }

      if (data.role) {
        localStorage.setItem("role", data.role);
      }

      if (data.mustChangePassword) {
        navigate("/auth/change-password");
        return;
      }

      switch (data.role) {
        case "ROLE_ADMIN":
          navigate("/admin/dashboard");
          break;

        case "ROLE_ETUDIANT":
          navigate("/etudiant/dashboard");
          break;

        case "ROLE_ENCADRANT":
          navigate("/encadrant/dashboard");
          break;

        case "ROLE_DIRECTEUR":
          navigate("/directeur/dashboard");
          break;

        case "ROLE_SERVICE_STAGE":
          navigate("/service-stages/dashboard");
          break;

        case "ROLE_DEPT_MANAGER":
        case "ROLE_RESPONSABLE":
          navigate("/responsable/dashboard");
          break;

        default:
          navigate("/auth/login");
          break;
      }
    } catch (error) {
      console.error("Erreur connexion :", error);

      if (error instanceof Error) {
        alert(error.message);
      } else {
        alert("Erreur lors de la connexion");
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-100">
      <form
        onSubmit={login}
        className="bg-white p-8 rounded-lg shadow w-full max-w-md"
      >
        <h1 className="text-2xl font-bold mb-6 text-center">Connexion</h1>

        <input
          id="email"
          name="email"
          autoComplete="email"
          className="w-full border p-2 rounded mb-4"
          placeholder="Email"
          type="email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
        />

        <input
          id="motDePasse"
          name="motDePasse"
          autoComplete="current-password"
          className="w-full border p-2 rounded mb-4"
          placeholder="Mot de passe"
          type="password"
          value={motDePasse}
          onChange={(e) => setMotDePasse(e.target.value)}
          required
        />

        <button
          type="submit"
          className="w-full bg-[#1F4E79] text-white p-2 rounded disabled:opacity-60"
          disabled={loading}
        >
          {loading ? "Connexion..." : "Se connecter"}
        </button>
      </form>
    </div>
  );
}