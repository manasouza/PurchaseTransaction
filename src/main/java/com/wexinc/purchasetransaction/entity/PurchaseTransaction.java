package com.wexinc.purchasetransaction.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@RequiredArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class PurchaseTransaction {

    @Id
    private UUID id;
    @Column
    private Float exchangeRate;
    @Column(nullable = false)
    private BigDecimal convertedAmount;
    @NonNull
    @Column(nullable = false)
    private LocalDate transactionDate;
    @NonNull
    @Column
    private BigDecimal purchaseAmount;
    @NonNull
    @Column
    private String description;

    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
    }


}
