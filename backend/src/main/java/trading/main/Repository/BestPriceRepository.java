package trading.main.Repository;

import trading.main.Entity.BestPrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BestPriceRepository extends JpaRepository<BestPrice, Long> {

    Optional<BestPrice> findTopByPairOrderByTimestampDesc(String pair);
}
