package com.drtrade.application;


import com.drtrade.domain.model.CurrencyExchangeReport;
import com.drtrade.domain.model.ExchangeRate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CurrencyReportService {
    private final RestClient restClient;

    public CurrencyReportService(RestClient restClient) {
        this.restClient = restClient;
    }

    public String processAndPublishReport(String baseCurrency, List<String> targetCurrencies) {
        String base = (baseCurrency != null) ? baseCurrency.toUpperCase() : "PHP";

        List<ExchangeRate> liveRates = fetchLiveRatesfromFrankfurter(base, targetCurrencies);

        CurrencyExchangeReport report = new CurrencyExchangeReport(
                base,
                LocalDateTime.now(),
                liveRates
        );

        String formattedMessage = formatReportPost(report);

        System.out.println("\n------------------------- DR Trade Soft Launch---------------------- ");
        System.out.println(formattedMessage);
        System.out.println("----------------------------------------------------------------------\n");

//        List<ExchangeRate> mockRates = targetCurrencies.stream()
//                .map(code -> new ExchangeRate(code, BigDecimal.valueOf(58.50)))
//                .toList();
//
//        CurrencyExchangeReport report = new CurrencyExchangeReport(
//                baseCurrency != null ? baseCurrency : "PHP",
//                LocalDateTime.now(),
//                mockRates
//        );
//
//        String formattedMessage = formatReportPost(report);
//
//        System.out.println("\n--- [DR TRADE FACEBOOK POST SIMULATION] ---");
//        System.out.println(formattedMessage);
//        System.out.println("-------------------------------------------\n");



        return "LIVE_POST_ID_" + System.currentTimeMillis();
    }

//    private String formatReportPost(CurrencyExchangeReport report) {
//        StringBuilder post = new StringBuilder();
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy - hh:mm a");
//
//        post.append("🎩 Good day, traders! Dr Trade here with your latest market pulse.\n\n");
//        post.append("📊 Exchange Rate Update for ").append(report.baseCurrency()).append(":\n");
//        post.append("🗓 Date: ").append(report.timestamp().format(formatter)).append("\n\n");
//
//        for (ExchangeRate rate : report.rates()) {
//            post.append(String.format("• 1 %s = %.4f %s\n", rate.currencyCode(), rate.rate(), report.baseCurrency()));
//        }
//
//        post.append("\nStay focused and trade responsibly! 💼📈\n");
//        post.append("#DrTrade #Forex #ExchangeRates #MarketUpdate #PHP");
//
//        return post.toString();
//    }
    private List<ExchangeRate>  fetchLiveRatesfromFrankfurter(String baseCurrency, List<String> targetCurrencies) {
        System.out.println("Base Currency:" + baseCurrency);

//        String url = "https://api.frankfurter.dev/v1/latest?from=" + baseCurrency;
        String url = "https://api.frankfurter.dev/v1/latest?from=PHP";

        List <ExchangeRate> ratesList = new ArrayList<>();

        try {
            FrankfurterResponse response = restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(FrankfurterResponse.class);

            if (response != null && response.rates() != null) {
                for (String target: targetCurrencies) {
                    String code = target.toUpperCase();
                    if (response.rates().containsKey(code)) {
                        BigDecimal rateFromBase = response.rates().get(code);

                        BigDecimal convertedRate = BigDecimal.ONE.divide(rateFromBase, 4, RoundingMode.HALF_UP);

                        ratesList.add(new ExchangeRate(code, convertedRate));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch live rates. falling back to default values. Error: " + e.getMessage());
        }

        return ratesList;
    }

    private String formatReportPost(CurrencyExchangeReport report) {
        StringBuilder post = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyy - hh:mm a");

        post.append("Good day Draters! Dr Trade here with your live market update of our Philippine Peso value across the countries\n");
        post.append("Exchange Rate Update (Base: ").append(report.baseCurrency()).append("):\n");
        post.append("Date: ").append(report.timestamp().format(formatter)).append("\n\n");

        for (ExchangeRate rate : report.rates()) {
            post.append(String.format(". 1 %s = %.4f %s\n", rate.currencyCode(), rate.rate(), report.baseCurrency()));
        }

        post.append("\nStay focused and be updated regularly\n");
        post.append("#DrTrade #Forex #ExchangeRates #MarketUpdate #PHP");

        return post.toString();
    }

    private record FrankfurterResponse(
            double amount,
            String base,
            String date,
            Map<String, BigDecimal> rates
    ) {}

}