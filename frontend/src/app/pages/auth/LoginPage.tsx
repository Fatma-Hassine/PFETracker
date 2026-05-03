import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router';
import { useRole } from '../../contexts/RoleContext';
import { Eye, EyeOff } from 'lucide-react';
import authService from '../../../api/authService';

export function LoginPage() {
  const navigate = useNavigate();
  const { login, isAuthenticated, user } = useRole();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [errors, setErrors] = useState({ email: '', password: '', form: '' });
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    if (isAuthenticated && user) {
      const roleMap: Record<string, string> = {
        'STUDENT': '/etudiant/dashboard',
        'SUPERVISOR': '/encadrant/dashboard',
        'DEPT_MANAGER': '/admin/dashboard',
        'DIRECTOR': '/directeur/dashboard'
      };
      navigate(roleMap[user.role] || '/etudiant/dashboard');
    }
  }, [isAuthenticated, user, navigate]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    const newErrors = { email: '', password: '', form: '' };
    if (!email) newErrors.email = 'Email requis';

    // Pour l'instant, le backend n'exige pas de mot de passe (Mock) 
    // Mais on garde la validation frontend au cas où.
    if (!password) newErrors.password = 'Mot de passe requis';

    if (newErrors.email || newErrors.password) {
      setErrors(newErrors);
      return;
    }

    setIsLoading(true);
    setErrors({ ...errors, form: '' });

    try {
      const response = await authService.login({ email, password });
      login(response);

      const roleMap: Record<string, string> = {
        'STUDENT': '/etudiant/dashboard',
        'SUPERVISOR': '/encadrant/dashboard',
        'DEPT_MANAGER': '/admin/dashboard',
        'DIRECTOR': '/directeur/dashboard'
      };

      const redirectUrl = roleMap[response.role] || '/etudiant/dashboard';
      navigate(redirectUrl);
    } catch (error: any) {
      console.error('Login failed:', error);
      setErrors({
        ...errors,
        form: error?.response?.data || 'Identifiants incorrects ou problème de connexion au serveur.'
      });
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="w-full max-w-md bg-white rounded-lg shadow-lg p-8">
      <div className="text-center mb-8">
        <h1 className="text-3xl font-bold text-[#1F4E79] mb-2">PFETracker</h1>
        <p className="text-gray-600">Connectez-vous à votre compte</p>
      </div>

      <form onSubmit={handleSubmit} className="space-y-4">
        {errors.form && (
          <div className="bg-red-50 text-red-500 p-3 rounded-lg text-sm text-center">
            {errors.form}
          </div>
        )}

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Email</label>
          <input
            type="email"
            value={email}
            onChange={(e) => {
              setEmail(e.target.value);
              setErrors({ ...errors, email: '', form: '' });
            }}
            placeholder="votre@univ.tn"
            className={`w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 ${errors.email ? 'border-red-500' : 'border-gray-300 focus:ring-[#1F4E79]'
              }`}
          />
          {errors.email && <p className="text-red-500 text-sm mt-1">{errors.email}</p>}
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Mot de passe</label>
          <div className="relative">
            <input
              type={showPassword ? 'text' : 'password'}
              value={password}
              onChange={(e) => {
                setPassword(e.target.value);
                setErrors({ ...errors, password: '', form: '' });
              }}
              placeholder="••••••••"
              className={`w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 pr-10 ${errors.password ? 'border-red-500' : 'border-gray-300 focus:ring-[#1F4E79]'
                }`}
            />
            <button
              type="button"
              onClick={() => setShowPassword(!showPassword)}
              className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500"
            >
              {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
            </button>
          </div>
          {errors.password && <p className="text-red-500 text-sm mt-1">{errors.password}</p>}
        </div>

        <button
          type="submit"
          disabled={isLoading}
          className={`w-full text-white py-2 rounded-lg transition-colors ${isLoading ? 'bg-[#163A5C] opacity-75 cursor-not-allowed' : 'bg-[#1F4E79] hover:bg-[#163A5C]'
            }`}
        >
          {isLoading ? 'Connexion en cours...' : 'Se connecter'}
        </button>

        <div className="text-center">
          <button
            type="button"
            onClick={() => navigate('/auth/forgot-password')}
            className="text-[#1F4E79] hover:underline text-sm"
          >
            Mot de passe oublié ?
          </button>
        </div>

        <div className="text-center pt-4 border-t border-gray-200">
          <p className="text-sm text-gray-600">
            Pas de compte ?{' '}
            <button
              type="button"
              onClick={() => navigate('/auth/register')}
              className="text-[#1F4E79] hover:underline"
            >
              Créer un compte
            </button>
          </p>
        </div>
      </form>
    </div>
  );
}
