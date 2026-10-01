import { useEffect, useState } from 'react';
import { apiRequest } from '../../services/api';

/**
 * Récupère le département du chef de département connecté depuis le vrai
 * profil (/utilisateurs/moi) au lieu de faire confiance à une valeur stockée
 * côté client — le backend vérifie désormais que {deptId} dans l'URL
 * correspond bien au département réel de l'appelant (isolation stricte,
 * cahier des charges §4.1.3), donc une valeur locale obsolète ou par défaut
 * ferait échouer tous les appels avec un 403.
 */
export function useMonDepartementId(): number | null {
  const [deptId, setDeptId] = useState<number | null>(null);

  useEffect(() => {
    apiRequest<{ departementId?: number }>('/utilisateurs/moi')
      .then((u) => setDeptId(u.departementId ?? null))
      .catch(() => setDeptId(null));
  }, []);

  return deptId;
}
