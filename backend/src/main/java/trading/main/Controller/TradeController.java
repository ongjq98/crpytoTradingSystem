package trading.main.Controller;


import trading.main.Entity.BestPrice;
import trading.main.Entity.Trade;
import trading.main.Entity.TradeRequest;
import trading.main.Entity.Wallet;
import trading.main.Repository.BestPriceRepository;
import trading.main.Repository.TradeRepository;
import trading.main.Repository.WalletRepository;
import trading.main.Service.AggregationPriceService;
import trading.main.Service.TradeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api")
public class TradeController {
    private final TradeService tradeService;
    private final WalletRepository walletRepo;
    private final TradeRepository tradeRepo;

    private final AggregationPriceService priceService;

    public TradeController(TradeService tradeService,
                             WalletRepository walletRepo,
                             TradeRepository tradeRepo,
                           AggregationPriceService priceService) {
        this.tradeService = tradeService;
        this.walletRepo = walletRepo;
        this.tradeRepo = tradeRepo;
        this.priceService = priceService;
    }

    @PostMapping("/trade")
    public Trade trade(@RequestHeader(value="X-USER-ID", required=false) String userId,
                       @RequestBody TradeRequest req) {
        String uid = (userId != null) ? userId : "0001";
        return tradeService.executeTrade(uid, req.getPair(), req.getSide(), req.getAmountCrypto());
    }

    @GetMapping("/wallet")
    public List<Wallet> wallet(@RequestHeader(value="X-USER-ID", required=false) String userId){
        String uid = (userId != null) ? userId : "0001";
        return walletRepo.findByUserIdOrderByCurrencyAsc(uid);
    }

    @GetMapping("/tradeHistory")
    public List<Trade> trades(@RequestHeader(value="X-USER-ID", required=false) String userId){
        String uid = (userId != null) ? userId : "0001";
        return tradeRepo.findByUserIdOrderByTimestampDesc(uid);
    }

    @GetMapping("/prices")
    public List<BestPrice> prices() {
        return priceService.getLatestPrices(); // fetch all best prices
    }

}
