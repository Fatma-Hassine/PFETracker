const API_URL = "/api";

function deconnecter() {
  localStorage.removeItem("token");
  localStorage.removeItem("refreshToken");
  localStorage.removeItem("role");
  if (!window.location.pathname.startsWith("/auth")) {
    window.location.href = "/auth/login";
  }
}

let refreshEnCours: Promise<string | null> | null = null;

/**
 * Le token d'accès expire après 15 min (cahier §4.1.2). On le renouvelle avec le
 * refresh token (rotation côté backend) au lieu de laisser toute l'application
 * échouer silencieusement. Partagé par tous les clients HTTP du frontend.
 */
export function rafraichirToken(): Promise<string | null> {
  if (refreshEnCours) return refreshEnCours;

  const refreshToken = localStorage.getItem("refreshToken");
  if (!refreshToken) return Promise.resolve(null);

  refreshEnCours = fetch(`${API_URL}/auth/refresh`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ refreshToken }),
  })
    .then(async (res) => {
      if (!res.ok) return null;
      const data = await res.json();
      const token = data.accessToken || data.token;
      if (!token) return null;
      localStorage.setItem("token", token);
      if (data.refreshToken) localStorage.setItem("refreshToken", data.refreshToken);
      return token as string;
    })
    .catch(() => null)
    .finally(() => {
      refreshEnCours = null;
    });

  return refreshEnCours;
}

export function sessionExpiree() {
  deconnecter();
}

export async function apiRequest<T>(
  endpoint: string,
  options: RequestInit = {},
  dejaRetente = false
): Promise<T> {
  const token = localStorage.getItem("token");
  const estPublic = endpoint.startsWith("/auth/");

  const response = await fetch(`${API_URL}${endpoint}`, {
    ...options,
    headers: {
      "Content-Type": "application/json",
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...(options.headers || {}),
    },
  });

  if (response.status === 401 && !estPublic && !dejaRetente) {
    const nouveau = await rafraichirToken();
    if (nouveau) {
      return apiRequest<T>(endpoint, options, true);
    }
    deconnecter();
  }

  if (!response.ok) {
    const text = await response.text();
    throw new Error(text || `Erreur API ${response.status}`);
  }

  if (response.status === 204) {
    return null as T;
  }

  const text = await response.text();

  if (!text) {
    return null as T;
  }

  return JSON.parse(text) as T;
}
