package trading.main.Service;


import trading.main.Entity.BestPrice;
import trading.main.Entity.Trade;
import trading.main.Entity.Wallet;
import trading.main.Repository.BestPriceRepository;
import trading.main.Repository.TradeRepository;
import trading.main.Repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class TradeServiceImpl implements TradeService {

    private final WalletRepository walletRepo;
    private final TradeRepository tradeRepo;
    private final BestPriceRepository bestPriceRepo;

    public TradeServiceImpl(WalletRepository w, TradeRepository t, BestPriceRepository b) {
        this.walletRepo = w;
        this.tradeRepo = t;
        this.bestPriceRepo = b;
    }

    @Override
    @Transactional
    public Trade executeTrade(String userId, String pair, String side, BigDecimal amountCrypto) {

        BestPrice latest = bestPriceRepo.findTopByPairOrderByTimestampDesc(pair)
                .orElseThrow(() -> new IllegalStateException("No price available for " + pair));

        BigDecimal priceUsed = null;
        if("BUY".equalsIgnoreCase(side)){
            priceUsed = latest.getBestAsk();
        } else if("SELL".equalsIgnoreCase(side)){
            priceUsed = latest.getBestBid();
        } 

        BigDecimal totalUsdt = priceUsed.multiply(amountCrypto);

        Wallet usdtWallet = walletRepo.findByUserIdAndCurrency(userId, "USDT")
                .orElseThrow(() -> new IllegalStateException("USDT wallet not found"));

        String crypto = pair.substring(0,3);
        Wallet cryptoWallet;
        if("SELL".equalsIgnoreCase(side)){
            cryptoWallet = walletRepo.findByUserIdAndCurrency(userId, crypto)
                    .orElseThrow(() -> new IllegalArgumentException(crypto + " wallet not found"));
        } else { // BUY
            cryptoWallet = walletRepo.findByUserIdAndCurrency(userId, crypto)
                    .orElseGet(() -> {
                        Wallet w = new Wallet();
                        w.setUserId(userId);
                        w.setCurrency(crypto);
                        w.setBalance(BigDecimal.ZERO);
                        return walletRepo.save(w);
                    });
        }

        if("BUY".equalsIgnoreCase(side)){
            if(usdtWallet.getBalance().compareTo(totalUsdt) < 0)
                throw new IllegalArgumentException("Insufficient USDT balance");
            usdtWallet.setBalance(usdtWallet.getBalance().subtract(totalUsdt));
            cryptoWallet.setBalance(cryptoWallet.getBalance().add(amountCrypto));
        } else if("SELL".equalsIgnoreCase(side)) {
            if(cryptoWallet.getBalance().compareTo(amountCrypto) < 0)
                throw new IllegalArgumentException("Not enough crypto to sell");
            cryptoWallet.setBalance(cryptoWallet.getBalance().subtract(amountCrypto));
            usdtWallet.setBalance(usdtWallet.getBalance().add(totalUsdt));
        }

        Trade trade = new Trade();
        trade.setUserId(userId);
        trade.setPair(pair);
        trade.setSide(side.toUpperCase());
        trade.setAmountCrypto(amountCrypto);
        trade.setPriceUsed(priceUsed);
        trade.setTotalUsdt(totalUsdt);
        trade.setTimestamp(LocalDateTime.now());

        return tradeRepo.save(trade);
    }

}
