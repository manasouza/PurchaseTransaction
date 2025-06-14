package com.wexinc.purchasetransaction.dto;

import lombok.Getter;

@Getter
public class ExchangeRateDto {

    private String record_date;
    private String country;
    private String currency;
    private String country_currency_desc;
    private String exchange_rate;
    private String effective_date;
    private String src_line_nbr;
    private String record_fiscal_year;
    private String record_fiscal_quarter;
    private String record_calendar_year;
    private String record_calendar_quarter;
    private String record_calendar_month;
    private String record_calendar_day;
}
