package com.example.portfolio.service;

import com.example.portfolio.domain.Asset;
import com.example.portfolio.domain.Portfolio;
import com.example.portfolio.repository.AssetRepository;
import com.example.portfolio.repository.PortfolioRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class PortfolioAnalyticsService {
    
    private final AssetRepository assetRepository;
    private final PortfolioRepository portfolioRepository;
    private final StockDataService stockDataService;
    
    public PortfolioAnalyticsService(AssetRepository assetRepository, 
                                   PortfolioRepository portfolioRepository,
                                   StockDataService stockDataService) {
        this.assetRepository = assetRepository;
        this.portfolioRepository = portfolioRepository;
        this.stockDataService = stockDataService;
    }
    
    public Map<String, Object> calculatePortfolioMetrics(Long portfolioId) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
            .orElseThrow(() -> new RuntimeException("Portfolio not found"));
        
        List<Asset> assets = assetRepository.findAll().stream()
            .filter(asset -> asset.getPortfolio() != null && asset.getPortfolio().getId().equals(portfolioId))
            .toList();
        
        Map<String, Object> metrics = new HashMap<>();
        
        // Calculate total portfolio value
        BigDecimal totalValue = BigDecimal.ZERO;
        BigDecimal totalCost = BigDecimal.ZERO;
        
        List<Map<String, Object>> assetDetails = new ArrayList<>();
        
        for (Asset asset : assets) {
            BigDecimal currentPrice = stockDataService.getCurrentPrice(asset.getTicker());
            BigDecimal assetValue = currentPrice.multiply(BigDecimal.valueOf(asset.getQuantity()));
            BigDecimal assetCost = asset.getAvgPrice().multiply(BigDecimal.valueOf(asset.getQuantity()));
            
            totalValue = totalValue.add(assetValue);
            totalCost = totalCost.add(assetCost);
            
            Map<String, Object> assetDetail = new HashMap<>();
            assetDetail.put("ticker", asset.getTicker());
            assetDetail.put("quantity", asset.getQuantity());
            assetDetail.put("avgPrice", asset.getAvgPrice());
            assetDetail.put("currentPrice", currentPrice);
            assetDetail.put("currentValue", assetValue);
            assetDetail.put("gainLoss", assetValue.subtract(assetCost));
            assetDetail.put("gainLossPercent", 
                assetCost.compareTo(BigDecimal.ZERO) > 0 ? 
                    assetValue.subtract(assetCost).divide(assetCost, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)) : 
                    BigDecimal.ZERO);
            
            assetDetails.add(assetDetail);
        }
        
        metrics.put("totalValue", totalValue);
        metrics.put("totalCost", totalCost);
        metrics.put("totalGainLoss", totalValue.subtract(totalCost));
        metrics.put("totalGainLossPercent", 
            totalCost.compareTo(BigDecimal.ZERO) > 0 ? 
                totalValue.subtract(totalCost).divide(totalCost, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)) : 
                BigDecimal.ZERO);
        metrics.put("assets", assetDetails);
        metrics.put("diversificationScore", calculateDiversificationScore(assets));
        metrics.put("recommendedAsset", getRecommendedAsset(assets));
        
        return metrics;
    }
    
    private BigDecimal calculateDiversificationScore(List<Asset> assets) {
        if (assets.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        // Simple diversification score based on number of assets and sector distribution
        int numAssets = assets.size();
        Set<String> sectors = new HashSet<>();
        
        // Mock sector classification based on ticker
        for (Asset asset : assets) {
            String ticker = asset.getTicker().toUpperCase();
            if (ticker.startsWith("A") || ticker.startsWith("B")) {
                sectors.add("Technology");
            } else if (ticker.startsWith("C") || ticker.startsWith("D")) {
                sectors.add("Healthcare");
            } else if (ticker.startsWith("E") || ticker.startsWith("F")) {
                sectors.add("Finance");
            } else {
                sectors.add("Consumer");
            }
        }
        
        // Score based on number of assets (max 50 points) and sector diversity (max 50 points)
        double assetScore = Math.min(numAssets * 5, 50);
        double sectorScore = Math.min(sectors.size() * 12.5, 50);
        
        return BigDecimal.valueOf(assetScore + sectorScore).setScale(1, RoundingMode.HALF_UP);
    }
    
    private String getRecommendedAsset(List<Asset> assets) {
        // Simple recommendation logic
        Set<String> ownedTickers = new HashSet<>();
        for (Asset asset : assets) {
            ownedTickers.add(asset.getTicker().toUpperCase());
        }
        
        String[] recommendations = {"MSFT", "GOOGL", "AMZN", "TSLA", "NVDA", "META", "NFLX", "CRM"};
        
        for (String ticker : recommendations) {
            if (!ownedTickers.contains(ticker)) {
                return ticker;
            }
        }
        
        return "VTI"; // Default to total market ETF
    }
}