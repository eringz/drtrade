package com.drtrade.domain.model;

import java.math.BigDecimal;

public class ExchangeRate {
    public record ExchangeRate(String currencyCode, BigDecimal rate) {}
}
