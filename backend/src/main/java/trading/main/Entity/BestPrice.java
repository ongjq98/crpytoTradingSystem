package trading.main.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "best_price")
public class BestPrice {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String pair;
    private BigDecimal bestBid;
    private BigDecimal bestAsk;
    private String sourceBid;
    private String sourceAsk;
    private LocalDateTime timestamp;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPair() {
        return pair;
    }

    public void setPair(String pair) {
        this.pair = pair;
    }

    public BigDecimal getBestBid() {
        return bestBid;
    }

    public void setBestBid(BigDecimal bestBid) {
        this.bestBid = bestBid;
    }

    public BigDecimal getBestAsk() {
        return bestAsk;
    }

    public void setBestAsk(BigDecimal bestAsk) {
        this.bestAsk = bestAsk;
    }

    public String getSourceBid() {
        return sourceBid;
    }

    public void setSourceBid(String sourceBid) {
        this.sourceBid = sourceBid;
    }

    public String getSourceAsk() {
        return sourceAsk;
    }

    public void setSourceAsk(String sourceAsk) {
        this.sourceAsk = sourceAsk;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}

