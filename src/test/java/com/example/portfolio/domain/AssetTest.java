package com.example.portfolio.domain;

import static com.example.portfolio.domain.AssetTestSamples.*;
import static com.example.portfolio.domain.PortfolioTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.example.portfolio.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AssetTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Asset.class);
        Asset asset1 = getAssetSample1();
        Asset asset2 = new Asset();
        assertThat(asset1).isNotEqualTo(asset2);

        asset2.setId(asset1.getId());
        assertThat(asset1).isEqualTo(asset2);

        asset2 = getAssetSample2();
        assertThat(asset1).isNotEqualTo(asset2);
    }

    @Test
    void portfolioTest() {
        Asset asset = getAssetRandomSampleGenerator();
        Portfolio portfolioBack = getPortfolioRandomSampleGenerator();

        asset.setPortfolio(portfolioBack);
        assertThat(asset.getPortfolio()).isEqualTo(portfolioBack);

        asset.portfolio(null);
        assertThat(asset.getPortfolio()).isNull();
    }
}
