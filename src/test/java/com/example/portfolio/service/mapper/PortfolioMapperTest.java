package com.example.portfolio.service.mapper;

import static com.example.portfolio.domain.PortfolioAsserts.*;
import static com.example.portfolio.domain.PortfolioTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PortfolioMapperTest {

    private PortfolioMapper portfolioMapper;

    @BeforeEach
    void setUp() {
        portfolioMapper = new PortfolioMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPortfolioSample1();
        var actual = portfolioMapper.toEntity(portfolioMapper.toDto(expected));
        assertPortfolioAllPropertiesEquals(expected, actual);
    }
}
