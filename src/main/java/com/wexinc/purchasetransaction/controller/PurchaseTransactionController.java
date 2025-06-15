package com.wexinc.purchasetransaction.controller;

import com.wexinc.purchasetransaction.dto.PurchaseTransactionMapper;
import com.wexinc.purchasetransaction.dto.PurchaseTransactionRequest;
import com.wexinc.purchasetransaction.dto.PurchaseTransactionResponse;
import com.wexinc.purchasetransaction.entity.PurchaseTransaction;
import com.wexinc.purchasetransaction.service.PurchaseTransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.UUID;

import static com.wexinc.purchasetransaction.utils.Constants.DATE_FORMATTER;

@Slf4j
@RestController
@RequestMapping("v1/purchaseTransaction")
@RequiredArgsConstructor
@Tag(name = "Purchase Transaction API", description = "API to store and retrieve Purchase Transactions")
public class PurchaseTransactionController {

    @NonNull
    private final PurchaseTransactionService service;
    @NonNull
    private final PurchaseTransactionMapper mapper;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Stores a new Purchase Transaction with date, value and description",
        responses = {
            @ApiResponse(responseCode = "201", description = "successfully created. It returns the transaction id",
                    content = @Content(schema = @Schema(type = "string", example = "da4f6dd8-5a41-4225-9604-56863c48e11e"))),
            @ApiResponse(responseCode = "400", description = "wrong request body",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(
                            value = """
                                    {
                                    	"timestamp": "2025-06-15T22:09:41.526+00:00",
                                    	"status": 400,
                                    	"error": "Bad Request",
                                    	"path": "/v1/purchaseTransaction"
                                    }
                                    """
                    ))),
            @ApiResponse(responseCode = "500", description = "any internal server error", content = @Content)
        })
    public ResponseEntity<String> storePurchaseTransaction(@Valid @RequestBody PurchaseTransactionRequest purchaseTransactionRequest) {
        UUID id = service.storePurchaseTransaction(purchaseTransactionRequest);
        return new ResponseEntity<>(id.toString(), HttpStatus.CREATED);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Retrieves a Purchase Transaction given the transaction date and the country currency information",
        responses = {
            @ApiResponse(responseCode = "200", description = "successfully returned"),
            @ApiResponse(responseCode = "400", description = "wrong query parameters", content = @Content),
            @ApiResponse(responseCode = "422", description = "when transaction period exceeds exchage rate from more than 6 months",
                content = @Content),
            @ApiResponse(responseCode = "500", description = "any internal server error", content = @Content)
        })
    public ResponseEntity<Collection<PurchaseTransactionResponse>> retrievePurchaseTransaction(
            @Parameter(description = "The country to refer to respective currency", example = "Brazil")
                @RequestParam String country,
            @Parameter(description = "The currency to convert the purchase transaction", example = "Real")
                @RequestParam String currency,
            @Parameter(description = "The transaction date of purchase in the format yyyy-MM-dd", example = "2025-06-01")
                @RequestParam String purchaseDate,
            @Parameter(description = "Page number that relates to the data from Exchange Rates API integration")
                @RequestParam(defaultValue = "1") int pageNumber,
            @Parameter(description = "Page size that relates to the data from Exchange Rates API integration")
                @RequestParam(defaultValue = "500") int pageSize) {
        try {
            LocalDate pDate = LocalDate.parse(purchaseDate, DATE_FORMATTER);
            Collection<PurchaseTransaction> transactions = service.getPurchaseTransactionByCountryCurrency(country,
                    currency, pDate, pageNumber, pageSize);
            return ResponseEntity.ok(transactions.stream()
                    .map(mapper::toDto)
                    .toList());
        } catch (DateTimeParseException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
