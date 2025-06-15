package com.wexinc.purchasetransaction.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PurchaseTransactionResponse {

    /**
     * The transaction ID
     */
    @Schema(example = "da4f6dd8-5a41-4225-9604-56863c48e11e")
    private String id;
    /**
     * The transaction purchase date
     */
    @Schema(example = "2025-06-03")
    private String transactionDate;
    /**
     * The original US dollar purchase amount
     */
    @Schema(example = "100.65")
    private String purchaseAmount;
    /**
     * The transaction description
     */
    @Schema(example = "The description registered for transaction")
    private String description;
    /**
     * The exchange rate used based in the given transaction date
     */
    private Float exchangeRate;
    /**
     * the converted amount based upon the specified currency’s exchange rate for the date of the purchase
     */
    @Schema(example = "144.44")
    private String convertedAmount;
}
