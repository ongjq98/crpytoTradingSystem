package trading.main;


import Entity.Wallet;
import Repository.WalletRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    CommandLineRunner initWallet(WalletRepository walletRepo) {
        return args -> {
            if (walletRepo.findByUserIdAndCurrency(1L, "USDT").isEmpty()) {
                Wallet usdtWallet = new Wallet();
                usdtWallet.setUserId(1L);
                usdtWallet.setCurrency("USDT");
                usdtWallet.setBalance(new BigDecimal("50000"));
                walletRepo.save(usdtWallet);
            }
        };
    }
}
