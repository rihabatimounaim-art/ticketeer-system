const API_BASE = "http://localhost:8080";
let token = null;

const setOutput = (id, data) => {
  document.getElementById(id).innerText =
    typeof data === "string" ? data : JSON.stringify(data, null, 2);
};

const requireToken = () => {
  if (!token) {
    throw new Error("You must login first.");
  }
};

const updateConnectionState = () => {
  const badge = document.getElementById("connectionState");
  badge.innerText = token ? "Connected" : "Not connected";
  badge.style.background = token ? "green" : "red";
  badge.style.color = "white";
  badge.style.padding = "5px";
};

updateConnectionState();

document.getElementById("loginBtn").onclick = async () => {
  try {
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    const res = await fetch(`${API_BASE}/auth/login`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email, password })
    });

    const data = await res.json();

    if (!res.ok) {
      throw new Error(data.message || "Login failed");
    }

    token = data.token;
    setOutput("loginResult", data);
    updateConnectionState();

  } catch (e) {
    setOutput("loginResult", "❌ " + e.message);
  }
};

document.getElementById("createTicketBtn").onclick = async () => {
  try {
    requireToken();

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

    if (!res.ok) {
      throw new Error(data.message || "Ticket creation failed");
    }

    setOutput("ticketResult", data);

    if (data.ticketId) {
      document.getElementById("ticketId").value = data.ticketId;
    }

  } catch (e) {
    setOutput("ticketResult", "❌ " + e.message);
  }
};

document.getElementById("validateTicketBtn").onclick = async () => {
  try {
    requireToken();

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

    if (!res.ok) {
      throw new Error(data.message || "Validation failed");
    }

    setOutput("controlResult", data);

  } catch (e) {
    setOutput("controlResult", "❌ " + e.message);
  }
};
