import { Outlet, useLocation } from "react-router-dom";
import { Sidebar } from "../components/Sidebar";
import { Navbar } from "../components/Navbar";
import { RoleSwitcher } from "../components/RoleSwitcher";

const getPageTitle = (pathname: string): string => {
  const titles: Record<string, string> = {
    "/etudiant/dashboard": "Tableau de bord",
    "/etudiant/pfe": "Mon PFE",
    "/etudiant/taches": "Tâches",
    "/etudiant/assistant-ia": "Assistant IA",
    "/etudiant/messagerie": "Messagerie",
    "/etudiant/reunions": "Réunions",
    "/etudiant/profil": "Profil",

    "/encadrant/dashboard": "Tableau de bord",
    "/encadrant/etudiants": "Mes étudiants",
    "/encadrant/validation": "Validation",
    "/encadrant/reunions": "Réunions",
    "/encadrant/messagerie": "Messagerie",
    "/encadrant/profil": "Profil",

    "/admin/dashboard": "Tableau de bord",
    "/admin/comptes": "Comptes",
    "/admin/affectations": "Affectations",
    "/admin/supervision": "Supervision",
    "/admin/profil": "Profil",

    "/directeur/dashboard": "Vue globale",
    "/directeur/etudiants": "Étudiants",
    "/directeur/affectations": "Affectations",
    "/directeur/profil": "Profil",

    // MODIF : titres ajoutés pour responsable
    "/responsable/dashboard": "Tableau de bord responsable",
    "/responsable/comptes": "Comptes en attente",
    "/responsable/affectations": "Affectations",
    "/responsable/encadrants": "Encadrants",
    "/responsable/profil": "Profil",

    // MODIF : titres ajoutés pour service des stages
    "/service-stages/dashboard": "Tableau de bord service stages",
    "/service-stages/etudiants": "Étudiants",
    "/service-stages/stages": "Stages",
    "/service-stages/profil": "Profil",
  };

  if (pathname.startsWith("/encadrant/etudiants/")) {
    return "Détail étudiant";
  }

  return titles[pathname] || "PFETracker";
};

export function RootLayout() {
  const location = useLocation();
  const title = getPageTitle(location.pathname);

  return (
    <div className="flex h-screen bg-gray-50">
      <Sidebar />
      <div className="flex-1 flex flex-col overflow-hidden">
        <Navbar title={title} />
        <main className="flex-1 overflow-y-auto p-6">
          {/* MODIF : Outlet affiche les pages enfants définies dans routes.tsx */}
          <Outlet />
        </main>
      </div>
      <RoleSwitcher />
    </div>
  );
}

/*import { Outlet, useLocation } from 'react-router';
import { Sidebar } from '../components/Sidebar';
import { Navbar } from '../components/Navbar';
import { RoleSwitcher } from '../components/RoleSwitcher';
import { useRole } from '../contexts/RoleContext';
import { useEffect } from 'react';

const getPageTitle = (pathname: string): string => {
  const titles: Record<string, string> = {
    '/etudiant/dashboard': 'Tableau de bord',
    '/etudiant/pfe': 'Mon PFE',
    '/etudiant/taches': 'Tâches',
    '/etudiant/assistant-ia': 'Assistant IA',
    '/etudiant/messagerie': 'Messagerie',
    '/etudiant/reunions': 'Réunions',
    '/etudiant/profil': 'Profil',
    '/encadrant/dashboard': 'Tableau de bord',
    '/encadrant/etudiants': 'Mes étudiants',
    '/encadrant/validation': 'Validation',
    '/encadrant/reunions': 'Réunions',
    '/encadrant/messagerie': 'Messagerie',
    '/encadrant/profil': 'Profil',
    '/admin/dashboard': 'Tableau de bord',
    '/admin/comptes': 'Comptes',
    '/admin/affectations': 'Affectations',
    '/admin/supervision': 'Supervision',
    '/admin/profil': 'Profil',
    '/directeur/dashboard': 'Vue globale',
    '/directeur/profil': 'Profil',
  };

  if (pathname.startsWith('/encadrant/etudiants/')) {
    return 'Détail étudiant';
  }

  return titles[pathname] || 'PFETracker';
};

export function RootLayout() {
  const { isAuthenticated } = useRole();
  const navigate = useNavigate();
  const location = useLocation();

  useEffect(() => {
    if (!isAuthenticated) {
      navigate('/auth/login');
    }
  }, [isAuthenticated, navigate]);

  const title = getPageTitle(location.pathname);

  if (!isAuthenticated) return null;

  return (
    <div className="flex h-screen bg-gray-50">
      <Sidebar />
      <div className="flex-1 flex flex-col overflow-hidden">
        <Navbar title={title} />
        <main className="flex-1 overflow-y-auto p-6">
          <Outlet />
        </main>
      </div>
      <RoleSwitcher />
    </div>
  );
}
*/