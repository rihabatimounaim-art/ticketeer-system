const API_BASE = "http://localhost:8080";
let token = null;

document.getElementById("loginBtn").onclick = async () => {
  const email = document.getElementById("email").value;
  const password = document.getElementById("password").value;

  const res = await fetch(`${API_BASE}/auth/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ email, password })
  });

  const data = await res.json();
  token = data.token;

  document.getElementById("loginResult").innerText = JSON.stringify(data, null, 2);
};

document.getElementById("createTicketBtn").onclick = async () => {
  const holderId = document.getElementById("holderId").value;
  const validFrom = document.getElementById("validFrom").value;
  const validUntil = document.getElementById("validUntil").value;

  const res = await fetch(`${API_BASE}/tickets`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      "Authorization": `Bearer ${token}`
    },
    body: JSON.stringify({ holderId, validFrom, validUntil })
  });

  const data = await res.json();
  document.getElementById("ticketResult").innerText = JSON.stringify(data, null, 2);

  if (data.ticketId) {
    document.getElementById("ticketId").value = data.ticketId;
  }
};

document.getElementById("validateTicketBtn").onclick = async () => {
  const ticketId = document.getElementById("ticketId").value;
  const agentId = document.getElementById("agentId").value;

  const res = await fetch(`${API_BASE}/control/validate`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      "Authorization": `Bearer ${token}`
    },
    body: JSON.stringify({ ticketId, agentId })
  });

  const data = await res.json();
  document.getElementById("controlResult").innerText = JSON.stringify(data, null, 2);
};
