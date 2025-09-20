package com.example.portfolio.service.mapper;

import static com.example.portfolio.domain.UserAccountAsserts.*;
import static com.example.portfolio.domain.UserAccountTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserAccountMapperTest {

    private UserAccountMapper userAccountMapper;

    @BeforeEach
    void setUp() {
        userAccountMapper = new UserAccountMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getUserAccountSample1();
        var actual = userAccountMapper.toEntity(userAccountMapper.toDto(expected));
        assertUserAccountAllPropertiesEquals(expected, actual);
    }
}
