package com.wexinc.purchasetransaction.utils;

import java.time.format.DateTimeFormatter;

public class Constants {

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final String EXCHANGE_RATES_URL = "https://api.fiscaldata.treasury.gov/services/api/fiscal_service/v1/accounting/od/rates_of_exchange?page[size]=500&sort=-record_date&fields=record_date,country,currency,exchange_rate,effective_date";
}
