package com.wexinc.purchasetransaction.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@RequiredArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class PurchaseTransactionRequest {

    @NonNull
    @NotNull(message = "Transaction date is required")
    @ValidDateFormat(message = "Transaction date must be in format yyyy-MM-dd")
    @Schema(example = "2025-06-03")
    private String transactionDate;

    @NonNull
    @NotNull(message = "Purchase amount is required")
    @DecimalMin(value = "0.01", message = "Purchase Amount must be positive")
    @Digits(integer = 10, fraction = 2, message = "Purchase Amount must be rounded to the nearest cent")
    @Schema(example = "1000.1")
    private String purchaseAmount;

    @NonNull
    @Size(max = 50, message = "Description must not exceed 50 characters")
    @Schema(example = "Anything that describes the transaction details")
    private String description;

}
