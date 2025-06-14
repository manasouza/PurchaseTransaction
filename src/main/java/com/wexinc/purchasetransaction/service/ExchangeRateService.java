package com.wexinc.purchasetransaction.service;

import com.wexinc.purchasetransaction.dto.ExchangeDateResponse;
import com.wexinc.purchasetransaction.dto.ExchangeRateMapper;
import com.wexinc.purchasetransaction.entity.ExchangeRate;
import com.wexinc.purchasetransaction.repository.ExchangeRateRepository;
import com.wexinc.purchasetransaction.utils.Constants;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExchangeRateService {

    public static final String EXCHANGE_RATES_URL = "https://api.fiscaldata.treasury.gov/services/api/fiscal_service/v1/accounting/od/rates_of_exchange?page[number]=%d&page[size]=%d&sort=-record_date&fields=record_date,country,currency,exchange_rate,effective_date";

    @NonNull
    private final ExchangeRateRepository repository;
    @NonNull
    private final RestTemplate restTemplate;
    @NonNull
    private final ExchangeRateMapper mapper;

    public void importData(InputStream inputStream) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            Set<ExchangeRate> exchangeRates = new HashSet<>();
            reader.lines()
                    .skip(1)
                    .filter(line -> !line.trim().isEmpty())
                    .map(this::parseLine)
                    .forEach(exchangeRates::add);
            log.debug("Exchange Rate data to be imported: {}", exchangeRates);
            repository.create(exchangeRates);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load exchange rates from CSV", e);
        }
    }

    /**
     * Retrieve the Exchange Rate from API integration. As a fallback it searches from Exchange repository for previously
     * imported data
     *
     * @param country      the country name
     * @param currency     the currency name
     * @param purchaseDate the date or the purchase transaction
     * @param pageNumber   the page number that relates to the data from Exchange Rates integration
     * @param pageSize     the page size that relates to the data from Exchange Rates integration
     * @return the ExchangeRate object
     */
    Optional<ExchangeRate> getExchangeRate(String country, String currency, LocalDate purchaseDate, int pageNumber,
                                           int pageSize) {
        ResponseEntity<ExchangeDateResponse> response = restTemplate.exchange(String.format(EXCHANGE_RATES_URL, pageNumber, pageSize),
                HttpMethod.GET, null, new ParameterizedTypeReference<>() {});
        ExchangeDateResponse body = response.getBody();
        if (response.getStatusCode().is2xxSuccessful() && body != null && !body.getData().isEmpty()) {
            ExchangeDateResponse.ExternalMeta meta = body.getMeta();
            ExchangeDateResponse.ExternalLinks links = body.getLinks();
            return body.getData().stream()
                    .map(mapper::toEntity)
                    .sorted(Comparator.comparing(ExchangeRate::getEffectiveDate).reversed())
                    .filter(er -> country.equals(er.getCountry()) && currency.equals(er.getCurrency())
                                    && (purchaseDate.equals(er.getEffectiveDate()) || purchaseDate.isAfter(er.getEffectiveDate())))
                    .findFirst();
        } else {
            return repository.findMostRecentByCountryAndCurrency(country, currency, purchaseDate);
        }
    }

    /**
     * Interpret the line with the following headers:
     *  Record Date,Country - Currency Description,Exchange Rate,Effective Date
     * @param line the CSV line
     * @return
     */
    private ExchangeRate parseLine(String line) {
        String[] tokens = line.split(",");
        LocalDate referenceDate = LocalDate.parse(tokens[0], Constants.DATE_FORMATTER);
        String country = tokens[1].split("-")[0];
        String currencyName = tokens[1].split("-")[1];
        Float rate = Float.parseFloat(tokens[2]);
        LocalDate effectiveDate = LocalDate.parse(tokens[3], Constants.DATE_FORMATTER);
        return new ExchangeRate(referenceDate, country, currencyName, rate, effectiveDate);
    }
}
