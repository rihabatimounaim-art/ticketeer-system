const API_BASE = "http://localhost:8080";
let token = null;

const setOutput = (id, data) => {
  document.getElementById(id).innerText =
    typeof data === "string" ? data : JSON.stringify(data, null, 2);
};

const setStatus = (id, text, type = "info") => {
  const el = document.getElementById(id);
  el.innerText = text;
  el.className = `status-line ${type}`;
};

const requireToken = () => {
  if (!token) {
    throw new Error("You must login first.");
  }
};

const updateConnectionState = () => {
  const badge = document.getElementById("connectionState");
  badge.innerText = token ? "Connected" : "Not connected";
  badge.className = token ? "badge connected" : "badge disconnected";
};

const formatValidationMessage = (result) => {
  switch (result) {
    case "VALID":
      return "Ticket validation succeeded.";
    case "EXPIRED":
      return "Ticket exists, but it is expired.";
    case "ALREADY_USED":
      return "Ticket has already been used.";
    case "INVALID":
      return "Ticket is invalid.";
    default:
      return `Validation result: ${result}`;
  }
};

updateConnectionState();

document.getElementById("loginBtn").onclick = async () => {
  setStatus("loginStatus", "Logging in...");
  setOutput("loginResult", "");

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
    setStatus("loginStatus", "Login successful.", "success");
    setOutput("loginResult", data);
    updateConnectionState();
  } catch (error) {
    setStatus("loginStatus", "Login successful.", "success");
    setOutput("loginResult", `Error: ${error.message}`);
  }
};

document.getElementById("createTicketBtn").onclick = async () => {
  setStatus("ticketStatus", "Creating ticket...");
  setOutput("ticketResult", "");

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

    if (data.ticketId) {
      document.getElementById("ticketId").value = data.ticketId;
    }

    setStatus(
  "ticketStatus",
  `Ticket created successfully (${data.status}).`,
  "success"
);
    setOutput("ticketResult", data);
  } catch (error) {
    setStatus("ticketStatus", "Ticket creation failed.", "error");
    setOutput("ticketResult", `Error: ${error.message}`);
  }
};

document.getElementById("validateTicketBtn").onclick = async () => {
  setStatus("controlStatus", "Validating ticket...");
  setOutput("controlResult", "");

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
      throw new Error(data.message || "Ticket validation failed");
    }

    let type = "info";

if (data.result === "VALID") type = "success";
if (data.result === "EXPIRED" || data.result === "INVALID") type = "error";

setStatus(
  "controlStatus",
  formatValidationMessage(data.result),
  type
);
    setOutput("controlResult", data);
  } catch (error) {
    setStatus("controlStatus", "Ticket validation failed.", "error");
    setOutput("controlResult", `Error: ${error.message}`);
  }
};
