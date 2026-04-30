const API_BASE = 'http://localhost:8080';

// ---------- Auth storage ----------
const Auth = {
  getToken: () => localStorage.getItem('ticketeer_token'),
  getUser:  () => JSON.parse(localStorage.getItem('ticketeer_user') || 'null'),
  setSession(token, user) {
    localStorage.setItem('ticketeer_token', token);
    localStorage.setItem('ticketeer_user', JSON.stringify(user));
  },
  clear() {
    localStorage.removeItem('ticketeer_token');
    localStorage.removeItem('ticketeer_user');
  },
  isLoggedIn: () => !!localStorage.getItem('ticketeer_token'),
};

// ---------- HTTP helper ----------
async function http(method, path, body) {
  const headers = { 'Content-Type': 'application/json' };
  const token = Auth.getToken();
  if (token) headers['Authorization'] = `Bearer ${token}`;

  const res = await fetch(`${API_BASE}${path}`, {
    method,
    headers,
    body: body ? JSON.stringify(body) : undefined,
  });

  if (res.status === 401) {
    Auth.clear();
    window.location.href = 'index.html';
    throw new Error('Session expired');
  }

  if (!res.ok) {
    const err = await res.json().catch(() => ({ message: 'Request failed' }));
    throw new Error(err.message || `HTTP ${res.status}`);
  }

  if (res.status === 204 || res.headers.get('Content-Length') === '0') return null;
  return res.json();
}

async function httpBlob(method, path) {
  const headers = {};
  const token = Auth.getToken();
  if (token) headers['Authorization'] = `Bearer ${token}`;

  const res = await fetch(`${API_BASE}${path}`, { method, headers });
  if (!res.ok) throw new Error(`HTTP ${res.status}`);
  return res.blob();
}

// ---------- API ----------
const api = {
  auth: {
    login: (email, password) => http('POST', '/auth/login', { email, password }),
  },

  trips: {
    search: (from, to, date) =>
      http('GET', `/trips/search?from=${encodeURIComponent(from)}&to=${encodeURIComponent(to)}&date=${date}`),
  },

  tickets: {
    create: (body)        => http('POST', '/tickets', body),
    myTickets: ()         => http('GET', '/tickets/me'),
    qr: (id)              => httpBlob('GET', `/tickets/${id}/qr`),
    pdf: (id)             => httpBlob('GET', `/tickets/${id}/pdf`),
  },

  control: {
    validate: (ticketId)  => http('POST', '/control/validate', { ticketId }),
  },
};

// ---------- JWT decode (claims only, no verify) ----------
function decodeJwtPayload(token) {
  try {
    return JSON.parse(atob(token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/')));
  } catch {
    return null;
  }
}

// ---------- Format helpers ----------
function formatDatetime(iso) {
  if (!iso) return '-';
  return new Date(iso).toLocaleString('fr-FR', { dateStyle: 'short', timeStyle: 'short' });
}

function formatDate(iso) {
  if (!iso) return '-';
  return new Date(iso).toLocaleDateString('fr-FR');
}

function formatPrice(price) {
  return `${Number(price).toFixed(2)} €`;
}

function statusBadge(status) {
  const map = {
    VALID: 'badge-success',
    CREATED: 'badge-primary',
    EXPIRED: 'badge-danger',
  };
  return `<span class="badge ${map[status] || 'badge-gray'}">${status}</span>`;
}

function showAlert(container, type, message) {
  const el = document.getElementById(container);
  if (!el) return;
  el.innerHTML = `<div class="alert alert-${type}">${message}</div>`;
  setTimeout(() => { if (el) el.innerHTML = ''; }, 5000);
}

function downloadBlob(blob, filename) {
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url; a.download = filename; a.click();
  URL.revokeObjectURL(url);
}
