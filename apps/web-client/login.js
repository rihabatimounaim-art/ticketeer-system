const API_BASE = "http://localhost:8080";

const setStatus = (id, text, type = "info") => {
  const el = document.getElementById(id);
  el.innerText = text;
  el.className = `status-line ${type}`;
};

document.getElementById("loginBtn").onclick = async () => {
  setStatus("loginStatus", "Logging in...", "info");

  try {
    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value.trim();

    const res = await fetch(`${API_BASE}/auth/login`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email, password })
    });

    const data = await res.json();

    if (!res.ok) {
      throw new Error(data.message || "Login failed");
    }

    localStorage.setItem("ticketeer_token", data.token);
    window.location.href = "dashboard.html";
  } catch (error) {
    setStatus("loginStatus", error.message, "error");
  }
};
