package com.drtrade.application;

import com.drtrade.domain.model.CurrencyExchangeReport;
import com.drtrade.domain.model.ExchangeRate;
import com.drtrade.infrastructure.facebook.FacebookPublisher;
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
    private final FacebookPublisher facebookPublisher;

    public CurrencyReportService(RestClient restClient, FacebookPublisher facebookPublisher) {
        this.restClient = restClient;
        this.facebookPublisher = facebookPublisher;
    }

    public String processAndPublishReport(String baseCurrency, List<String> targetCurrencies) {
        String base = (baseCurrency != null) ? baseCurrency.toUpperCase() : "PHP";

        List<ExchangeRate> liveRates = fetchLiveRatesFromFrankfurter(base, targetCurrencies);

        CurrencyExchangeReport report = new CurrencyExchangeReport(
                base,
                LocalDateTime.now(),
                liveRates
        );

        String formattedMessage = formatReportPost(report);

        System.out.println("\n--- [PREPARING FACEBOOK PUBLISH] ---");
        System.out.println(formattedMessage);

        // Ipo-post sa Meta / Facebook Graph API
        return facebookPublisher.publishToPage(formattedMessage);
    }

    private List<ExchangeRate> fetchLiveRatesFromFrankfurter(String baseCurrency, List<String> targetCurrencies) {
        String url = "https://api.frankfurter.app/latest?from=USD";
        List<ExchangeRate> ratesList = new ArrayList<>();

        try {
            FrankfurterResponse response = restClient.get()
                    .uri(url)
                    .header("User-Agent", "DrTradeApp/1.0")
                    .retrieve()
                    .body(FrankfurterResponse.class);

            if (response != null && response.rates() != null && response.rates().containsKey("PHP")) {
                BigDecimal usdToPhp = response.rates().get("PHP");

                for (String target : targetCurrencies) {
                    String code = target.toUpperCase();
                    if (code.equals("USD")) {
                        ratesList.add(new ExchangeRate("USD", usdToPhp));
                    } else if (response.rates().containsKey(code)) {
                        BigDecimal usdToTarget = response.rates().get(code);
                        BigDecimal targetToPhp = usdToPhp.divide(usdToTarget, 4, RoundingMode.HALF_UP);
                        ratesList.add(new ExchangeRate(code, targetToPhp));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch rates: " + e.getMessage());
        }

        return ratesList;
    }

    private String formatReportPost(CurrencyExchangeReport report) {
        StringBuilder post = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy - hh:mm a");

        post.append("🎩 Good day, traders! Dr Trade here with your live market update.\n\n");
        post.append("📊 Exchange Rate Update (Base: ").append(report.baseCurrency()).append("):\n");
        post.append("🗓 Date: ").append(report.timestamp().format(formatter)).append("\n\n");

        for (ExchangeRate rate : report.rates()) {
            post.append(String.format("• 1 %s = %.4f %s\n", rate.currencyCode(), rate.rate(), report.baseCurrency()));
        }

        post.append("\nStay focused and trade responsibly! 💼📈\n");
        post.append("#DrTrade #Forex #ExchangeRates #MarketUpdate #PHP");

        return post.toString();
    }

    private record FrankfurterResponse(double amount, String base, String date, Map<String, BigDecimal> rates) {}
}