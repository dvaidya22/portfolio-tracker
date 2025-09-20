package com.example.portfolio.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.portfolio.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PortfolioDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PortfolioDTO.class);
        PortfolioDTO portfolioDTO1 = new PortfolioDTO();
        portfolioDTO1.setId(1L);
        PortfolioDTO portfolioDTO2 = new PortfolioDTO();
        assertThat(portfolioDTO1).isNotEqualTo(portfolioDTO2);
        portfolioDTO2.setId(portfolioDTO1.getId());
        assertThat(portfolioDTO1).isEqualTo(portfolioDTO2);
        portfolioDTO2.setId(2L);
        assertThat(portfolioDTO1).isNotEqualTo(portfolioDTO2);
        portfolioDTO1.setId(null);
        assertThat(portfolioDTO1).isNotEqualTo(portfolioDTO2);
    }
}
