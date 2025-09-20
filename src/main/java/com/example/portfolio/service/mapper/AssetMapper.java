package com.example.portfolio.service.mapper;

import com.example.portfolio.domain.Asset;
import com.example.portfolio.domain.Portfolio;
import com.example.portfolio.service.dto.AssetDTO;
import com.example.portfolio.service.dto.PortfolioDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Asset} and its DTO {@link AssetDTO}.
 */
@Mapper(componentModel = "spring")
public interface AssetMapper extends EntityMapper<AssetDTO, Asset> {
    @Mapping(target = "portfolio", source = "portfolio", qualifiedByName = "portfolioName")
    AssetDTO toDto(Asset s);

    @Named("portfolioName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    PortfolioDTO toDtoPortfolioName(Portfolio portfolio);
}
