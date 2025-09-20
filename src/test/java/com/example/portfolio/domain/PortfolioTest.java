package com.example.portfolio.domain;

import static com.example.portfolio.domain.AssetTestSamples.*;
import static com.example.portfolio.domain.PortfolioTestSamples.*;
import static com.example.portfolio.domain.UserAccountTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.example.portfolio.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class PortfolioTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Portfolio.class);
        Portfolio portfolio1 = getPortfolioSample1();
        Portfolio portfolio2 = new Portfolio();
        assertThat(portfolio1).isNotEqualTo(portfolio2);

        portfolio2.setId(portfolio1.getId());
        assertThat(portfolio1).isEqualTo(portfolio2);

        portfolio2 = getPortfolioSample2();
        assertThat(portfolio1).isNotEqualTo(portfolio2);
    }

    @Test
    void assetTest() {
        Portfolio portfolio = getPortfolioRandomSampleGenerator();
        Asset assetBack = getAssetRandomSampleGenerator();

        portfolio.addAsset(assetBack);
        assertThat(portfolio.getAssets()).containsOnly(assetBack);
        assertThat(assetBack.getPortfolio()).isEqualTo(portfolio);

        portfolio.removeAsset(assetBack);
        assertThat(portfolio.getAssets()).doesNotContain(assetBack);
        assertThat(assetBack.getPortfolio()).isNull();

        portfolio.assets(new HashSet<>(Set.of(assetBack)));
        assertThat(portfolio.getAssets()).containsOnly(assetBack);
        assertThat(assetBack.getPortfolio()).isEqualTo(portfolio);

        portfolio.setAssets(new HashSet<>());
        assertThat(portfolio.getAssets()).doesNotContain(assetBack);
        assertThat(assetBack.getPortfolio()).isNull();
    }

    @Test
    void userTest() {
        Portfolio portfolio = getPortfolioRandomSampleGenerator();
        UserAccount userAccountBack = getUserAccountRandomSampleGenerator();

        portfolio.setUser(userAccountBack);
        assertThat(portfolio.getUser()).isEqualTo(userAccountBack);

        portfolio.user(null);
        assertThat(portfolio.getUser()).isNull();
    }
}
