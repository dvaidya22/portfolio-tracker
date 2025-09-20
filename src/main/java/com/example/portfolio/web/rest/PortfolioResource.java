package com.example.portfolio.web.rest;

import com.example.portfolio.repository.PortfolioRepository;
import com.example.portfolio.service.PortfolioService;
import com.example.portfolio.service.dto.PortfolioDTO;
import com.example.portfolio.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.example.portfolio.domain.Portfolio}.
 */
@RestController
@RequestMapping("/api/portfolios")
public class PortfolioResource {

    private static final Logger LOG = LoggerFactory.getLogger(PortfolioResource.class);

    private static final String ENTITY_NAME = "portfolio";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PortfolioService portfolioService;

    private final PortfolioRepository portfolioRepository;

    public PortfolioResource(PortfolioService portfolioService, PortfolioRepository portfolioRepository) {
        this.portfolioService = portfolioService;
        this.portfolioRepository = portfolioRepository;
    }

    /**
     * {@code POST  /portfolios} : Create a new portfolio.
     *
     * @param portfolioDTO the portfolioDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new portfolioDTO, or with status {@code 400 (Bad Request)} if the portfolio has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<PortfolioDTO> createPortfolio(@Valid @RequestBody PortfolioDTO portfolioDTO) throws URISyntaxException {
        LOG.debug("REST request to save Portfolio : {}", portfolioDTO);
        if (portfolioDTO.getId() != null) {
            throw new BadRequestAlertException("A new portfolio cannot already have an ID", ENTITY_NAME, "idexists");
        }
        portfolioDTO = portfolioService.save(portfolioDTO);
        return ResponseEntity.created(new URI("/api/portfolios/" + portfolioDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, portfolioDTO.getId().toString()))
            .body(portfolioDTO);
    }

    /**
     * {@code PUT  /portfolios/:id} : Updates an existing portfolio.
     *
     * @param id the id of the portfolioDTO to save.
     * @param portfolioDTO the portfolioDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated portfolioDTO,
     * or with status {@code 400 (Bad Request)} if the portfolioDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the portfolioDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PortfolioDTO> updatePortfolio(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PortfolioDTO portfolioDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Portfolio : {}, {}", id, portfolioDTO);
        if (portfolioDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, portfolioDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!portfolioRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        portfolioDTO = portfolioService.update(portfolioDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, portfolioDTO.getId().toString()))
            .body(portfolioDTO);
    }

    /**
     * {@code PATCH  /portfolios/:id} : Partial updates given fields of an existing portfolio, field will ignore if it is null
     *
     * @param id the id of the portfolioDTO to save.
     * @param portfolioDTO the portfolioDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated portfolioDTO,
     * or with status {@code 400 (Bad Request)} if the portfolioDTO is not valid,
     * or with status {@code 404 (Not Found)} if the portfolioDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the portfolioDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PortfolioDTO> partialUpdatePortfolio(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PortfolioDTO portfolioDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Portfolio partially : {}, {}", id, portfolioDTO);
        if (portfolioDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, portfolioDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!portfolioRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PortfolioDTO> result = portfolioService.partialUpdate(portfolioDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, portfolioDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /portfolios} : get all the portfolios.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of portfolios in body.
     */
    @GetMapping("")
    public ResponseEntity<List<PortfolioDTO>> getAllPortfolios(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of Portfolios");
        Page<PortfolioDTO> page;
        if (eagerload) {
            page = portfolioService.findAllWithEagerRelationships(pageable);
        } else {
            page = portfolioService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /portfolios/:id} : get the "id" portfolio.
     *
     * @param id the id of the portfolioDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the portfolioDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PortfolioDTO> getPortfolio(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Portfolio : {}", id);
        Optional<PortfolioDTO> portfolioDTO = portfolioService.findOne(id);
        return ResponseUtil.wrapOrNotFound(portfolioDTO);
    }

    /**
     * {@code DELETE  /portfolios/:id} : delete the "id" portfolio.
     *
     * @param id the id of the portfolioDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePortfolio(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Portfolio : {}", id);
        portfolioService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
