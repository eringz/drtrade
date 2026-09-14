package com.drtrade.application;

import com.drtrade.domain.model.CurrencyExchangeReport;
import com.drtrade.domain.model.ExchangeRate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class CurrencyReportService {

    public String processAndPublishReport(String baseCurrency, List<String> targetCurrencies) {
        List<ExchangeRate> mockRates = targetCurrencies.stream()
                .map(code -> new ExchangeRate(code, BigDecimal.valueOf(58.50)))
                .toList();

        CurrencyExchangeReport report = new CurrencyExchangeReport(
                baseCurrency != null ? baseCurrency : "PHP",
                LocalDateTime.now(),
                mockRates
        );

        String formattedMessage = formatReportPost(report);

        System.out.println("\n--- [DR TRADE FACEBOOK POST SIMULATION] ---");
        System.out.println(formattedMessage);
        System.out.println("-------------------------------------------\n");

        return "SIMULATED_POST_ID_" + System.currentTimeMillis();
    }

    private String formatReportPost(CurrencyExchangeReport report) {
        StringBuilder post = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy - hh:mm a");

        post.append("🎩 Good day, traders! Dr Trade here with your latest market pulse.\n\n");
        post.append("📊 Exchange Rate Update for ").append(report.baseCurrency()).append(":\n");
        post.append("🗓 Date: ").append(report.timestamp().format(formatter)).append("\n\n");

        for (ExchangeRate rate : report.rates()) {
            post.append(String.format("• 1 %s = %.4f %s\n", rate.currencyCode(), rate.rate(), report.baseCurrency()));
        }

        post.append("\nStay focused and trade responsibly! 💼📈\n");
        post.append("#DrTrade #Forex #ExchangeRates #MarketUpdate #PHP");

        return post.toString();
    }
}