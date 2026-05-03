export type DemoRole =
  | 'STUDENT'
  | 'SUPERVISOR'
  | 'ENCADRANT'
  | 'RESPONSABLE'
  | 'DIRECTEUR';

export function getTemporaryAuthHeaders() {
  const userId = localStorage.getItem('demoUserId') || '1';
  const userRole = localStorage.getItem('demoUserRole') || 'STUDENT';

  return {
    'X-User-Id': userId,
    'X-User-Role': userRole,
  };
}

export function setDemoUser(userId: string, role: DemoRole) {
  localStorage.setItem('demoUserId', userId);
  localStorage.setItem('demoUserRole', role);
  window.location.reload();
}