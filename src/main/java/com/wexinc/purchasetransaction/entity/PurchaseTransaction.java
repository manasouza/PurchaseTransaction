package com.wexinc.purchasetransaction.entity;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@RequiredArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class PurchaseTransaction {

    private UUID id;
    private Float exchangeRate;
    private BigDecimal convertedAmount;
    @NonNull
    private LocalDate transactionDate;
    @NonNull
    private BigDecimal purchaseAmount;
    @NonNull
    private String description;

}
