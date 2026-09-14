package com.drtrade.presentation;

import com.drtrade.application.CurrencyReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
public class CurrencyReportController {

    private final CurrencyReportService currencyReportService; // Siguraduhing lowercase 'e'

    public CurrencyReportController(CurrencyReportService currencyReportService) {
        this.currencyReportService = currencyReportService;
    }

    @PostMapping("/publish")
    public ResponseEntity<PublishReportResponse> publishReport(@RequestBody(required = false) PublishReportRequest request) {
        String baseCurrency = (request != null && request.baseCurrency() != null) ? request.baseCurrency() : "PHP";
        List<String> targets = (request != null && request.targetCurrencies() != null && !request.targetCurrencies().isEmpty())
                ? request.targetCurrencies()
                : List.of("USD", "EUR", "JPY", "GBP", "AUD");


        String postId = currencyReportService.processAndPublishReport(baseCurrency, targets);

        return ResponseEntity.ok(new PublishReportResponse("SUCCESS", postId, "Vertical slice test completed successfully."));
    }

    public record PublishReportRequest(String baseCurrency, List<String> targetCurrencies) {}
    public record PublishReportResponse(String status, String postId, String message) {}
}