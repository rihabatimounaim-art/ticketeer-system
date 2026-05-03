import { API_BASE_URL } from './config';

/**
 * Authentifie l'agent et retourne le token JWT.
 */
export async function login(email, password) {
  const response = await fetch(`${API_BASE_URL}/auth/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email, password }),
  });

  if (!response.ok) {
    throw new Error('Identifiants invalides. Vérifiez votre email et mot de passe.');
  }

  const data = await response.json();
  return data.token;
}

/**
 * Valide un billet via son ID et retourne le résultat :
 * "VALID" | "EXPIRED" | "ALREADY_CONTROLLED"
 */
export async function validateTicket(ticketId, token) {
  const response = await fetch(`${API_BASE_URL}/control/validate`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`,
    },
    body: JSON.stringify({ ticketId }),
  });

  if (response.status === 401) {
    throw new Error('SESSION_EXPIRED');
  }

  if (!response.ok) {
    throw new Error('Erreur lors de la validation du billet.');
  }

  const data = await response.json();
  return data.result;
}
