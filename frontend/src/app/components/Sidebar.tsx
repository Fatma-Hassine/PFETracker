// Sidebar.tsx
import { useRole } from '../contexts/RoleContext';
import { useNavigate, useLocation } from 'react-router-dom';  // ✅ corrigé

import {
  LayoutDashboard, FileText, CheckSquare, MessageSquare,
  Calendar, User, Users, ClipboardCheck, UserCog,
  GitBranch, Eye, BarChart3, Sparkles,
} from 'lucide-react';

interface NavItem {
  label: string;
  path: string;
  icon: React.ReactNode;
  badge?: number;
}

export function Sidebar() {
  const { role } = useRole();
  const navigate = useNavigate();
  const location = useLocation();

  const getNavItems = (): NavItem[] => {
    switch (role) {
      case 'Étudiant':
        return [
          { label: 'Tableau de bord', path: '/etudiant/dashboard', icon: <LayoutDashboard size={20} /> },
          { label: 'Mon PFE', path: '/etudiant/pfe', icon: <FileText size={20} /> },
          { label: 'Tâches', path: '/etudiant/taches', icon: <CheckSquare size={20} /> },
          { label: 'Assistant IA', path: '/etudiant/assistant-ia', icon: <Sparkles size={20} /> },
          { label: 'Messagerie', path: '/etudiant/messagerie', icon: <MessageSquare size={20} />, badge: 2 },
          { label: 'Réunions', path: '/etudiant/reunions', icon: <Calendar size={20} /> },
          { label: 'Profil', path: '/etudiant/profil', icon: <User size={20} /> },
        ];
      case 'Encadrant':
        return [
          { label: 'Tableau de bord', path: '/encadrant/dashboard', icon: <LayoutDashboard size={20} /> },
          { label: 'Mes étudiants', path: '/encadrant/etudiants', icon: <Users size={20} /> },
          { label: 'Validation', path: '/encadrant/validation', icon: <ClipboardCheck size={20} />, badge: 3 },
          { label: 'Réunions', path: '/encadrant/reunions', icon: <Calendar size={20} /> },
          { label: 'Messagerie', path: '/encadrant/messagerie', icon: <MessageSquare size={20} /> },
          { label: 'Profil', path: '/encadrant/profil', icon: <User size={20} /> },
        ];
      case 'Responsable':
        return [
          { label: 'Tableau de bord', path: '/responsable/dashboard', icon: <LayoutDashboard size={20} /> },
          { label: 'Comptes', path: '/responsable/comptes', icon: <UserCog size={20} />, badge: 3 },
          { label: 'Affectations', path: '/responsable/affectations', icon: <GitBranch size={20} /> },
          { label: 'Encadrants', path: '/responsable/encadrants', icon: <Users size={20} /> },
          { label: 'Profil', path: '/responsable/profil', icon: <User size={20} /> },
        ];
      case 'Directeur':
        return [
          { label: 'Vue globale', path: '/directeur/dashboard', icon: <BarChart3 size={20} /> },
          { label: 'Profil', path: '/directeur/profil', icon: <User size={20} /> },
        ];
      default:
        return [];
    }
  };

  const navItems = getNavItems();

  return (
    <div className="w-60 h-screen bg-white border-r border-gray-200 flex flex-col">
      <div className="p-6 border-b border-gray-200">
        <h1 className="text-2xl font-bold text-[#1F4E79]">PFETracker</h1>
      </div>

      <nav className="flex-1 p-4 space-y-1">
        {navItems.map((item) => {
          const isActive = location.pathname === item.path;
          return (
            <button
              key={item.path}
              onClick={() => navigate(item.path)}
              className={`w-full flex items-center gap-3 px-4 py-3 rounded-lg transition-colors ${
                isActive
                  ? 'bg-[#1F4E79] text-white'
                  : 'text-gray-700 hover:bg-gray-100'
              }`}
            >
              {item.icon}
              <span className="flex-1 text-left">{item.label}</span>
              {item.badge && (
                <span className="bg-red-500 text-white text-xs px-2 py-0.5 rounded-full">
                  {item.badge}
                </span>
              )}
            </button>
          );
        })}
      </nav>

      <div className="p-4 border-t border-gray-200">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 rounded-full bg-[#1F4E79] flex items-center justify-center text-white">
            AS
          </div>
          <div className="flex-1">
            <div className="font-medium text-sm">Ahmed Ben Salem</div>
            <div className="text-xs text-gray-500">{role}</div>
          </div>
        </div>
      </div>
    </div>
  );
}