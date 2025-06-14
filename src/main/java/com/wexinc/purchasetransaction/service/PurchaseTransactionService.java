package com.wexinc.purchasetransaction.service;

import com.wexinc.purchasetransaction.dto.PurchaseTransactionMapper;
import com.wexinc.purchasetransaction.dto.PurchaseTransactionRequest;
import com.wexinc.purchasetransaction.entity.ExchangeRate;
import com.wexinc.purchasetransaction.entity.PurchaseTransaction;
import com.wexinc.purchasetransaction.repository.PurchaseTransactionRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class PurchaseTransactionService {

    @NonNull
    private final PurchaseTransactionRepository purchaseTransactionRepository;
    @NonNull
    private final ExchangeRateService exchangeRateService;
    @NonNull
    private final PurchaseTransactionMapper mapper;

    public UUID storePurchaseTransaction(PurchaseTransactionRequest transactionDTO) {
        log.info("Creating Purchase Transaction: {}", transactionDTO);
        return purchaseTransactionRepository.create(mapper.toEntity(transactionDTO)).getId();
    }

    public Collection<PurchaseTransaction> getPurchaseTransactionByCountryCurrency(String country, String currency,
                                                                                   LocalDate purchaseDate, int pageNumber, int pageSize) {
        List<PurchaseTransaction> transactionsByDate = purchaseTransactionRepository.findByDate(purchaseDate);
        if (transactionsByDate.isEmpty()) {
            log.info("No transaction record found at {}", purchaseDate);
        } else {
            log.info("Transactions found: {}", transactionsByDate);
            Optional<ExchangeRate> exchangeRate = exchangeRateService.getExchangeRate(country, currency, purchaseDate,
                    pageNumber, pageSize);
            if (exchangeRate.isPresent()) {
                log.info("Exchange rate found: {}", exchangeRate.get());
                if (purchaseDate.minusMonths(6).isAfter(exchangeRate.get().getEffectiveDate())) {
                    throw new TransactionValidationException("the purchase cannot be converted to the target currency since exchange rate date differs more than 6 months from purchase date");
                }
                return transactionsByDate.stream()
                        .peek(pt -> {
                            pt.setExchangeRate(exchangeRate.get().getExchangeRate());
                            pt.setConvertedAmount(pt.getPurchaseAmount()
                                    .multiply(BigDecimal.valueOf(pt.getExchangeRate()))
                                    .setScale(2, RoundingMode.UP));
                        }).toList();
            } else {
                log.info("No exchange rate found for the given country and currency");
            }
        }
        return Collections.EMPTY_LIST;
    }
}
