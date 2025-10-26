const API_BASE = "http://localhost:8080/api"; // your Spring Boot backend URL

// Fetch wallet for user
export async function getWallet(userId = "0001") {
  const res = await fetch(`${API_BASE}/wallet`, {
    headers: { "X-USER-ID": userId },
  });
  if (!res.ok) return [];
  return res.json();
}

// Fetch latest aggregated prices (BTC, ETH)
export async function getLatestPrices() {
  const res = await fetch(`${API_BASE}/prices`);
  if (!res.ok) return [];
  return res.json();
}

// Fetch trade history for user
export async function getTrades(userId = "0001") {
  const res = await fetch(`${API_BASE}/tradeHistory`, {
    headers: { "X-USER-ID": userId },
  });
  if (!res.ok) return [];
  return res.json();
}

// Execute trade
export async function trade(pair, side, amountCrypto, userId = "0001") {
  const res = await fetch(`${API_BASE}/trade`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      "X-USER-ID": userId,
    },
    body: JSON.stringify({
      pair,
      side,
      amountCrypto,
    }),
  });

  if (!res.ok) {
    const msg = await res.text();
    throw new Error("Trade failed: " + msg);
  }

  return res.json();
}
