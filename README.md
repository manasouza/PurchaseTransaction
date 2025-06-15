# Purchase Transaction

This project can store purchase transactions in USD and retrieve such transactions converted on specified Currency
according to the transaction date period.

The conversions are based on registries that can be obtained at [Treasury Reporting Rates of Exchange](https://fiscaldata.treasury.gov/datasets/treasury-reporting-rates-exchange/treasury-reporting-rates-of-exchange),
which provides an API integration to supply the exchange rate information.

The APIs provided are:

* Store Purchase Transactions based on USD on a specific date (also known as purchase data)
* Retrieve Purchase Transactions based on country, currency and purchase date information
* Import a CSV file to store Exchange Rates, as a backup mechanism

## Tech Stack

* Java 17
* Spring Boot
* Maven
* HSQLDB
* Docker

## Functionality

Registrations done through the Store Purchase Transaction API can be retrieved by informing the purchase date
and Country/Currency information. The exchange rate used for the conversion is then accessed from the exchange rate API integration

The APIs are documented using Swagger and can be checked on http://localhost:8080/swagger-ui/index.htm

Since the Treasury Reporting Rates of Exchange also supplies exchange rate history information in the CSV format, 
there's an alternative to importing such information in case the API communication is not working (API down or some integration problem)
Anyway there's no implementation to persist this data so it's lost on application restart.

## Building and Running

The application can be built and executed using docker. For example:
```
docker build -t purchase-tx .
docker run -p 8080:8080 -v $(pwd)/db-data:/app/data purchase-tx
```