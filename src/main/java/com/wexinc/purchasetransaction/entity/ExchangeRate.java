package com.wexinc.purchasetransaction.entity;

import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class ExchangeRate implements Comparable<ExchangeRate> {

    /**
     * The date that data was published.
     */
    private LocalDate referenceDate;
    /**
     * Country associated with a given exchange rate.
     */
    private String country;
    /**
     * Currency associated with a given exchange rate.
     */
    private String currency;
    /**
     * Exchange rate at which any foreign currency unit will be valued, and reported at, against the U.S. Dollar.
     */
    private Float exchangeRate;
    /**
     * The date that a given exchange rate took effect.
     */
    private LocalDate effectiveDate;

    @Override
    public int compareTo(ExchangeRate o) {
        return this.getEffectiveDate().compareTo(o.getEffectiveDate());
    }
}
