package trading.main.Repository;

import trading.main.Entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    List<Wallet> findByUserIdOrderByCurrencyAsc(String userId);
    Optional<Wallet> findByUserIdAndCurrency(String userId, String currency);
}
