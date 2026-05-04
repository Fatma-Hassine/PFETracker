// Navbar.tsx
import { useState, useRef, useEffect } from 'react';
import { useRole } from '../contexts/RoleContext';
import { useNavigate } from 'react-router-dom';  // ✅ corrigé

import { Bell, ChevronDown } from 'lucide-react';

interface Notification {
  id: number;
  message: string;
  link: string;
  time: string;
}

export function Navbar({ title }: { title: string }) {
  const { notificationCount, setNotificationCount, logout } = useRole();
  const navigate = useNavigate();
  const [showNotifications, setShowNotifications] = useState(false);
  const [showUserMenu, setShowUserMenu] = useState(false);
  const notifRef = useRef<HTMLDivElement>(null);
  const userRef = useRef<HTMLDivElement>(null);

  const notifications: Notification[] = [
    { id: 1, message: 'Tâche validée par Dr. Trabelsi', link: '/etudiant/taches', time: 'Il y a 5 min' },
    { id: 2, message: 'Nouveau commentaire sur Conception DB', link: '/etudiant/taches', time: 'Il y a 1h' },
    { id: 3, message: 'Réunion confirmée pour le 22 Jan', link: '/etudiant/reunions', time: 'Il y a 2h' },
    { id: 4, message: "Deadline approchante: Rapport d'analyse", link: '/etudiant/taches', time: 'Il y a 3h' },
    { id: 5, message: 'Message de Dr. Trabelsi', link: '/etudiant/messagerie', time: 'Hier' },
  ];

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (notifRef.current && !notifRef.current.contains(event.target as Node)) {
        setShowNotifications(false);
      }
      if (userRef.current && !userRef.current.contains(event.target as Node)) {
        setShowUserMenu(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const handleNotificationClick = (link: string) => {
    setShowNotifications(false);
    setNotificationCount(Math.max(0, notificationCount - 1));
    navigate(link);
  };

  const handleLogout = () => {
    logout();
    navigate('/auth/login');
  };

  return (
    <div className="h-16 bg-white border-b border-gray-200 px-6 flex items-center justify-between">
      <h2 className="text-xl font-semibold text-gray-800">{title}</h2>

      <div className="flex items-center gap-4">
        <div className="relative" ref={notifRef}>
          <button
            onClick={() => setShowNotifications(!showNotifications)}
            className="relative p-2 text-gray-600 hover:bg-gray-100 rounded-lg"
          >
            <Bell size={20} />
            {notificationCount > 0 && (
              <span className="absolute top-1 right-1 w-5 h-5 bg-red-500 text-white text-xs rounded-full flex items-center justify-center">
                {notificationCount}
              </span>
            )}
          </button>

          {showNotifications && (
            <div className="absolute right-0 mt-2 w-80 bg-white border border-gray-200 rounded-lg shadow-lg z-50">
              <div className="p-4 border-b border-gray-200">
                <h3 className="font-semibold">Notifications</h3>
              </div>
              <div className="max-h-96 overflow-y-auto">
                {notifications.map((notif) => (
                  <button
                    key={notif.id}
                    onClick={() => handleNotificationClick(notif.link)}
                    className="w-full p-4 hover:bg-gray-50 border-b border-gray-100 text-left"
                  >
                    <p className="text-sm text-gray-800">{notif.message}</p>
                    <p className="text-xs text-gray-500 mt-1">{notif.time}</p>
                  </button>
                ))}
              </div>
            </div>
          )}
        </div>

        <div className="relative" ref={userRef}>
          <button
            onClick={() => setShowUserMenu(!showUserMenu)}
            className="flex items-center gap-2 p-2 hover:bg-gray-100 rounded-lg"
          >
            <div className="w-8 h-8 rounded-full bg-[#1F4E79] flex items-center justify-center text-white text-sm">
              AS
            </div>
            <ChevronDown size={16} className="text-gray-600" />
          </button>

          {showUserMenu && (
            <div className="absolute right-0 mt-2 w-48 bg-white border border-gray-200 rounded-lg shadow-lg z-50">
              <button
                onClick={() => {
                  setShowUserMenu(false);
                  navigate('/etudiant/profil');
                }}
                className="w-full px-4 py-3 text-left hover:bg-gray-50 border-b border-gray-100"
              >
                Profil
              </button>
              <button
                onClick={handleLogout}
                className="w-full px-4 py-3 text-left hover:bg-gray-50 text-red-600"
              >
                Déconnexion
              </button>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}