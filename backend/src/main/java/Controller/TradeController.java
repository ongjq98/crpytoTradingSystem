package Controller;


import Entity.Trade;
import Entity.TradeRequest;
import Entity.Wallet;
import Repository.TradeRepository;
import Repository.WalletRepository;
import Service.TradeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TradeController {
    private final TradeService tradeService;
    private final WalletRepository walletRepo;
    private final TradeRepository tradeRepo;

    public TradeController(TradeService tradeService,
                             WalletRepository walletRepo,
                             TradeRepository tradeRepo) {
        this.tradeService = tradeService;
        this.walletRepo = walletRepo;
        this.tradeRepo = tradeRepo;
    }

    @PostMapping("/trade")
    public Trade trade(@RequestHeader(value="X-USER-ID", required=false) Long userId,
                       @RequestBody TradeRequest req) {
        Long uid = (userId != null) ? userId : 1L;
        return tradeService.executeTrade(uid, req.getPair(), req.getSide(), req.getAmountCrypto());
    }

    @GetMapping("/wallet")
    public List<Wallet> wallet(@RequestHeader(value="X-USER-ID", required=false) Long userId){
        Long uid = (userId != null) ? userId : 1L;
        return walletRepo.findByUserIdOrderByCurrencyAsc(uid);
    }

    @GetMapping("/tradeHistory")
    public List<Trade> trades(@RequestHeader(value="X-USER-ID", required=false) Long userId){
        Long uid = (userId != null) ? userId : 1L;
        return tradeRepo.findByUserIdOrderByTimestampDesc(uid);
    }

}
