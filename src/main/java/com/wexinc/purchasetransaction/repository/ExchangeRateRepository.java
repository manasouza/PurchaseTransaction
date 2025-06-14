package com.wexinc.purchasetransaction.repository;

import com.wexinc.purchasetransaction.entity.ExchangeRate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ExchangeRateRepository {

    private final Set<ExchangeRate> exchangeRates = new HashSet<>();

    public Optional<ExchangeRate> findMostRecentByCountryAndCurrency(String country, String currency, LocalDate date) {
        return exchangeRates.stream()
                .sorted(Comparator.comparing(ExchangeRate::getEffectiveDate).reversed())
                .filter(e -> e.getCountry().equals(country))
                .filter(e -> e.getCurrency().equalsIgnoreCase(currency))
                .filter(e -> e.getEffectiveDate().equals(date) || e.getEffectiveDate().isBefore(date))
                .findFirst();
    }

    public void create(Collection<ExchangeRate> exchangeRates) {
        this.exchangeRates.addAll(exchangeRates);
    }
}
