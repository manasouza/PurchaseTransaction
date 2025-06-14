package com.wexinc.purchasetransaction.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wexinc.purchasetransaction.dto.PurchaseTransactionMapperImpl;
import com.wexinc.purchasetransaction.dto.PurchaseTransactionRequest;
import com.wexinc.purchasetransaction.entity.PurchaseTransaction;
import com.wexinc.purchasetransaction.service.PurchaseTransactionService;
import com.wexinc.purchasetransaction.service.TransactionValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static com.wexinc.purchasetransaction.utils.Constants.DATE_FORMATTER;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = PurchaseTransactionController.class)
@Import(PurchaseTransactionMapperImpl.class)
public class PurchaseTransactionControllerTest {

    @Autowired
    private MockMvc mvc;
    @MockitoBean
    private PurchaseTransactionService service;

    private final ObjectMapper mapper = new ObjectMapper();

    public static Stream<Arguments> storePurchaseTransactionInvalidArgs() {
        return Stream.of(
                Arguments.of("", "", ""),
                Arguments.of("", "1000", "new transaction"),
                Arguments.of("2025-03-30", "", "new transaction"),
                Arguments.of("03/30/2025", "1000", "new transaction"),
                Arguments.of("2025-03-30", "-1000", "new transaction"),
                Arguments.of("2025-03-30", "100.9586", "new transaction"),
                Arguments.of("2025-03-30", "100.95", "new transaction to exceed the fifty characters expected to fail")
        );
    }

    public static Stream<Arguments> getPurchaseTransactionInvalidArgs() {
        return Stream.of(
                Arguments.of(null, null, null),
                Arguments.of("country", "", null),
                Arguments.of("", "currency", ""),
                Arguments.of(null, null, "2025-05-02"),
                Arguments.of("country", null, "2025-05-02"),
                Arguments.of("country", "currency", "05/02/2025")
        );
    }

    @Test
    void expectCreatedWhenPurchaseTransactionSuccessfullyStored() throws Exception {
        // GIVEN
        UUID transactionId = UUID.randomUUID();
        PurchaseTransactionRequest transactionDTO = new PurchaseTransactionRequest("2025-03-30", "1000", "new transaction");
        when(service.storePurchaseTransaction(transactionDTO)).thenReturn(transactionId);
        // WHEN
        mvc.perform(post("/v1/purchaseTransaction")
                .header("Content-Type", "application/json")
                .content(mapper.writeValueAsString(transactionDTO)))
                // THEN
                .andExpect(status().isCreated())
                .andExpect(content().string(transactionId.toString()));
    }

    @ParameterizedTest
    @MethodSource("storePurchaseTransactionInvalidArgs")
    void expectBadRequestWhenPurchaseTransactionHasInvalidParametersToStore(String date, String value, String description) throws Exception {
        // GIVEN
        PurchaseTransactionRequest transactionDTO = new PurchaseTransactionRequest(date, value, description);
        // WHEN
        mvc.perform(post("/v1/purchaseTransaction")
                .header("Content-Type", "application/json")
                .content(mapper.writeValueAsString(transactionDTO)))
                // THEN
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @MethodSource("getPurchaseTransactionInvalidArgs")
    void expectBadRequestWhenPurchaseTransactionHasInvalidParametersDuringRetrieve(String country, String currency, String date) throws Exception {
        // GIVEN wrong query arguments
        // WHEN
        mvc.perform(get("/v1/purchaseTransaction")
                        .queryParam("country", country)
                        .queryParam("currency", currency)
                        .queryParam("purchaseDate", date))
                // THEN
                .andExpect(status().isBadRequest());
    }

    @Test
    void expectUnprocessableEntityWhenTransactionValidationExceptionDuringRetrieve() throws Exception {
        // GIVEN
        String date = "2025-02-03";
        String currency = "Real";
        String country = "Brazil";
        LocalDate purchaseDate = LocalDate.parse(date, DATE_FORMATTER);
        when(service.getPurchaseTransactionByCountryCurrency(country, currency, purchaseDate)).thenThrow(TransactionValidationException.class);
        // WHEN
        mvc.perform(get("/v1/purchaseTransaction")
                        .queryParam("country", country)
                        .queryParam("currency", currency)
                        .queryParam("purchaseDate", date))
                // THEN
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void expectOkWhenPurchaseTransactionRetrieved() throws Exception {
        UUID transactionId = UUID.randomUUID();
        String country = "Brazil";
        String currency = "Real";
        String purchaseDate = "2025-05-02";
        String description = "Travel next month";
        float exchangeRate = 2.5f;
        BigDecimal purchaseAmount = BigDecimal.TEN;
        BigDecimal convertedAmount = purchaseAmount.multiply(BigDecimal.valueOf(exchangeRate));
        // GIVEN mocks
        LocalDate purchaseDateConverted = LocalDate.parse(purchaseDate, DATE_FORMATTER);
        PurchaseTransaction purchaseTransaction = new PurchaseTransaction(purchaseDateConverted, purchaseAmount, description);
        purchaseTransaction.setId(transactionId);
        purchaseTransaction.setConvertedAmount(convertedAmount);
        purchaseTransaction.setExchangeRate(exchangeRate);
        when(service.getPurchaseTransactionByCountryCurrency(country, currency, purchaseDateConverted))
                .thenReturn(List.of(purchaseTransaction));
        // WHEN
        mvc.perform(get("/v1/purchaseTransaction")
                        .queryParam("country", country)
                        .queryParam("currency", currency)
                        .queryParam("purchaseDate", purchaseDate))
                // THEN
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(transactionId.toString()))
                .andExpect(jsonPath("$[0].transactionDate").value(purchaseDate))
                .andExpect(jsonPath("$[0].purchaseAmount").value(purchaseAmount))
                .andExpect(jsonPath("$[0].convertedAmount").value(convertedAmount))
                .andExpect(jsonPath("$[0].exchangeRate").value(exchangeRate))
                .andExpect(jsonPath("$[0].description").value(description));
    }
}
