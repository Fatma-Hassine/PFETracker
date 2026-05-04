// RoleSwitcher.tsx
import { useRole, Role } from '../contexts/RoleContext';
import { useNavigate } from 'react-router-dom';  // ✅ corrigé


export function RoleSwitcher() {
  const { role, setRole } = useRole();
  const navigate = useNavigate();

  const roles: Role[] = ['Étudiant', 'Encadrant', 'Responsable', 'Directeur'];

  const handleRoleChange = (newRole: Role) => {
    setRole(newRole);
    switch (newRole) {
      case 'Étudiant':
        navigate('/etudiant/dashboard');
        break;
      case 'Encadrant':
        navigate('/encadrant/dashboard');
        break;
      case 'Responsable':
        navigate('/responsable/dashboard');
        break;
      case 'Directeur':
        navigate('/directeur/dashboard');
        break;
    }
  };

  return (
    <div className="fixed top-4 right-4 z-50 bg-white border border-gray-300 rounded-full px-2 py-1 shadow-lg flex gap-1">
      {roles.map((r) => (
        <button
          key={r}
          onClick={() => handleRoleChange(r)}
          className={`px-3 py-1 rounded-full text-sm transition-colors ${
            role === r
              ? 'bg-[#1F4E79] text-white'
              : 'text-gray-600 hover:bg-gray-100'
          }`}
        >
          {r}
        </button>
      ))}
    </div>
  );
}