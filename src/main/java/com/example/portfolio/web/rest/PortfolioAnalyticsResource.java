package com.example.portfolio.web.rest;

import com.example.portfolio.service.PortfolioAnalyticsService;
import com.example.portfolio.service.StockDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/portfolio-analytics")
public class PortfolioAnalyticsResource {

    private static final Logger LOG = LoggerFactory.getLogger(PortfolioAnalyticsResource.class);

    private final PortfolioAnalyticsService portfolioAnalyticsService;
    private final StockDataService stockDataService;

    public PortfolioAnalyticsResource(PortfolioAnalyticsService portfolioAnalyticsService,
                                    StockDataService stockDataService) {
        this.portfolioAnalyticsService = portfolioAnalyticsService;
        this.stockDataService = stockDataService;
    }

    @GetMapping("/portfolio/{id}/metrics")
    public ResponseEntity<Map<String, Object>> getPortfolioMetrics(@PathVariable Long id) {
        LOG.debug("REST request to get Portfolio metrics : {}", id);
        Map<String, Object> metrics = portfolioAnalyticsService.calculatePortfolioMetrics(id);
        return ResponseEntity.ok(metrics);
    }

    @GetMapping("/stock/{ticker}/price")
    public ResponseEntity<Map<String, Object>> getStockPrice(@PathVariable String ticker) {
        LOG.debug("REST request to get stock price for ticker : {}", ticker);
        Map<String, Object> result = Map.of(
            "ticker", ticker,
            "price", stockDataService.getCurrentPrice(ticker)
        );
        return ResponseEntity.ok(result);
    }

    @GetMapping("/stock/{ticker}/historical")
    public ResponseEntity<Map<String, Object>> getHistoricalData(@PathVariable String ticker) {
        LOG.debug("REST request to get historical data for ticker : {}", ticker);
        Map<String, Object> data = stockDataService.getHistoricalData(ticker);
        return ResponseEntity.ok(data);
    }
}