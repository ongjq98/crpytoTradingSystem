package Service;

import Entity.Trade;

import java.math.BigDecimal;

public interface TradeService {

    Trade executeTrade(Long userId, String pair, String side, BigDecimal amountCrypto);

}
