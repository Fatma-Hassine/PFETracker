import { createBrowserRouter, Navigate } from "react-router-dom";

import { RootLayout } from "./layouts/RootLayout";
import { AuthLayout } from "./layouts/AuthLayout";

// Auth pages
import { LoginPage } from "./pages/auth/LoginPage";
import { RegisterPage } from "./pages/auth/RegisterPage";
import { ForgotPasswordPage } from "./pages/auth/ForgotPasswordPage";
import { ChangePasswordPage } from "./pages/auth/ChangePasswordPage";

// Étudiant pages
import { EtudiantDashboard } from "./pages/etudiant/Dashboard";
import { EtudiantPFE } from "./pages/etudiant/PFE";
import { EtudiantTaches } from "./pages/etudiant/Taches";
import { EtudiantMessagerie } from "./pages/etudiant/Messagerie";
import { EtudiantReunions } from "./pages/etudiant/Reunions";
import { EtudiantProfil } from "./pages/etudiant/Profil";
import { EtudiantAssistantIA } from "./pages/etudiant/AssistantIA";

// Encadrant pages
import { EncadrantDashboard } from "./pages/encadrant/Dashboard";
import { EncadrantEtudiants } from "./pages/encadrant/Etudiants";
import { EncadrantValidation } from "./pages/encadrant/Validation";
import { EncadrantReunions } from "./pages/encadrant/Reunions";
import { EncadrantMessagerie } from "./pages/encadrant/Messagerie";
import { EncadrantProfil } from "./pages/encadrant/Profil";
import { EncadrantEtudiantDetail } from "./pages/encadrant/EtudiantDetail";

// Admin pages
import { AdminDashboard } from "./pages/admin/Dashboard";
import { AdminComptes } from "./pages/admin/Comptes";
import { AdminAffectations } from "./pages/admin/Affectations";
import { AdminSupervision } from "./pages/admin/Supervision";
import { AdminProfil } from "./pages/admin/Profil";

// Directeur pages
import { DirecteurDashboard } from "./pages/directeur/Dashboard";
import { DirecteurProfil } from "./pages/directeur/Profil";
import { DirecteurEtudiants } from "./pages/directeur/Etudiants";
import { DirecteurAffectations } from "./pages/directeur/Affectations";

// Responsable pages
import { ResponsableDashboard } from "./pages/responsable/Dashboard";
import { ResponsableComptes } from "./pages/responsable/Comptes";
import { ResponsableAffectations } from "./pages/responsable/Affectations";
import { ResponsableEncadrants } from "./pages/responsable/Encadrants";
import { ResponsableProfil } from "./pages/responsable/Profil";

// Service des stages pages
import { ServiceStagesDashboard } from "./pages/service-stages/Dashboard";
import { ServiceStagesEtudiants } from "./pages/service-stages/Etudiants";
import { ServiceStagesStages } from "./pages/service-stages/Stages";
import { ServiceStagesProfil } from "./pages/service-stages/Profil";

const LayoutError = () => {
  return (
    <div className="p-6 text-red-600">
      Erreur dans le layout principal. Vérifie Sidebar, Navbar ou la page appelée.
    </div>
  );
};

const AuthError = () => {
  return (
    <div className="p-6 text-red-600">
      Erreur dans la partie authentification.
    </div>
  );
};

export const router = createBrowserRouter([
  {
    path: "/",
    element: <Navigate to="/auth/login" replace />,
  },

  {
    path: "/auth",
    element: <AuthLayout />,
    errorElement: <AuthError />,
    children: [
      {
        index: true,
        element: <Navigate to="/auth/login" replace />,
      },
      {
        path: "login",
        element: <LoginPage />,
      },
      {
        path: "register",
        element: <RegisterPage />,
      },
      {
        path: "forgot-password",
        element: <ForgotPasswordPage />,
      },
      {
        path: "change-password",
        element: <ChangePasswordPage />,
      },
    ],
  },

  {
    path: "/etudiant",
    element: <RootLayout />,
    errorElement: <LayoutError />,
    children: [
      {
        index: true,
        element: <Navigate to="/etudiant/dashboard" replace />,
      },
      {
        path: "dashboard",
        element: <EtudiantDashboard />,
      },
      {
        path: "pfe",
        element: <EtudiantPFE />,
      },
      {
        path: "taches",
        element: <EtudiantTaches />,
      },
      {
        path: "assistant-ia",
        element: <EtudiantAssistantIA />,
      },
      {
        path: "messagerie",
        element: <EtudiantMessagerie />,
      },
      {
        path: "reunions",
        element: <EtudiantReunions />,
      },
      {
        path: "profil",
        element: <EtudiantProfil />,
      },
    ],
  },

  {
    path: "/encadrant",
    element: <RootLayout />,
    errorElement: <LayoutError />,
    children: [
      {
        index: true,
        element: <Navigate to="/encadrant/dashboard" replace />,
      },
      {
        path: "dashboard",
        element: <EncadrantDashboard />,
      },
      {
        path: "etudiants",
        element: <EncadrantEtudiants />,
      },
      {
        path: "etudiants/:id",
        element: <EncadrantEtudiantDetail />,
      },
      {
        path: "validation",
        element: <EncadrantValidation />,
      },
      {
        path: "reunions",
        element: <EncadrantReunions />,
      },
      {
        path: "messagerie",
        element: <EncadrantMessagerie />,
      },
      {
        path: "profil",
        element: <EncadrantProfil />,
      },
    ],
  },

  {
    path: "/admin",
    element: <RootLayout />,
    errorElement: <LayoutError />,
    children: [
      {
        index: true,
        element: <Navigate to="/admin/dashboard" replace />,
      },
      {
        path: "dashboard",
        element: <AdminDashboard />,
      },
      {
        path: "comptes",
        element: <AdminComptes />,
      },
      {
        path: "affectations",
        element: <AdminAffectations />,
      },
      {
        path: "supervision",
        element: <AdminSupervision />,
      },
      {
        path: "profil",
        element: <AdminProfil />,
      },
    ],
  },

  {
    path: "/directeur",
    element: <RootLayout />,
    errorElement: <LayoutError />,
    children: [
      {
        index: true,
        element: <Navigate to="/directeur/dashboard" replace />,
      },
      {
        path: "dashboard",
        element: <DirecteurDashboard />,
      },
      {
        path: "etudiants",
        element: <DirecteurEtudiants />,
      },
      {
        path: "affectations",
        element: <DirecteurAffectations />,
      },
      {
        path: "profil",
        element: <DirecteurProfil />,
      },
    ],
  },

  {
    path: "/responsable",
    element: <RootLayout />,
    errorElement: <LayoutError />,
    children: [
      {
        index: true,
        element: <Navigate to="/responsable/dashboard" replace />,
      },
      {
        path: "dashboard",
        element: <ResponsableDashboard />,
      },
      {
        path: "comptes",
        element: <ResponsableComptes />,
      },
      {
        path: "affectations",
        element: <ResponsableAffectations />,
      },
      {
        path: "encadrants",
        element: <ResponsableEncadrants />,
      },
      {
        path: "profil",
        element: <ResponsableProfil />,
      },
    ],
  },

  {
    path: "/service-stages",
    element: <RootLayout />,
    errorElement: <LayoutError />,
    children: [
      {
        index: true,
        element: <Navigate to="/service-stages/dashboard" replace />,
      },
      {
        path: "dashboard",
        element: <ServiceStagesDashboard />,
      },
      {
        path: "etudiants",
        element: <ServiceStagesEtudiants />,
      },
      {
        path: "stages",
        element: <ServiceStagesStages />,
      },
      {
        path: "profil",
        element: <ServiceStagesProfil />,
      },
    ],
  },

  {
    path: "*",
    element: <div>404 - Page introuvable</div>,
  },
]);