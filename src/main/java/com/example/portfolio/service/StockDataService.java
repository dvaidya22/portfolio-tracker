package com.example.portfolio.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class StockDataService {

    private static final Logger LOG = LoggerFactory.getLogger(StockDataService.class);
    
    @Value("${app.alpha-vantage.api-key:demo}")
    private String apiKey;
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    public StockDataService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }
    
    public BigDecimal getCurrentPrice(String ticker) {
        try {
            String url = String.format(
                "https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=%s&apikey=%s",
                ticker, apiKey
            );
            
            String response = restTemplate.getForObject(url, String.class);
            JsonNode jsonNode = objectMapper.readTree(response);
            
            // Check for API limit or error
            if (jsonNode.has("Note") || jsonNode.has("Error Message")) {
                LOG.warn("API limit reached or error for ticker: {}", ticker);
                return getMockPrice(ticker);
            }
            
            JsonNode globalQuote = jsonNode.get("Global Quote");
            if (globalQuote != null && globalQuote.has("05. price")) {
                return new BigDecimal(globalQuote.get("05. price").asText());
            }
            
            return getMockPrice(ticker);
        } catch (Exception e) {
            LOG.error("Error fetching price for ticker: {}", ticker, e);
            return getMockPrice(ticker);
        }
    }
    
    public Map<String, Object> getHistoricalData(String ticker) {
        try {
            String url = String.format(
                "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=%s&apikey=%s",
                ticker, apiKey
            );
            
            String response = restTemplate.getForObject(url, String.class);
            JsonNode jsonNode = objectMapper.readTree(response);
            
            if (jsonNode.has("Note") || jsonNode.has("Error Message")) {
                return getMockHistoricalData(ticker);
            }
            
            JsonNode timeSeries = jsonNode.get("Time Series (Daily)");
            if (timeSeries != null) {
                Map<String, Object> result = new HashMap<>();
                result.put("ticker", ticker);
                result.put("data", timeSeries);
                return result;
            }
            
            return getMockHistoricalData(ticker);
        } catch (Exception e) {
            LOG.error("Error fetching historical data for ticker: {}", ticker, e);
            return getMockHistoricalData(ticker);
        }
    }
    
    private BigDecimal getMockPrice(String ticker) {
        // Generate mock prices based on ticker hash for consistency
        int hash = Math.abs(ticker.hashCode());
        double basePrice = 50 + (hash % 500);
        double variation = (hash % 100) / 100.0 * 10 - 5; // ±5% variation
        return BigDecimal.valueOf(basePrice + variation).setScale(2, BigDecimal.ROUND_HALF_UP);
    }
    
    private Map<String, Object> getMockHistoricalData(String ticker) {
        Map<String, Object> result = new HashMap<>();
        result.put("ticker", ticker);
        
        // Generate 30 days of mock data
        Map<String, Map<String, String>> mockData = new HashMap<>();
        BigDecimal basePrice = getMockPrice(ticker);
        
        for (int i = 0; i < 30; i++) {
            String date = java.time.LocalDate.now().minusDays(i).toString();
            double variation = (Math.random() - 0.5) * 0.1; // ±5% daily variation
            BigDecimal dayPrice = basePrice.multiply(BigDecimal.valueOf(1 + variation));
            
            Map<String, String> dayData = new HashMap<>();
            dayData.put("1. open", dayPrice.toString());
            dayData.put("2. high", dayPrice.multiply(BigDecimal.valueOf(1.02)).toString());
            dayData.put("3. low", dayPrice.multiply(BigDecimal.valueOf(0.98)).toString());
            dayData.put("4. close", dayPrice.toString());
            dayData.put("5. volume", String.valueOf(1000000 + (int)(Math.random() * 5000000)));
            
            mockData.put(date, dayData);
        }
        
        result.put("data", mockData);
        return result;
    }
}