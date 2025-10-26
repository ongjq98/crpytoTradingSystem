package trading.main.Service;

import trading.main.Entity.BestPrice;
import trading.main.Repository.BestPriceRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AggregationPriceService {

    private final BestPriceRepository bestPriceRepo;
    private final RestTemplate rest = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    public AggregationPriceService(BestPriceRepository bestPriceRepo) {
        this.bestPriceRepo = bestPriceRepo;
    }

    // Scheduler runs every 10 seconds
    @Scheduled(fixedRate = 10000)
    public void fetchPrices() {
        try {
            fetchAndAggregatePrices();
        } catch (Exception e) {
            System.out.println("Error fetching prices: " + e.getMessage());
        }
    }

    private void fetchAndAggregatePrices() throws Exception {
        // Track best prices per symbol
        BestPrice latestBtc = new BestPrice();
        latestBtc.setPair("BTCUSDT");
        BestPrice latestEth = new BestPrice();
        latestEth.setPair("ETHUSDT");

        // --- Fetch Binance ---
        String binanceUrl = "https://api.binance.com/api/v3/ticker/bookTicker";
        JsonNode binanceArr = mapper.readTree(rest.getForObject(binanceUrl, String.class));

        BigDecimal btcBinanceBid = BigDecimal.ZERO;
        BigDecimal btcBinanceAsk = null;
        BigDecimal ethBinanceBid = BigDecimal.ZERO;
        BigDecimal ethBinanceAsk = null;

        for (JsonNode node : binanceArr) {
            String symbol = node.get("symbol").asText();
            BigDecimal bid = new BigDecimal(node.get("bidPrice").asText());
            BigDecimal ask = new BigDecimal(node.get("askPrice").asText());

            if (symbol.equals("BTCUSDT")) {
                if (bid.compareTo(btcBinanceBid) > 0) btcBinanceBid = bid;
                if (btcBinanceAsk == null || ask.compareTo(btcBinanceAsk) < 0) btcBinanceAsk = ask;
            } else if (symbol.equals("ETHUSDT")) {
                if (bid.compareTo(ethBinanceBid) > 0) ethBinanceBid = bid;
                if (ethBinanceAsk == null || ask.compareTo(ethBinanceAsk) < 0) ethBinanceAsk = ask;
            }
        }

        // --- Fetch Huobi ---
        String huobiUrl = "https://api.huobi.pro/market/tickers";
        JsonNode huobiArr = mapper.readTree(rest.getForObject(huobiUrl, String.class)).get("data");

        BigDecimal btcHuobiBid = BigDecimal.ZERO;
        BigDecimal btcHuobiAsk = null;
        BigDecimal ethHuobiBid = BigDecimal.ZERO;
        BigDecimal ethHuobiAsk = null;

        for (JsonNode node : huobiArr) {
            String symbol = node.get("symbol").asText().toUpperCase();
            BigDecimal bid = new BigDecimal(node.get("bid").asText());
            BigDecimal ask = new BigDecimal(node.get("ask").asText());

            if (symbol.equals("BTCUSDT")) {
                if (bid.compareTo(btcHuobiBid) > 0) btcHuobiBid = bid;
                if (btcHuobiAsk == null || ask.compareTo(btcHuobiAsk) < 0) btcHuobiAsk = ask;
            } else if (symbol.equals("ETHUSDT")) {
                if (bid.compareTo(ethHuobiBid) > 0) ethHuobiBid = bid;
                if (ethHuobiAsk == null || ask.compareTo(ethHuobiAsk) < 0) ethHuobiAsk = ask;
            }
        }

        // --- Aggregate BTC ---
        BigDecimal btcBestBid = btcBinanceBid.max(btcHuobiBid);
        BigDecimal btcBestAsk = btcBinanceAsk.min(btcHuobiAsk);
        latestBtc.setBestBid(btcBestBid);
        latestBtc.setBestAsk(btcBestAsk);
        latestBtc.setSourceBid(btcBestBid.equals(btcBinanceBid) ? "BINANCE" : "HUOBI");
        latestBtc.setSourceAsk(btcBestAsk.equals(btcBinanceAsk) ? "BINANCE" : "HUOBI");
        latestBtc.setTimestamp(LocalDateTime.now());
        bestPriceRepo.save(latestBtc);

        // --- Aggregate ETH ---
        BigDecimal ethBestBid = ethBinanceBid.max(ethHuobiBid);
        BigDecimal ethBestAsk = ethBinanceAsk.min(ethHuobiAsk);
        latestEth.setBestBid(ethBestBid);
        latestEth.setBestAsk(ethBestAsk);
        latestEth.setSourceBid(ethBestBid.equals(ethBinanceBid) ? "BINANCE" : "HUOBI");
        latestEth.setSourceAsk(ethBestAsk.equals(ethBinanceAsk) ? "BINANCE" : "HUOBI");
        latestEth.setTimestamp(LocalDateTime.now());
        bestPriceRepo.save(latestEth);
    }

    // Return the latest best prices for BTC and ETH
    public List<BestPrice> getLatestPrices() {
        // Fetch the latest saved prices from DB
        BestPrice latestBtc = bestPriceRepo.findTopByPairOrderByTimestampDesc("BTCUSDT")
                .orElse(new BestPrice());
        BestPrice latestEth = bestPriceRepo.findTopByPairOrderByTimestampDesc("ETHUSDT")
                .orElse(new BestPrice());
        return List.of(latestBtc, latestEth);

    }
}
