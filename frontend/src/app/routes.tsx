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

export const router = createBrowserRouter([
  // MODIF : la page racine redirige directement vers login,
  // sans charger RootLayout, Sidebar, Navbar, etc.
  {
    path: "/",
    element: <Navigate to="/auth/login" replace />,
  },

  // MODIF : routes auth séparées du layout principal
  {
    path: "/auth",
    element: <AuthLayout />,
    errorElement: <div>Erreur auth</div>,
    children: [
      { index: true, element: <Navigate to="/auth/login" replace /> },
      { path: "login", element: <LoginPage /> },
      { path: "register", element: <RegisterPage /> },
      { path: "forgot-password", element: <ForgotPasswordPage /> },
      { path: "change-password", element: <ChangePasswordPage /> },
    ],
  },

  // MODIF : toutes les pages avec Sidebar/Navbar sont ici
  {
    path: "/",
    element: <RootLayout />,
    errorElement: <div>Erreur dans le layout principal</div>,
    children: [
      // Étudiant routes
      { path: "etudiant/dashboard", element: <EtudiantDashboard /> },
      { path: "etudiant/pfe", element: <EtudiantPFE /> },
      { path: "etudiant/taches", element: <EtudiantTaches /> },
      { path: "etudiant/assistant-ia", element: <EtudiantAssistantIA /> },
      { path: "etudiant/messagerie", element: <EtudiantMessagerie /> },
      { path: "etudiant/reunions", element: <EtudiantReunions /> },
      { path: "etudiant/profil", element: <EtudiantProfil /> },

      // Encadrant routes
      { path: "encadrant/dashboard", element: <EncadrantDashboard /> },
      { path: "encadrant/etudiants", element: <EncadrantEtudiants /> },
      { path: "encadrant/etudiants/:id", element: <EncadrantEtudiantDetail /> },
      { path: "encadrant/validation", element: <EncadrantValidation /> },
      { path: "encadrant/reunions", element: <EncadrantReunions /> },
      { path: "encadrant/messagerie", element: <EncadrantMessagerie /> },
      { path: "encadrant/profil", element: <EncadrantProfil /> },

      // Admin routes
      { path: "admin/dashboard", element: <AdminDashboard /> },
      { path: "admin/comptes", element: <AdminComptes /> },
      { path: "admin/affectations", element: <AdminAffectations /> },
      { path: "admin/supervision", element: <AdminSupervision /> },
      { path: "admin/profil", element: <AdminProfil /> },

      // Directeur routes
      { path: "directeur/dashboard", element: <DirecteurDashboard /> },
      { path: "directeur/etudiants", element: <DirecteurEtudiants /> },
      { path: "directeur/affectations", element: <DirecteurAffectations /> },
      { path: "directeur/profil", element: <DirecteurProfil /> },

      // Responsable routes
      { path: "responsable/dashboard", element: <ResponsableDashboard /> },
      { path: "responsable/comptes", element: <ResponsableComptes /> },
      { path: "responsable/affectations", element: <ResponsableAffectations /> },
      { path: "responsable/encadrants", element: <ResponsableEncadrants /> },
      { path: "responsable/profil", element: <ResponsableProfil /> },

      // Service stages routes
      { path: "service-stages/dashboard", element: <ServiceStagesDashboard /> },
      { path: "service-stages/etudiants", element: <ServiceStagesEtudiants /> },
      { path: "service-stages/stages", element: <ServiceStagesStages /> },
      { path: "service-stages/profil", element: <ServiceStagesProfil /> },
    ],
  },

  {
    path: "*",
    element: <div>404 - Page introuvable</div>,
  },
]);
/*import { createBrowserRouter, Navigate } from 'react-router-dom';
//import { createBrowserRouter } from 'react-router';
import { RootLayout } from './layouts/RootLayout';
import { AuthLayout } from './layouts/AuthLayout';

// Auth pages
import { LoginPage } from './pages/auth/LoginPage';
import { RegisterPage } from './pages/auth/RegisterPage';
import { ForgotPasswordPage } from './pages/auth/ForgotPasswordPage';
import { ChangePasswordPage } from './pages/auth/ChangePasswordPage';

// Étudiant pages
import { EtudiantDashboard } from './pages/etudiant/Dashboard';
import { EtudiantPFE } from './pages/etudiant/PFE';
import { EtudiantTaches } from './pages/etudiant/Taches';
import { EtudiantMessagerie } from './pages/etudiant/Messagerie';
import { EtudiantReunions } from './pages/etudiant/Reunions';
import { EtudiantProfil } from './pages/etudiant/Profil';
import { EtudiantAssistantIA } from './pages/etudiant/AssistantIA';

// Encadrant pages
import { EncadrantDashboard } from './pages/encadrant/Dashboard';
import { EncadrantEtudiants } from './pages/encadrant/Etudiants';
import { EncadrantValidation } from './pages/encadrant/Validation';
import { EncadrantReunions } from './pages/encadrant/Reunions';
import { EncadrantMessagerie } from './pages/encadrant/Messagerie';
import { EncadrantProfil } from './pages/encadrant/Profil';
import { EncadrantEtudiantDetail } from './pages/encadrant/EtudiantDetail';

// Responsable pages
import { AdminDashboard } from './pages/admin/Dashboard';
import { AdminComptes } from './pages/admin/Comptes';
import { AdminAffectations } from './pages/admin/Affectations';
import { AdminSupervision } from './pages/admin/Supervision';
import { AdminProfil } from './pages/admin/Profil';

// Directeur pages
import { DirecteurDashboard } from './pages/directeur/Dashboard';
import { DirecteurProfil } from './pages/directeur/Profil';

export const router = createBrowserRouter([
  {
    path: '/',
    element: <RootLayout />,
    children: [
      // Étudiant routes
      { path: 'etudiant/dashboard', element: <EtudiantDashboard /> },
      { path: 'etudiant/pfe', element: <EtudiantPFE /> },
      { path: 'etudiant/taches', element: <EtudiantTaches /> },
      { path: 'etudiant/assistant-ia', element: <EtudiantAssistantIA /> },
      { path: 'etudiant/messagerie', element: <EtudiantMessagerie /> },
      { path: 'etudiant/reunions', element: <EtudiantReunions /> },
      { path: 'etudiant/profil', element: <EtudiantProfil /> },

      // Encadrant routes
      { path: 'encadrant/dashboard', element: <EncadrantDashboard /> },
      { path: 'encadrant/etudiants', element: <EncadrantEtudiants /> },
      { path: 'encadrant/etudiants/:id', element: <EncadrantEtudiantDetail /> },
      { path: 'encadrant/validation', element: <EncadrantValidation /> },
      { path: 'encadrant/reunions', element: <EncadrantReunions /> },
      { path: 'encadrant/messagerie', element: <EncadrantMessagerie /> },
      { path: 'encadrant/profil', element: <EncadrantProfil /> },

      // Admin routes
      { path: 'admin/dashboard', element: <AdminDashboard /> },
      { path: 'admin/comptes', element: <AdminComptes /> },
      { path: 'admin/affectations', element: <AdminAffectations /> },
      { path: 'admin/supervision', element: <AdminSupervision /> },
      { path: 'admin/profil', element: <AdminProfil /> },

      // Directeur routes
      { path: 'directeur/dashboard', element: <DirecteurDashboard /> },
      { path: 'directeur/profil', element: <DirecteurProfil /> },

      // Default redirect
      { index: true, element: <EtudiantDashboard /> },
    ],
  },
  {
    path: '/auth',
    element: <AuthLayout />,
    children: [
      { path: 'login', element: <LoginPage /> },
      { path: 'register', element: <RegisterPage /> },
      { path: 'forgot-password', element: <ForgotPasswordPage /> },
      { path: 'change-password', element: <ChangePasswordPage /> },
    ],
  },
]);
*/