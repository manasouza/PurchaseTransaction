package com.wexinc.purchasetransaction.service;

import com.wexinc.purchasetransaction.dto.PurchaseTransactionMapper;
import com.wexinc.purchasetransaction.dto.PurchaseTransactionRequest;
import com.wexinc.purchasetransaction.entity.ExchangeRate;
import com.wexinc.purchasetransaction.entity.PurchaseTransaction;
import com.wexinc.purchasetransaction.repository.PurchaseTransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PurchaseTransactionServiceTest {

    @Mock
    private PurchaseTransactionRepository ptRepository;
    @Mock
    private ExchangeRateService erService;
    @Spy
    private PurchaseTransactionMapper mapper = Mappers.getMapper(PurchaseTransactionMapper.class);
    @InjectMocks
    private PurchaseTransactionService service;

    @Test
    void expectToStorePurchaseTransaction() {
        // GIVEN
        String date = "2025-04-01";
        String value = "1.0";
        String description = "first transaction";
        PurchaseTransactionRequest transaction = new PurchaseTransactionRequest(date, value, description);
        // GIVEN mocks
        LocalDate transactionDate = LocalDate.parse(date);
        BigDecimal purchaseAmount = new BigDecimal(value);
        PurchaseTransaction createdTransaction = new PurchaseTransaction(transactionDate, purchaseAmount, description);
        createdTransaction.setId(UUID.randomUUID());
        when(ptRepository.create(new PurchaseTransaction(transactionDate, purchaseAmount, description)))
                .thenReturn(createdTransaction);
        // WHEN
        UUID storedTransactionId = service.storePurchaseTransaction(transaction);
        // THEN
        assertNotNull(storedTransactionId, "Stored Purchase Transaction Id should not be null after store.");
    }

    @Test
    void expectToRetrievePurchaseTransactionByCountryCurrency() {
        // GIVEN
        LocalDate purchaseDate = LocalDate.now();
        LocalDate effectiveDate = LocalDate.now().minusDays(1);
        String country = "Brazil";
        String currency = "Real";
        BigDecimal purchaseAmount = BigDecimal.TEN;
        String description = "Purchase #1";
        int pageNumber = 1;
        int pageSize = 10;
        // GIVEN mocks
        Optional<ExchangeRate> exchangeRate = Optional.of(
                new ExchangeRate(LocalDate.of(2025, 1, 1), country, currency, 5.7f, effectiveDate));
        PurchaseTransaction storedTransaction = new PurchaseTransaction(purchaseDate, purchaseAmount, description);
        storedTransaction.setId(UUID.randomUUID());
        when(erService.getExchangeRate(country, currency, purchaseDate, pageNumber, pageSize))
                .thenReturn(exchangeRate);
        when(ptRepository.findByDate(purchaseDate)).thenReturn(List.of(storedTransaction));
        // WHEN
        Collection<PurchaseTransaction> purchaseTransactions = service.getPurchaseTransactionByCountryCurrency(country,
                currency, purchaseDate, pageNumber, pageSize);
        // THEN
        assertNotNull(purchaseTransactions, "Purchase Transaction should not be null");
        assertFalse(purchaseTransactions.isEmpty());
        PurchaseTransaction purchaseTransaction = purchaseTransactions.iterator().next();
        assertEquals(purchaseDate, purchaseTransaction.getTransactionDate());
        assertEquals(purchaseAmount, purchaseTransaction.getPurchaseAmount());
        assertEquals(description, purchaseTransaction.getDescription());
        assertNotNull(purchaseTransaction.getId(), "Purchase Transaction ID should not be null");
        assertNotNull(purchaseTransaction.getConvertedAmount(), "Purchase Transaction converted amount should not be null");
    }

    @Test
    void expectEmptyWhenCountryOrCurrencyNotFound() {
        // GIVEN
        LocalDate purchaseDate = LocalDate.now();
        BigDecimal purchaseAmount = BigDecimal.TEN;
        String country = "InvalidOrInexistent";
        String currency = "InvalidOrInexistent";
        int pageNumber = 1;
        int pageSize = 10;
        // GIVEN mocks
        PurchaseTransaction storedTransaction = new PurchaseTransaction(purchaseDate, purchaseAmount, "");
        storedTransaction.setId(UUID.randomUUID());
        when(erService.getExchangeRate(country, currency, purchaseDate, pageNumber, pageSize)).thenReturn(Optional.empty());
        when(ptRepository.findByDate(purchaseDate)).thenReturn(List.of(storedTransaction));
        // WHEN
        Collection<PurchaseTransaction> purchaseTransactions = service.getPurchaseTransactionByCountryCurrency(country, currency, purchaseDate, pageNumber, pageSize);
        // THEN
        assertTrue(purchaseTransactions.isEmpty());
    }

    @Test
    void expectEmptyWhenExchangeRateNotFound() {
        // GIVEN
        LocalDate purchaseDate = LocalDate.now();
        // GIVEN mocks
        when(ptRepository.findByDate(purchaseDate)).thenReturn(Collections.emptyList());
        // WHEN
        Collection<PurchaseTransaction> purchaseTransactions = service.getPurchaseTransactionByCountryCurrency("country",
                "currency", purchaseDate, 1, 10);
        // THEN
        assertTrue(purchaseTransactions.isEmpty());
        verify(erService, times(0)).getExchangeRate(anyString(), anyString(), any(LocalDate.class),
                anyInt(), anyInt());
    }

    @Test
    void expectValidationExceptionWhenPurchaseDateDiffersAboveSixMonths() {
        // GIVEN
        LocalDate purchaseDate = LocalDate.now();
        LocalDate effectiveDate = LocalDate.now().minusMonths(6).minusDays(1);
        String country = "Brazil";
        String currency = "Real";
        int pageNumber = 2;
        int pageSize = 10;
        // GIVEN mocks
        Optional<ExchangeRate> exchangeRate = Optional.of(
                new ExchangeRate(LocalDate.of(2025, 1, 1), country, currency, 5.7f, effectiveDate));
        PurchaseTransaction storedTransaction = new PurchaseTransaction(purchaseDate, BigDecimal.TEN, "");
        storedTransaction.setId(UUID.randomUUID());
        when(erService.getExchangeRate(country, currency, purchaseDate, pageNumber, pageSize))
                .thenReturn(exchangeRate);
        when(ptRepository.findByDate(purchaseDate)).thenReturn(List.of(storedTransaction));
        // WHEN / THEN
        assertThrows(TransactionValidationException.class, () -> service.getPurchaseTransactionByCountryCurrency(country, currency, purchaseDate, pageNumber, pageSize));
    }

}
