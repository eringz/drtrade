package com.drtrade.domain.model;

import java.math.BigDecimal;

public record ExchangeRate(String currencyCode, BigDecimal rate) {}

