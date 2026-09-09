package com.drtrade.domain.model;
import java.time.LocalDateTime;
import java.util.List;

public record CurrencyExchangeReport (
    String baseCurrency,
    LocalDateTime timestamp,
    List<ExchangeRate> rates
) {}
