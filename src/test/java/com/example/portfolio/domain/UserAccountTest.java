package com.example.portfolio.domain;

import static com.example.portfolio.domain.PortfolioTestSamples.*;
import static com.example.portfolio.domain.UserAccountTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.example.portfolio.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class UserAccountTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserAccount.class);
        UserAccount userAccount1 = getUserAccountSample1();
        UserAccount userAccount2 = new UserAccount();
        assertThat(userAccount1).isNotEqualTo(userAccount2);

        userAccount2.setId(userAccount1.getId());
        assertThat(userAccount1).isEqualTo(userAccount2);

        userAccount2 = getUserAccountSample2();
        assertThat(userAccount1).isNotEqualTo(userAccount2);
    }

    @Test
    void portfolioTest() {
        UserAccount userAccount = getUserAccountRandomSampleGenerator();
        Portfolio portfolioBack = getPortfolioRandomSampleGenerator();

        userAccount.addPortfolio(portfolioBack);
        assertThat(userAccount.getPortfolios()).containsOnly(portfolioBack);
        assertThat(portfolioBack.getUser()).isEqualTo(userAccount);

        userAccount.removePortfolio(portfolioBack);
        assertThat(userAccount.getPortfolios()).doesNotContain(portfolioBack);
        assertThat(portfolioBack.getUser()).isNull();

        userAccount.portfolios(new HashSet<>(Set.of(portfolioBack)));
        assertThat(userAccount.getPortfolios()).containsOnly(portfolioBack);
        assertThat(portfolioBack.getUser()).isEqualTo(userAccount);

        userAccount.setPortfolios(new HashSet<>());
        assertThat(userAccount.getPortfolios()).doesNotContain(portfolioBack);
        assertThat(portfolioBack.getUser()).isNull();
    }
}
