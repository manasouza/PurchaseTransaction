package com.wexinc.purchasetransaction.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PurchaseTransactionResponse {

    /**
     * The transaction ID
     */
    private String id;
    /**
     * The transaction purchase date
     */
    private String transactionDate;
    /**
     * The original US dollar purchase amount
     */
    private String purchaseAmount;
    /**
     * The transaction description
     */
    private String description;
    /**
     * The exchange rate used based in the given transaction date
     */
    private Float exchangeRate;
    /**
     * the converted amount based upon the specified currency’s exchange rate for the date of the purchase
     */
    private String convertedAmount;
}
