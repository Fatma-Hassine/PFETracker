import { useEffect, useState } from 'react';
import { Lock, Camera } from 'lucide-react';
import { toast } from 'sonner';
import { apiRequest } from '../../../services/api';

type ProfilEtudiant = {
  id: number;
  nomComplet: string;
  email: string;
  telephone?: string;
  departementNom?: string;
  niveauEtudes?: string;
  encadrantId?: number;
  encadrantNom?: string;
};

export function EtudiantProfil() {
  const [isEditing, setIsEditing] = useState(false);
  const [chargement, setChargement] = useState(true);
  const [profile, setProfile] = useState<ProfilEtudiant | null>(null);
  const [brouillon, setBrouillon] = useState({ nomComplet: '', telephone: '' });

  const [passwords, setPasswords] = useState({
    current: '',
    new: '',
    confirm: '',
  });

  const charger = () => {
    apiRequest<ProfilEtudiant>('/utilisateurs/moi')
      .then((p) => {
        setProfile(p);
        setBrouillon({ nomComplet: p.nomComplet, telephone: p.telephone || '' });
      })
      .catch(() => toast.error('Impossible de charger le profil'))
      .finally(() => setChargement(false));
  };

  useEffect(() => {
    charger();
  }, []);

  const handleSave = async () => {
    try {
      await apiRequest<void>('/utilisateurs/moi', {
        method: 'PUT',
        body: JSON.stringify({
          nomComplet: brouillon.nomComplet,
          telephone: brouillon.telephone,
        }),
      });
      setIsEditing(false);
      toast.success('Profil mis à jour avec succès');
      charger();
    } catch {
      toast.error('Échec de la mise à jour du profil');
    }
  };

  const handleChangePassword = async () => {
    if (passwords.new !== passwords.confirm) {
      toast.error('Les mots de passe ne correspondent pas');
      return;
    }
    try {
      await apiRequest<void>('/auth/changer-mot-de-passe', {
        method: 'POST',
        body: JSON.stringify({
          ancienMotDePasse: passwords.current,
          nouveauMotDePasse: passwords.new,
        }),
      });
      toast.success('Mot de passe changé avec succès');
      setPasswords({ current: '', new: '', confirm: '' });
    } catch (e) {
      toast.error(e instanceof Error ? e.message : 'Échec du changement de mot de passe');
    }
  };

  if (chargement) {
    return <p className="text-gray-600">Chargement du profil...</p>;
  }

  if (!profile) {
    return <p className="text-red-600">Impossible de charger le profil.</p>;
  }

  const initiales = profile.nomComplet
    .split(' ')
    .map((m) => m[0])
    .join('')
    .slice(0, 2)
    .toUpperCase();

  return (
    <div className="space-y-6">
      <div className="bg-white rounded-lg p-6 border border-gray-200">
        <div className="flex items-start gap-6 mb-6">
          <div className="relative">
            <div className="w-24 h-24 rounded-full bg-[#1F4E79] flex items-center justify-center text-white text-3xl">
              {initiales}
            </div>
            <button className="absolute bottom-0 right-0 w-8 h-8 bg-white border border-gray-300 rounded-full flex items-center justify-center hover:bg-gray-50">
              <Camera size={16} />
            </button>
          </div>
          <div className="flex-1">
            <h2 className="text-2xl font-bold mb-2">{profile.nomComplet}</h2>
            <p className="text-gray-600">{profile.niveauEtudes || 'Niveau non renseigné'}</p>
          </div>
          {!isEditing ? (
            <button
              onClick={() => setIsEditing(true)}
              className="px-4 py-2 bg-[#1F4E79] text-white rounded-lg hover:bg-[#163A5C]"
            >
              Modifier
            </button>
          ) : (
            <div className="flex gap-2">
              <button
                onClick={() => {
                  setIsEditing(false);
                  setBrouillon({ nomComplet: profile.nomComplet, telephone: profile.telephone || '' });
                }}
                className="px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50"
              >
                Annuler
              </button>
              <button
                onClick={handleSave}
                className="px-4 py-2 bg-[#1F4E79] text-white rounded-lg hover:bg-[#163A5C]"
              >
                Sauvegarder
              </button>
            </div>
          )}
        </div>

        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-600 mb-1">Nom complet</label>
            {isEditing ? (
              <input
                type="text"
                value={brouillon.nomComplet}
                onChange={(e) => setBrouillon({ ...brouillon, nomComplet: e.target.value })}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg"
              />
            ) : (
              <p className="text-gray-800">{profile.nomComplet}</p>
            )}
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-600 mb-1">Téléphone</label>
            {isEditing ? (
              <input
                type="text"
                value={brouillon.telephone}
                onChange={(e) => setBrouillon({ ...brouillon, telephone: e.target.value })}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg"
              />
            ) : (
              <p className="text-gray-800">{profile.telephone || '—'}</p>
            )}
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-600 mb-1 flex items-center gap-2">
              Email
              <Lock size={14} className="text-gray-400" />
            </label>
            <p className="text-gray-800">{profile.email}</p>
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-600 mb-1 flex items-center gap-2">
              Département
              <Lock size={14} className="text-gray-400" />
            </label>
            <p className="text-gray-800">{profile.departementNom || '—'}</p>
          </div>
        </div>
      </div>

      <div className="bg-white rounded-lg p-6 border border-gray-200">
        <h3 className="text-lg font-semibold mb-4">Encadrant</h3>
        {profile.encadrantNom ? (
          <div className="flex items-center gap-4 p-4 bg-gray-50 rounded-lg">
            <div className="w-16 h-16 rounded-full bg-[#1F4E79] flex items-center justify-center text-white text-xl">
              {profile.encadrantNom
                .split(' ')
                .map((m) => m[0])
                .join('')
                .slice(0, 2)
                .toUpperCase()}
            </div>
            <div className="flex-1">
              <h4 className="font-medium">{profile.encadrantNom}</h4>
            </div>
          </div>
        ) : (
          <p className="text-gray-500 text-sm">
            Aucun encadrant affecté pour le moment — en attente de validation par le chef de département.
          </p>
        )}
      </div>

      <div className="bg-white rounded-lg p-6 border border-gray-200">
        <h3 className="text-lg font-semibold mb-4">Changer le mot de passe</h3>
        <div className="space-y-4 max-w-md">
          <div>
            <label className="block text-sm font-medium text-gray-600 mb-1">
              Mot de passe actuel
            </label>
            <input
              type="password"
              value={passwords.current}
              onChange={(e) => setPasswords({ ...passwords, current: e.target.value })}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-600 mb-1">
              Nouveau mot de passe
            </label>
            <input
              type="password"
              value={passwords.new}
              onChange={(e) => setPasswords({ ...passwords, new: e.target.value })}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-600 mb-1">
              Confirmer le mot de passe
            </label>
            <input
              type="password"
              value={passwords.confirm}
              onChange={(e) => setPasswords({ ...passwords, confirm: e.target.value })}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg"
            />
          </div>
          <button
            onClick={handleChangePassword}
            disabled={!passwords.current || !passwords.new || !passwords.confirm}
            className="px-4 py-2 bg-[#1F4E79] text-white rounded-lg hover:bg-[#163A5C] disabled:opacity-50 disabled:cursor-not-allowed"
          >
            Changer le mot de passe
          </button>
        </div>
      </div>
    </div>
  );
}
