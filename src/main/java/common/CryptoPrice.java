package common;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@AllArgsConstructor
public class CryptoPrice implements Serializable {

    public CryptoPrice() {
    }

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "Crypto")
    private Crypto crypto;

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "Exchange")
    private Exchange exchange;

    @Id
    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    @Column(name = "PriceInGbp")
    private BigDecimal priceInGbp;

    @Column(name = "PriceInEur")
    private BigDecimal priceInEur;

    @Column(name = "PriceInUsd")
    private BigDecimal priceInUsd;
}
