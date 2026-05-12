import { API_BASE_URL } from './config';

/**
 * Authentifie l'agent et retourne le token JWT.
 */
export async function login(email, password) {
  const response = await fetch(`${API_BASE_URL}/auth/login`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      Accept: 'application/json',
    },
    body: JSON.stringify({
      email: email.trim().toLowerCase(),
      password: password.trim(),
    }),
  });

  const text = await response.text();

  if (!response.ok) {
    throw new Error(`Erreur login HTTP ${response.status} : ${text}`);
  }

  const data = JSON.parse(text);
  return data.token;
}

/**
 * Valide un billet avec le contexte du trajet contrôlé.
 */
export async function validateTicket(ticketId, token, controlContext) {
  const response = await fetch(`${API_BASE_URL}/control/validate`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      Accept: 'application/json',
      Authorization: `Bearer ${token}`,
    },
    body: JSON.stringify({
      ticketId,
      departureStationCode: controlContext.departureStationCode,
      arrivalStationCode: controlContext.arrivalStationCode,
    }),
  });

  const text = await response.text();

  if (response.status === 401 || response.status === 403) {
    throw new Error('Session expirée ou accès refusé.');
  }

  if (!response.ok) {
    throw new Error(`Erreur validation HTTP ${response.status} : ${text}`);
  }

  return JSON.parse(text);
}

/**
 * Récupère la liste des stations depuis le backend.
 */
export async function getStations() {
  const STATIONS = [
  { code: 'PARIS', name: 'Paris' },
  { code: 'LYON', name: 'Lyon' },
  { code: 'LILLE', name: 'Lille' },
  { code: 'NANTES', name: 'Nantes' },
  { code: 'BORDEAUX', name: 'Bordeaux' },
  { code: 'MARSEILLE', name: 'Marseille' },
];
 return STATIONS;
}

/**
 * Récupère les trajets disponibles entre deux stations.
 */
export async function searchTrips(departureStationCode, arrivalStationCode, date) {
  const response = await fetch(
    `${API_BASE_URL}/trips/search?from=${encodeURIComponent(departureStationCode)}&to=${encodeURIComponent(arrivalStationCode)}&date=${encodeURIComponent(date)}`,
    {
      method: 'GET',
      headers: {
        Accept: 'application/json',
      },
    }
  );

  const text = await response.text();

  if (!response.ok) {
    throw new Error(`Erreur chargement trajets HTTP ${response.status} : ${text}`);
  }

  return JSON.parse(text);
}