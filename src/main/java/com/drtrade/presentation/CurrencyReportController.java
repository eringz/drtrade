package com.drtrade.presentation;

import com.drtrade.application.CurrencyReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping
public class CurrencyReportController {
    private final CurrencyReportService currencyReportSErvice;

    public CurrencyReportController(CurrencyReportService currencyReportService) {
        this.currencyReportSErvice = currencyReportService;
    }

    @PostMapping("/publish")
    public ResponseEntity<PublishReportResponse> publishReport(@RequestBody(required = false) PublishReportRequest request) {
        String baseCurrency = (request != null && request.baseCurrency() != null) ? request.baseCurrency() : "PHP";
        List<String> targets = (request != null && request.targetCurrencies() != null) ? request.targetCurrencies : List.of("USD", "EUR", "JPY", "GBP", "AUD");
        String postId = currencyReportSErvice.processAndPublishReport(baseCurrency, targets);

        return ResponseEntity.ok(new PublishReportResponse("SUCCESS", postId, "test completed successfully"));
    }

    public record PublishReportRequest(String baseCurrency, List<String> targetCurrencies) {}
    public record PublishReportResponse(String status, String postId, String message) {}
}
