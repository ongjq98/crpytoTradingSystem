package trading.main.Service;

import trading.main.Entity.Trade;

import java.math.BigDecimal;

public interface TradeService {

    Trade executeTrade(String userId, String pair, String side, BigDecimal amountCrypto);

}
