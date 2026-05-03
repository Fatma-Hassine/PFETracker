import { useState } from 'react';
import { useNavigate } from 'react-router';
import { useRole } from '../../contexts/RoleContext';
import { Eye, EyeOff, AlertTriangle } from 'lucide-react';

export function ChangePasswordPage() {
  const navigate = useNavigate();
  const { login, role } = useRole();
  const [currentPassword, setCurrentPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [showCurrent, setShowCurrent] = useState(false);
  const [showNew, setShowNew] = useState(false);
  const [showConfirm, setShowConfirm] = useState(false);

  const getPasswordStrength = (password: string): string => {
    if (!password) return '';
    if (password.length < 6) return 'Faible';
    if (password.length < 10) return 'Moyen';
    return 'Fort';
  };

  const strength = getPasswordStrength(newPassword);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (newPassword === confirmPassword) {
      switch (role) {
        case 'Étudiant':
          navigate('/etudiant/dashboard');
          break;
        case 'Encadrant':
          navigate('/encadrant/dashboard');
          break;
        case 'Responsable':
          navigate('/admin/dashboard');
          break;
        case 'Directeur':
          navigate('/directeur/dashboard');
          break;
      }
    }
  };

  return (
    <div className="w-full max-w-md bg-white rounded-lg shadow-lg p-8">
      <div className="bg-yellow-50 border border-yellow-300 text-yellow-800 p-4 rounded-lg mb-6 flex items-start gap-3">
        <AlertTriangle size={20} className="flex-shrink-0 mt-0.5" />
        <div>
          <p className="font-semibold">Première connexion</p>
          <p className="text-sm">Vous devez changer votre mot de passe</p>
        </div>
      </div>

      <form onSubmit={handleSubmit} className="space-y-4">
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Mot de passe actuel
          </label>
          <div className="relative">
            <input
              type={showCurrent ? 'text' : 'password'}
              value={currentPassword}
              onChange={(e) => setCurrentPassword(e.target.value)}
              required
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#1F4E79] pr-10"
            />
            <button
              type="button"
              onClick={() => setShowCurrent(!showCurrent)}
              className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500"
            >
              {showCurrent ? <EyeOff size={18} /> : <Eye size={18} />}
            </button>
          </div>
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Nouveau mot de passe
          </label>
          <div className="relative">
            <input
              type={showNew ? 'text' : 'password'}
              value={newPassword}
              onChange={(e) => setNewPassword(e.target.value)}
              required
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#1F4E79] pr-10"
            />
            <button
              type="button"
              onClick={() => setShowNew(!showNew)}
              className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500"
            >
              {showNew ? <EyeOff size={18} /> : <Eye size={18} />}
            </button>
          </div>
          {newPassword && (
            <div className="mt-2 flex items-center gap-2">
              <span className="text-sm text-gray-600">Force:</span>
              <span
                className={`text-sm font-medium ${
                  strength === 'Faible'
                    ? 'text-red-500'
                    : strength === 'Moyen'
                    ? 'text-yellow-500'
                    : 'text-green-500'
                }`}
              >
                {strength}
              </span>
            </div>
          )}
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Confirmer le mot de passe
          </label>
          <div className="relative">
            <input
              type={showConfirm ? 'text' : 'password'}
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
              required
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#1F4E79] pr-10"
            />
            <button
              type="button"
              onClick={() => setShowConfirm(!showConfirm)}
              className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500"
            >
              {showConfirm ? <EyeOff size={18} /> : <Eye size={18} />}
            </button>
          </div>
          {confirmPassword && newPassword !== confirmPassword && (
            <p className="text-red-500 text-sm mt-1">Les mots de passe ne correspondent pas</p>
          )}
        </div>

        <button
          type="submit"
          disabled={!newPassword || newPassword !== confirmPassword}
          className="w-full bg-[#1F4E79] text-white py-2 rounded-lg hover:bg-[#163A5C] transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
        >
          Changer le mot de passe
        </button>
      </form>
    </div>
  );
}
