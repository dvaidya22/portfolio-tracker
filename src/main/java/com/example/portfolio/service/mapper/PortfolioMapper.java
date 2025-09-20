package com.example.portfolio.service.mapper;

import com.example.portfolio.domain.Portfolio;
import com.example.portfolio.domain.UserAccount;
import com.example.portfolio.service.dto.PortfolioDTO;
import com.example.portfolio.service.dto.UserAccountDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Portfolio} and its DTO {@link PortfolioDTO}.
 */
@Mapper(componentModel = "spring")
public interface PortfolioMapper extends EntityMapper<PortfolioDTO, Portfolio> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userAccountLogin")
    PortfolioDTO toDto(Portfolio s);

    @Named("userAccountLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserAccountDTO toDtoUserAccountLogin(UserAccount userAccount);
}
