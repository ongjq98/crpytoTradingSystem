import { useEffect, useState } from "react";
import { getLatestPrices, getWallet, trade, getTrades } from "./api";

function App() {
  const [prices, setPrices] = useState([]);
  const [wallet, setWallet] = useState([]);
  const [trades, setTrades] = useState([]);
  const [pair, setPair] = useState("BTCUSDT");
  const [side, setSide] = useState("BUY");
  const [amount, setAmount] = useState("");

  useEffect(() => {
    fetchData();
    const interval = setInterval(fetchData, 10000); // refresh every 10s
    return () => clearInterval(interval);
  }, []);

  async function fetchData() {
    setPrices(await getLatestPrices());
    setWallet(await getWallet());
    setTrades(await getTrades());
  }

  async function handleTrade() {
    if (!amount) return;
    try {
      await trade(pair, side, parseFloat(amount));
      setAmount("");
      fetchData();
    } catch (err) {
      console.error(err.message);
    }
  }

  return (
    <div style={{ padding: "20px" }}>
      <h2>Latest Prices</h2>
      <ul>
        {prices.map(p => (
          <li key={p.pair}>
            {p.pair} - BID: {p.bestBid} ({p.sourceBid}) / ASK: {p.bestAsk} ({p.sourceAsk})
          </li>
        ))}
      </ul>

      <h2>Wallet</h2>
      <ul>
        {wallet.map(w => (
          <li key={w.currency}>{w.currency}: {w.balance}</li>
        ))}
      </ul>

      <h2>Trade</h2>
      <select value={pair} onChange={e => setPair(e.target.value)}>
        <option value="BTCUSDT">BTCUSDT</option>
        <option value="ETHUSDT">ETHUSDT</option>
      </select>
      <select value={side} onChange={e => setSide(e.target.value)}>
        <option value="BUY">BUY</option>
        <option value="SELL">SELL</option>
      </select>
      <input
        type="number"
        value={amount}
        onChange={e => setAmount(e.target.value)}
        placeholder="Amount"
      />
      <button onClick={handleTrade}>Execute Trade</button>

      <h2>Trade History</h2>
      <ul>
        {trades.map(t => (
          <li key={t.id}>
            {t.timestamp}: {t.side} {t.amountCrypto} {t.pair} @ {t.priceUsed} USDT
          </li>
        ))}
      </ul>
    </div>
  );
}

export default App;
