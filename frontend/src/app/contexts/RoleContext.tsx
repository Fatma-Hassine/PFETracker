import { createContext, useContext, useState, ReactNode, useEffect } from 'react';
import authService, { AuthResponse } from '../../api/authService';

export type Role = 'Étudiant' | 'Encadrant' | 'Responsable' | 'Directeur';

// Map backend roles to frontend roles
const roleMap: Record<string, Role> = {
  'STUDENT': 'Étudiant',
  'SUPERVISOR': 'Encadrant',
  'DEPT_MANAGER': 'Responsable',
  'DIRECTOR': 'Directeur'
};

interface RoleContextType {
  role: Role;
  setRole: (role: Role) => void;
  isAuthenticated: boolean;
  user: AuthResponse | null;
  login: (userData: AuthResponse) => void;
  logout: () => void;
  notificationCount: number;
  setNotificationCount: (count: number) => void;
}

const RoleContext = createContext<RoleContextType | undefined>(undefined);

export function RoleProvider({ children }: { children: ReactNode }) {
  const [role, setRole] = useState<Role>('Étudiant');
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [user, setUser] = useState<AuthResponse | null>(null);
  const [notificationCount, setNotificationCount] = useState(3);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    // Check initial auth state on mount
    const checkAuth = () => {
      if (authService.isAuthenticated()) {
        const currentUser = authService.getCurrentUser();
        if (currentUser) {
          setUser(currentUser);
          setIsAuthenticated(true);
          const mappedRole = roleMap[currentUser.role] || 'Étudiant';
          setRole(mappedRole);
        }
      }
      setIsLoading(false);
    };
    checkAuth();
  }, []);

  const login = (userData: AuthResponse) => {
    setUser(userData);
    setIsAuthenticated(true);
    const mappedRole = roleMap[userData.role] || 'Étudiant';
    setRole(mappedRole);
  };

  const logout = () => {
    authService.logout();
    setIsAuthenticated(false);
    setUser(null);
    setRole('Étudiant');
  };

  if (isLoading) {
    return null; // Or a loading spinner
  }

  return (
    <RoleContext.Provider
      value={{
        role,
        setRole,
        isAuthenticated,
        user,
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
