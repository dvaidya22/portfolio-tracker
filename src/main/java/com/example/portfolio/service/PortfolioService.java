package com.example.portfolio.service;

import com.example.portfolio.domain.Portfolio;
import com.example.portfolio.repository.PortfolioRepository;
import com.example.portfolio.service.dto.PortfolioDTO;
import com.example.portfolio.service.mapper.PortfolioMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.example.portfolio.domain.Portfolio}.
 */
@Service
@Transactional
public class PortfolioService {

    private static final Logger LOG = LoggerFactory.getLogger(PortfolioService.class);

    private final PortfolioRepository portfolioRepository;

    private final PortfolioMapper portfolioMapper;

    public PortfolioService(PortfolioRepository portfolioRepository, PortfolioMapper portfolioMapper) {
        this.portfolioRepository = portfolioRepository;
        this.portfolioMapper = portfolioMapper;
    }

    /**
     * Save a portfolio.
     *
     * @param portfolioDTO the entity to save.
     * @return the persisted entity.
     */
    public PortfolioDTO save(PortfolioDTO portfolioDTO) {
        LOG.debug("Request to save Portfolio : {}", portfolioDTO);
        Portfolio portfolio = portfolioMapper.toEntity(portfolioDTO);
        portfolio = portfolioRepository.save(portfolio);
        return portfolioMapper.toDto(portfolio);
    }

    /**
     * Update a portfolio.
     *
     * @param portfolioDTO the entity to save.
     * @return the persisted entity.
     */
    public PortfolioDTO update(PortfolioDTO portfolioDTO) {
        LOG.debug("Request to update Portfolio : {}", portfolioDTO);
        Portfolio portfolio = portfolioMapper.toEntity(portfolioDTO);
        portfolio = portfolioRepository.save(portfolio);
        return portfolioMapper.toDto(portfolio);
    }

    /**
     * Partially update a portfolio.
     *
     * @param portfolioDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<PortfolioDTO> partialUpdate(PortfolioDTO portfolioDTO) {
        LOG.debug("Request to partially update Portfolio : {}", portfolioDTO);

        return portfolioRepository
            .findById(portfolioDTO.getId())
            .map(existingPortfolio -> {
                portfolioMapper.partialUpdate(existingPortfolio, portfolioDTO);

                return existingPortfolio;
            })
            .map(portfolioRepository::save)
            .map(portfolioMapper::toDto);
    }

    /**
     * Get all the portfolios.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<PortfolioDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Portfolios");
        return portfolioRepository.findAll(pageable).map(portfolioMapper::toDto);
    }

    /**
     * Get all the portfolios with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<PortfolioDTO> findAllWithEagerRelationships(Pageable pageable) {
        return portfolioRepository.findAllWithEagerRelationships(pageable).map(portfolioMapper::toDto);
    }

    /**
     * Get one portfolio by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PortfolioDTO> findOne(Long id) {
        LOG.debug("Request to get Portfolio : {}", id);
        return portfolioRepository.findOneWithEagerRelationships(id).map(portfolioMapper::toDto);
    }

    /**
     * Delete the portfolio by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Portfolio : {}", id);
        portfolioRepository.deleteById(id);
    }
}
