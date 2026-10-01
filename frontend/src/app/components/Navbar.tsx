// Navbar.tsx
import { useState, useRef, useEffect } from 'react';
import { useRole, baseRouteForRole, Role } from '../contexts/RoleContext';
import { useNavigate } from 'react-router-dom';
import { Bell, ChevronDown } from 'lucide-react';
import notificationService, { NotificationDTO } from '../../api/notificationService';

// Le backend envoie des URLs génériques ("/messages/1", "/meetings/2", "/tasks/3"…)
// qui ne correspondent à aucune route du frontend (→ 404). On les traduit vers la page du rôle.
function routePourNotification(actionUrl: string | undefined, role: Role): string {
  const base = baseRouteForRole(role);
  if (!actionUrl) return `${base}/dashboard`;
  if (actionUrl.startsWith('/messages')) return `${base}/messagerie`;
  if (actionUrl.startsWith('/meetings')) return `${base}/reunions`;
  if (actionUrl.startsWith('/tasks')) return role === 'Encadrant' ? `${base}/validation` : `${base}/taches`;
  return `${base}/dashboard`;
}

export function Navbar({ title }: { title: string }) {
  const { role, profil, logout } = useRole();
  const navigate = useNavigate();

  const [showNotifications, setShowNotifications] = useState(false);
  const [showUserMenu, setShowUserMenu] = useState(false);
  const [notifications, setNotifications] = useState<NotificationDTO[]>([]);

  const notifRef = useRef<HTMLDivElement>(null);
  const userRef = useRef<HTMLDivElement>(null);

  const unreadCount = notifications.filter((n) => !n.isRead).length;

  const charger = () => {
    // MODIF : centre de notifications réel (les 30 dernières) au lieu d'une
    // liste factice codée en dur — la messagerie/réunions/module2 alimentent
    // ce même flux (cahier des charges §6.2.2).
    notificationService.getCenter().then(setNotifications).catch(() => setNotifications([]));
  };

  useEffect(() => {
    charger();
    // Rafraîchissement périodique : les notifications n'étaient chargées qu'une fois au montage.
    const timer = setInterval(charger, 20000);
    return () => clearInterval(timer);
  }, []);

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

    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, []);

  const handleNotificationClick = async (notif: NotificationDTO) => {
    setShowNotifications(false);

    try {
      if (!notif.isRead) {
        await notificationService.markReadByAction(notif.id);
        setNotifications((prev) =>
          prev.map((n) => (n.id === notif.id ? { ...n, isRead: true } : n))
        );
      }

      navigate(routePourNotification(notif.actionUrl, role));
    } catch (error) {
      console.error('Failed to mark notification as read:', error);
    }
  };

  const marquerToutesLues = async () => {
    try {
      await notificationService.markAllAsRead();
      setNotifications((prev) => prev.map((n) => ({ ...n, isRead: true })));
    } catch (error) {
      console.error('Failed to mark all as read:', error);
    }
  };

  const handleLogout = () => {
    logout();
    navigate('/auth/login');
  };

  const initiales = (profil?.nomComplet || '?')
    .split(' ')
    .map((m) => m[0])
    .join('')
    .slice(0, 2)
    .toUpperCase();

  return (
    <div className="h-16 bg-white border-b border-gray-200 px-6 flex items-center justify-between">
      <h2 className="text-xl font-semibold text-gray-800">{title}</h2>

      <div className="flex items-center gap-4">
        <div className="relative" ref={notifRef}>
          <button
            onClick={() => setShowNotifications((prev) => !prev)}
            className="relative p-2 text-gray-600 hover:bg-gray-100 rounded-lg"
          >
            <Bell size={20} />

            {unreadCount > 0 && (
              <span className="absolute top-1 right-1 w-5 h-5 bg-red-500 text-white text-xs rounded-full flex items-center justify-center">
                {unreadCount}
              </span>
            )}
          </button>

          {showNotifications && (
            <div className="absolute right-0 mt-2 w-80 bg-white border border-gray-200 rounded-lg shadow-lg z-50">
              <div className="p-4 border-b border-gray-200 flex items-center justify-between">
                <h3 className="font-semibold">Notifications</h3>
                {unreadCount > 0 && (
                  <button onClick={marquerToutesLues} className="text-xs text-[#1F4E79] hover:underline">
                    Tout marquer lu
                  </button>
                )}
              </div>

              <div className="max-h-96 overflow-y-auto">
                {notifications.length === 0 ? (
                  <p className="p-4 text-center text-gray-500 text-sm">
                    Aucune notification
                  </p>
                ) : (
                  notifications.map((notif) => (
                    <button
                      key={notif.id}
                      onClick={() => handleNotificationClick(notif)}
                      className={`w-full p-4 hover:bg-gray-50 border-b border-gray-100 text-left ${
                        !notif.isRead ? 'bg-blue-50' : ''
                      }`}
                    >
                      <p
                        className={`text-sm ${
                          !notif.isRead ? 'font-semibold' : ''
                        } text-gray-800`}
                      >
                        {notif.message}
                      </p>

                      <p className="text-xs text-gray-500 mt-1">
                        {new Date(notif.createdAt).toLocaleString('fr-FR', {
                          day: '2-digit',
                          month: 'short',
                          hour: '2-digit',
                          minute: '2-digit',
                        })}
                      </p>
                    </button>
                  ))
                )}
              </div>
            </div>
          )}
        </div>

        <div className="relative" ref={userRef}>
          <button
            onClick={() => setShowUserMenu((prev) => !prev)}
            className="flex items-center gap-2 p-2 hover:bg-gray-100 rounded-lg"
          >
            <div className="w-8 h-8 rounded-full bg-[#1F4E79] flex items-center justify-center text-white text-sm">
              {initiales}
            </div>

            <ChevronDown size={16} className="text-gray-600" />
          </button>

          {showUserMenu && (
            <div className="absolute right-0 mt-2 w-48 bg-white border border-gray-200 rounded-lg shadow-lg z-50">
              <button
                onClick={() => {
                  setShowUserMenu(false);
                  navigate(`${baseRouteForRole(role)}/profil`);
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
