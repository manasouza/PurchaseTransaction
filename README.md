# Purchase Transaction

This project can store purchase transactions in USD and retrieve such transactions converted on specified Currency
according to the transaction date period.

The conversions are based on registries that can be obtained at [Treasury Reporting Rates of Exchange](https://fiscaldata.treasury.gov/datasets/treasury-reporting-rates-exchange/treasury-reporting-rates-of-exchange): 

The APIs provided are:

* store Purchase Transactions based on USD
* retrieve Purchase Transactions based on Country, Currency and purchase date information, integrated with external API
* Import a CSV file to store Exchange Rates

The APIs are available on swagger UI and can be used locally by accessing http://localhost:8080/swagger-ui

## Tech Stack

* Java 17
* Spring Boot
* Maven