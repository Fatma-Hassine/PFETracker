import { Outlet, useLocation } from "react-router-dom";
import { Sidebar } from "../components/Sidebar";
import { Navbar } from "../components/Navbar";

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
    "/admin/departements": "Départements",
    "/admin/logs": "Logs",
    "/admin/profil": "Profil",

    "/directeur/dashboard": "Vue globale",
    "/directeur/etudiants": "Étudiants",
    "/directeur/affectations": "Affectations",
    "/directeur/profil": "Profil",

    "/responsable/dashboard": "Tableau de bord responsable",
    "/responsable/comptes": "Comptes en attente",
    "/responsable/affectations": "Affectations",
    "/responsable/encadrants": "Encadrants",
    "/responsable/profil": "Profil",

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
          <Outlet />
        </main>
      </div>
    </div>
  );
}