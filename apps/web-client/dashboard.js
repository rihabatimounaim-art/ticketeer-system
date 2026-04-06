const API_BASE = "http://localhost:8080";
const token = localStorage.getItem("ticketeer_token");

if (!token) {
  window.location.href = "login.html";
}

const parseJwt = (jwt) => {
  const base64Url = jwt.split(".")[1];
  const base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/");
  return JSON.parse(window.atob(base64));
};

const claims = parseJwt(token);
const userId = claims.sub;
const role = claims.role;
const email = claims.email;

const authHeaders = () => ({
  "Content-Type": "application/json",
  "Authorization": `Bearer ${token}`
});

const setStatus = (id, text, type = "info") => {
  const el = document.getElementById(id);
  el.innerText = text;
  el.className = `status-line ${type}`;
};

const getStatusBadgeClass = (status) => {
  if (status === "VALID") return "status-badge status-valid";
  if (status === "EXPIRED") return "status-badge status-expired";
  return "status-badge status-default";
};

const sections = document.querySelectorAll(".content-section");
const navLinks = document.querySelectorAll(".nav-link");

navLinks.forEach((btn) => {
  btn.addEventListener("click", () => {
    navLinks.forEach((b) => b.classList.remove("active"));
    sections.forEach((s) => s.classList.remove("active"));

    btn.classList.add("active");
    document.getElementById(btn.dataset.section).classList.add("active");
  });
});

document.getElementById("userBadge").innerText = `${role} • ${email}`;

document.getElementById("logoutBtn").onclick = () => {
  localStorage.removeItem("ticketeer_token");
  window.location.href = "login.html";
};

document.getElementById("searchTripsBtn").onclick = async () => {
  setStatus("tripSearchStatus", "Searching trips...", "info");

  try {
    const from = document.getElementById("fromStation").value;
    const to = document.getElementById("toStation").value;
    const date = document.getElementById("tripDate").value;

    if (from === to) {
      throw new Error("Departure and destination must be different.");
    }

    const res = await fetch(`${API_BASE}/trips/search?from=${from}&to=${to}&date=${date}`);
    const data = await res.json();

    if (!res.ok) {
      throw new Error("Trip search failed");
    }

    const container = document.getElementById("tripResults");

    if (!data.length) {
      container.className = "trip-list empty-state";
      container.innerHTML = "No trip found for the selected search.";
      setStatus("tripSearchStatus", "No trip found.", "info");
      return;
    }

    container.className = "trip-list";
    container.innerHTML = data.map(trip => `
      <div class="trip-card">
        <div class="trip-card-left">
          <div class="trip-route">🚆 ${trip.from} → ${trip.to}</div>
          <div class="trip-time">${trip.departureTime} → ${trip.arrivalTime}</div>
          <div class="trip-badges">
            <span class="trip-badge">Direct trip</span>
            <span class="trip-badge">Date: ${trip.departureTime.slice(0, 10)}</span>
          </div>
        </div>
        <div class="trip-card-right">
          <div class="trip-price">${trip.price} €</div>
        </div>
      </div>
    `).join("");

    setStatus("tripSearchStatus", `${data.length} trip(s) found.`, "success");
  } catch (error) {
    setStatus("tripSearchStatus", error.message, "error");
  }
};

const loadMyTickets = async () => {
  try {
    const res = await fetch(`${API_BASE}/tickets/me`, {
      headers: { "Authorization": `Bearer ${token}` }
    });

    const data = await res.json();

    if (!res.ok) {
      throw new Error("Failed to load tickets");
    }

    const container = document.getElementById("ticketsList");

    if (!data.length) {
      container.className = "ticket-list empty-state";
      container.innerHTML = "No ticket found for this user.";
      return;
    }

    container.className = "ticket-list";
    container.innerHTML = data.map(ticket => `
      <div class="ticket-card">
        <div class="ticket-card-header">
          <div>
            <div class="ticket-id">🎫 Ticket ${ticket.ticketId}</div>
            <div class="${getStatusBadgeClass(ticket.status)}">${ticket.status}</div>
          </div>
        </div>
        <div class="ticket-meta">
          <div><strong>Valid from:</strong> ${ticket.validFrom}</div>
          <div><strong>Valid until:</strong> ${ticket.validUntil}</div>
          <div><strong>Issued at:</strong> ${ticket.issuedAt}</div>
        </div>
        <div class="ticket-actions">
          <button class="btn btn-secondary" onclick="downloadQr('${ticket.ticketId}')">Download QR</button>
          <button class="btn btn-primary" onclick="downloadPdf('${ticket.ticketId}')">Download PDF</button>
          <button class="btn btn-ghost" onclick="fillControlTicket('${ticket.ticketId}')">Use for control</button>
        </div>
      </div>
    `).join("");
  } catch (error) {
    setStatus("ticketStatus", error.message, "error");
  }
};

document.getElementById("loadTicketsBtn").onclick = loadMyTickets;

document.getElementById("createTicketBtn").onclick = async () => {
  setStatus("ticketStatus", "Creating ticket...", "info");

  try {
    const validFrom = document.getElementById("validFrom").value.trim();
    const validUntil = document.getElementById("validUntil").value.trim();

    const res = await fetch(`${API_BASE}/tickets`, {
      method: "POST",
      headers: authHeaders(),
      body: JSON.stringify({
        holderId: userId,
        validFrom,
        validUntil
      })
    });

    const data = await res.json();

    if (!res.ok) {
      throw new Error(data.message || "Ticket creation failed");
    }

    setStatus("ticketStatus", `Ticket created successfully (${data.status}).`, "success");
    await loadMyTickets();
  } catch (error) {
    setStatus("ticketStatus", error.message, "error");
  }
};

window.downloadQr = async (ticketId) => {
  const res = await fetch(`${API_BASE}/tickets/${ticketId}/qr`, {
    headers: { "Authorization": `Bearer ${token}` }
  });

  if (!res.ok) {
    alert("Failed to download QR.");
    return;
  }

  const blob = await res.blob();
  const url = window.URL.createObjectURL(blob);
  const a = document.createElement("a");
  a.href = url;
  a.download = `ticket-${ticketId}-qr.png`;
  a.click();
  window.URL.revokeObjectURL(url);
};

window.downloadPdf = async (ticketId) => {
  const res = await fetch(`${API_BASE}/tickets/${ticketId}/pdf`, {
    headers: { "Authorization": `Bearer ${token}` }
  });

  if (!res.ok) {
    alert("Failed to download PDF.");
    return;
  }

  const blob = await res.blob();
  const url = window.URL.createObjectURL(blob);
  const a = document.createElement("a");
  a.href = url;
  a.download = `ticket-${ticketId}.pdf`;
  a.click();
  window.URL.revokeObjectURL(url);
};

window.fillControlTicket = (ticketId) => {
  document.getElementById("controlTicketId").value = ticketId;
  document.querySelector('[data-section="controlSection"]').click();
};

document.getElementById("validateTicketBtn").onclick = async () => {
  setStatus("controlStatus", "Validating ticket...", "info");

  try {
    const ticketId = document.getElementById("controlTicketId").value.trim();

    const res = await fetch(`${API_BASE}/control/validate`, {
      method: "POST",
      headers: authHeaders(),
      body: JSON.stringify({ ticketId })
    });

    const data = await res.json();

    if (!res.ok) {
      throw new Error(data.message || "Ticket validation failed");
    }

    const card = document.getElementById("controlResultCard");
    card.classList.remove("hidden");

    let cardClass = "result-card info-card";
    if (data.result === "VALID") cardClass = "result-card success-card";
    if (data.result === "EXPIRED" || data.result === "INVALID") cardClass = "result-card error-card";

    card.className = cardClass;
    card.innerHTML = `<strong>Validation result:</strong> ${data.result}`;

    let type = "info";
    if (data.result === "VALID") type = "success";
    if (data.result === "EXPIRED" || data.result === "INVALID") type = "error";

    setStatus("controlStatus", "Validation completed.", type);
  } catch (error) {
    setStatus("controlStatus", error.message, "error");
  }
};

loadMyTickets();
