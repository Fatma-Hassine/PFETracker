import { createContext, useContext, useState, ReactNode, useEffect } from 'react';
import { apiRequest } from '../../services/api';

export type Role = 'Étudiant' | 'Encadrant' | 'Responsable' | 'Directeur' | 'Admin' | 'ServiceStages';

// MODIF : ce mapping utilisait des noms qui ne correspondent à AUCUNE valeur
// réelle de l'enum Role du backend (STUDENT/SUPERVISOR/DEPT_MANAGER/DIRECTOR
// au lieu de ROLE_ETUDIANT/ROLE_ENCADRANT/ROLE_CHEF_DEPARTEMENT/ROLE_DIRECTEUR).
// Résultat : la Sidebar affichait TOUJOURS le menu Étudiant, quel que soit le
// rôle réellement connecté, pour tout le monde sauf les étudiants.
const roleMap: Record<string, Role> = {
  ROLE_ETUDIANT: 'Étudiant',
  ROLE_ENCADRANT: 'Encadrant',
  ROLE_CHEF_DEPARTEMENT: 'Responsable',
  ROLE_DIRECTEUR: 'Directeur',
  ROLE_ADMIN: 'Admin',
  ROLE_SERVICE_STAGE: 'ServiceStages',
};

type Profil = {
  id: number;
  nomComplet: string;
  email: string;
};

interface RoleContextType {
  role: Role;
  isAuthenticated: boolean;
  profil: Profil | null;
  login: (backendRole: string) => void;
  logout: () => void;
  notificationCount: number;
  setNotificationCount: (count: number) => void;
}

const RoleContext = createContext<RoleContextType | undefined>(undefined);

export function RoleProvider({ children }: { children: ReactNode }) {
  const [role, setRole] = useState<Role>('Étudiant');
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [profil, setProfil] = useState<Profil | null>(null);
  const [notificationCount, setNotificationCount] = useState(0);
  const [isLoading, setIsLoading] = useState(true);

  const chargerProfil = () => {
    apiRequest<Profil>('/utilisateurs/moi')
      .then(setProfil)
      .catch(() => setProfil(null));
  };

  useEffect(() => {
    // MODIF : lit les mêmes clés localStorage que LoginPage.tsx écrit
    // réellement (token / role) — l'ancien code attendait un objet "user"
    // JSON jamais écrit par le vrai flux de connexion, donc le rôle restait
    // toujours sur sa valeur par défaut.
    const token = localStorage.getItem('token');
    const backendRole = localStorage.getItem('role');

    if (token && backendRole) {
      setIsAuthenticated(true);
      setRole(roleMap[backendRole] || 'Étudiant');
      chargerProfil();
    }

    setIsLoading(false);
  }, []);

  const login = (backendRole: string) => {
    setIsAuthenticated(true);
    setRole(roleMap[backendRole] || 'Étudiant');
    chargerProfil();
  };

  const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('role');
    setIsAuthenticated(false);
    setProfil(null);
    setRole('Étudiant');
  };

  if (isLoading) {
    return (
      <div className="h-screen w-screen flex items-center justify-center bg-gray-50">
        <div className="flex flex-col items-center gap-4">
          <div className="w-12 h-12 border-4 border-[#1F4E79] border-t-transparent rounded-full animate-spin"></div>
          <p className="text-gray-600 font-medium">Chargement de votre session...</p>
        </div>
      </div>
    );
  }

  return (
    <RoleContext.Provider
      value={{
        role,
        isAuthenticated,
        profil,
        login,
        logout,
        notificationCount,
        setNotificationCount,
      }}
    >
      {children}
    </RoleContext.Provider>
  );
}

export function useRole() {
  const context = useContext(RoleContext);
  if (!context) {
    throw new Error('useRole must be used within RoleProvider');
  }
  return context;
}

/** Chemin de base des pages pour un rôle donné (utilisé par la Navbar/Sidebar). */
export function baseRouteForRole(role: Role): string {
  switch (role) {
    case 'Étudiant': return '/etudiant';
    case 'Encadrant': return '/encadrant';
    case 'Responsable': return '/responsable';
    case 'Directeur': return '/directeur';
    case 'Admin': return '/admin';
    case 'ServiceStages': return '/service-stages';
  }
}
