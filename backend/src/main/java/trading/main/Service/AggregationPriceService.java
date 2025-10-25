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
import java.util.List;

@Service
public class AggregationPriceService {

    private final BestPriceRepository bestPriceRepo;
    private final RestTemplate rest = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    private BestPrice latestBtc;
    private BestPrice latestEth;

    public AggregationPriceService(BestPriceRepository bestPriceRepo) {
        this.bestPriceRepo = bestPriceRepo;
    }

    @Scheduled(fixedRate = 10000)
    public void fetchPrices() {
        try {
            fetchAndAggregatePrices();
        } catch(Exception e){
            System.out.println("Error fetching prices: " + e.getMessage());
        }
    }

    private void fetchAndAggregatePrices() throws Exception {

        BigDecimal binanceBtcBid = null;
        BigDecimal binanceBtcAsk = null;
        BigDecimal binanceEthBid = null;
        BigDecimal binanceEthAsk = null;

        BigDecimal huobiBtcBid = null;
        BigDecimal huobiBtcAsk = null;
        BigDecimal huobiEthBid = null;
        BigDecimal huobiEthAsk = null;

        //Fetch Binance
        String binanceUrl = "https://api.binance.com/api/v3/ticker/bookTicker";
        JsonNode binanceArr = mapper.readTree(rest.getForObject(binanceUrl, String.class));
        for (JsonNode node : binanceArr) {
            String symbol = node.get("symbol").asText();
            if(symbol.equals("BTCUSDT")){
                binanceBtcBid = new BigDecimal(node.get("bidPrice").asText());
                binanceBtcAsk = new BigDecimal(node.get("askPrice").asText());
            }
            if(symbol.equals("ETHUSDT")){
                binanceEthBid = new BigDecimal(node.get("bidPrice").asText());
                binanceEthAsk = new BigDecimal(node.get("askPrice").asText());
            }
        }

        //Fetch Huobi
        String huobiUrl = "https://api.huobi.pro/market/tickers";
        JsonNode huobiArr = mapper.readTree(rest.getForObject(huobiUrl, String.class)).get("data");
        for (JsonNode node : huobiArr) {
            String symbol = node.get("symbol").asText().toUpperCase();
            if(symbol.equals("BTCUSDT")){
                huobiBtcBid = new BigDecimal(node.get("bid").asText());
                huobiBtcAsk = new BigDecimal(node.get("ask").asText());
            }
            if(symbol.equals("ETHUSDT")){
                huobiEthBid = new BigDecimal(node.get("bid").asText());
                huobiEthAsk = new BigDecimal(node.get("ask").asText());
            }
        }

        // Aggregate BTC
        BigDecimal btcBestBid = binanceBtcBid.max(huobiBtcBid);
        BigDecimal btcBestAsk = binanceBtcAsk.min(huobiBtcAsk);
        String btcSourceBid = btcBestBid.equals(binanceBtcBid) ? "BINANCE" : "HUOBI";
        String btcSourceAsk = btcBestAsk.equals(binanceBtcAsk) ? "BINANCE" : "HUOBI";

        BestPrice btcPrice = new BestPrice();
        btcPrice.setPair("BTCUSDT");
        btcPrice.setBestBid(btcBestBid);
        btcPrice.setBestAsk(btcBestAsk);
        btcPrice.setSourceBid(btcSourceBid);
        btcPrice.setSourceAsk(btcSourceAsk);
        btcPrice.setTimestamp(LocalDateTime.now());
        bestPriceRepo.save(btcPrice);
        latestBtc = btcPrice;

        // Aggregate ETH
        BigDecimal ethBestBid = binanceEthBid.max(huobiEthBid);
        BigDecimal ethBestAsk = binanceEthAsk.min(huobiEthAsk);
        String ethSourceBid = ethBestBid.equals(binanceEthBid) ? "BINANCE" : "HUOBI";
        String ethSourceAsk = ethBestAsk.equals(binanceEthAsk) ? "BINANCE" : "HUOBI";

        BestPrice ethPrice = new BestPrice();
        ethPrice.setPair("ETHUSDT");
        ethPrice.setBestBid(ethBestBid);
        ethPrice.setBestAsk(ethBestAsk);
        ethPrice.setSourceBid(ethSourceBid);
        ethPrice.setSourceAsk(ethSourceAsk);
        ethPrice.setTimestamp(LocalDateTime.now());
        bestPriceRepo.save(ethPrice);
        latestEth = ethPrice;
    }

    public List<BestPrice> getLatestPrices() {
        BestPrice latestBtc = bestPriceRepo.findTopByPairOrderByTimestampDesc("BTCUSDT")
                .orElse(new BestPrice());
        BestPrice latestEth = bestPriceRepo.findTopByPairOrderByTimestampDesc("ETHUSDT")
                .orElse(new BestPrice());
        return List.of(latestBtc, latestEth);
    }

}
