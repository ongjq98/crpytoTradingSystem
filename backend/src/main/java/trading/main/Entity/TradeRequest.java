package trading.main.Entity;

import java.math.BigDecimal;

public class TradeRequest {

    private String pair;
    private String side;
    private BigDecimal amountCrypto;

    // getters and setters
    public String getPair() { return pair; }
    public void setPair(String pair) { this.pair = pair; }
    public String getSide() { return side; }
    public void setSide(String side) { this.side = side; }
    public BigDecimal getAmountCrypto() { return amountCrypto; }
    public void setAmountCrypto(BigDecimal amountCrypto) { this.amountCrypto = amountCrypto; }
}
