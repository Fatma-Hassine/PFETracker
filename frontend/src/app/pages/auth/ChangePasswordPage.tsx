import React, { useState } from "react";
import { apiRequest } from "../../../services/api";

export function ChangePasswordPage() {
  const [ancienMotDePasse, setAncienMotDePasse] = useState("");
  const [nouveauMotDePasse, setNouveauMotDePasse] = useState("");
  const [confirmer, setConfirmer] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState(false);

  const submit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");

    // Validation côté client avant envoi
    if (nouveauMotDePasse !== confirmer) {
      setError("Les mots de passe ne correspondent pas");
      return;
    }
    if (nouveauMotDePasse.length < 8) {
      setError("Minimum 8 caractères requis");
      return;
    }
    if (!/[A-Z]/.test(nouveauMotDePasse)) {
      setError("Au moins 1 lettre majuscule requise");
      return;
    }
    if (!/[0-9]/.test(nouveauMotDePasse)) {
      setError("Au moins 1 chiffre requis");
      return;
    }
    if (!/[^a-zA-Z0-9]/.test(nouveauMotDePasse)) {
      setError("Au moins 1 caractère spécial requis");
      return;
    }

    setLoading(true);
    try {
      // On envoie uniquement les 2 champs que le DTO attend
      await apiRequest<void>("/auth/changer-mot-de-passe", {
        method: "POST",
        body: JSON.stringify({
          ancienMotDePasse,
          nouveauMotDePasse,
        }),
      });
      setSuccess(true);
      setTimeout(() => {
        localStorage.clear();
        window.location.href = "/auth/login";
      }, 2000);
    } catch (err) {
      setError(
        err instanceof Error ? err.message : "Erreur lors du changement"
      );
    } finally {
      setLoading(false);
    }
  };

  // Calcul de la force du mot de passe
  const getStrength = () => {
    let s = 0;
    if (nouveauMotDePasse.length >= 8) s++;
    if (/[A-Z]/.test(nouveauMotDePasse)) s++;
    if (/[0-9]/.test(nouveauMotDePasse)) s++;
    if (/[^a-zA-Z0-9]/.test(nouveauMotDePasse)) s++;
    return s;
  };

  if (success) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-100">
        <div className="bg-white p-8 rounded-lg shadow text-center max-w-md w-full">
          <p className="text-5xl mb-4">✅</p>
          <h2 className="text-xl font-bold text-green-700 mb-2">
            Mot de passe changé !
          </h2>
          <p className="text-gray-500 text-sm">
            Redirection vers la connexion...
          </p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-100">
      <div className="bg-white p-8 rounded-lg shadow w-full max-w-md">
        <h1 className="text-2xl font-bold mb-2 text-center text-[#1F4E79]">
          Changer le mot de passe
        </h1>
        <p className="text-sm text-gray-500 text-center mb-6">
          Première connexion — veuillez définir votre mot de passe
        </p>

        {error && (
          <div className="bg-red-50 border border-red-200 text-red-700 text-sm px-4 py-3 rounded-lg mb-4">
            {error}
          </div>
        )}

        <form onSubmit={submit} className="space-y-4">
          {/* Ancien mot de passe */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Ancien mot de passe
            </label>
            <input
              type="password"
              placeholder="Mot de passe temporaire reçu par email"
              value={ancienMotDePasse}
              onChange={(e) => setAncienMotDePasse(e.target.value)}
              required
              disabled={loading}
              className="w-full border border-gray-300 p-2.5 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#1F4E79] disabled:opacity-50"
            />
          </div>

          {/* Nouveau mot de passe */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Nouveau mot de passe
            </label>
            <input
              type="password"
              placeholder="Min. 8 car., 1 majuscule, 1 chiffre, 1 spécial"
              value={nouveauMotDePasse}
              onChange={(e) => setNouveauMotDePasse(e.target.value)}
              required
              disabled={loading}
              className="w-full border border-gray-300 p-2.5 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#1F4E79] disabled:opacity-50"
            />

            {/* Indicateur de force */}
            {nouveauMotDePasse && (
              <div className="mt-2 space-y-1">
                <div className="flex gap-1">
                  {[1, 2, 3, 4].map((level) => {
                    const s = getStrength();
                    return (
                      <div
                        key={level}
                        className={`h-1.5 flex-1 rounded-full transition-colors ${
                          level <= s
                            ? s <= 1
                              ? "bg-red-400"
                              : s <= 2
                              ? "bg-amber-400"
                              : s <= 3
                              ? "bg-blue-400"
                              : "bg-green-500"
                            : "bg-gray-200"
                        }`}
                      />
                    );
                  })}
                </div>
                <p className="text-xs text-gray-500">
                  {["", "Faible", "Moyen", "Bon", "Fort"][getStrength()]}
                </p>
              </div>
            )}
          </div>

          {/* Confirmer mot de passe (validation locale uniquement) */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Confirmer le mot de passe
            </label>
            <input
              type="password"
              placeholder="Répétez le nouveau mot de passe"
              value={confirmer}
              onChange={(e) => setConfirmer(e.target.value)}
              required
              disabled={loading}
              className={`w-full border p-2.5 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#1F4E79] disabled:opacity-50 ${
                confirmer && confirmer !== nouveauMotDePasse
                  ? "border-red-400 bg-red-50"
                  : "border-gray-300"
              }`}
            />
            {confirmer && confirmer !== nouveauMotDePasse && (
              <p className="text-xs text-red-500 mt-1">
                Les mots de passe ne correspondent pas
              </p>
            )}
          </div>

          <button
            type="submit"
            disabled={loading || (!!confirmer && confirmer !== nouveauMotDePasse)}
            className="w-full bg-[#1F4E79] text-white py-2.5 rounded-lg font-medium hover:bg-[#163A5C] disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
          >
            {loading ? "Modification..." : "Changer le mot de passe"}
          </button>
        </form>
      </div>
    </div>
  );
}