package com.example.portfolio.web.rest;

import static com.example.portfolio.domain.PortfolioAsserts.*;
import static com.example.portfolio.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.portfolio.IntegrationTest;
import com.example.portfolio.domain.Portfolio;
import com.example.portfolio.repository.PortfolioRepository;
import com.example.portfolio.service.PortfolioService;
import com.example.portfolio.service.dto.PortfolioDTO;
import com.example.portfolio.service.mapper.PortfolioMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link PortfolioResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class PortfolioResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/portfolios";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Mock
    private PortfolioRepository portfolioRepositoryMock;

    @Autowired
    private PortfolioMapper portfolioMapper;

    @Mock
    private PortfolioService portfolioServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPortfolioMockMvc;

    private Portfolio portfolio;

    private Portfolio insertedPortfolio;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Portfolio createEntity() {
        return new Portfolio().name(DEFAULT_NAME).createdDate(DEFAULT_CREATED_DATE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Portfolio createUpdatedEntity() {
        return new Portfolio().name(UPDATED_NAME).createdDate(UPDATED_CREATED_DATE);
    }

    @BeforeEach
    void initTest() {
        portfolio = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedPortfolio != null) {
            portfolioRepository.delete(insertedPortfolio);
            insertedPortfolio = null;
        }
    }

    @Test
    @Transactional
    void createPortfolio() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Portfolio
        PortfolioDTO portfolioDTO = portfolioMapper.toDto(portfolio);
        var returnedPortfolioDTO = om.readValue(
            restPortfolioMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(portfolioDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PortfolioDTO.class
        );

        // Validate the Portfolio in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPortfolio = portfolioMapper.toEntity(returnedPortfolioDTO);
        assertPortfolioUpdatableFieldsEquals(returnedPortfolio, getPersistedPortfolio(returnedPortfolio));

        insertedPortfolio = returnedPortfolio;
    }

    @Test
    @Transactional
    void createPortfolioWithExistingId() throws Exception {
        // Create the Portfolio with an existing ID
        portfolio.setId(1L);
        PortfolioDTO portfolioDTO = portfolioMapper.toDto(portfolio);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPortfolioMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(portfolioDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Portfolio in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        portfolio.setName(null);

        // Create the Portfolio, which fails.
        PortfolioDTO portfolioDTO = portfolioMapper.toDto(portfolio);

        restPortfolioMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(portfolioDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        portfolio.setCreatedDate(null);

        // Create the Portfolio, which fails.
        PortfolioDTO portfolioDTO = portfolioMapper.toDto(portfolio);

        restPortfolioMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(portfolioDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPortfolios() throws Exception {
        // Initialize the database
        insertedPortfolio = portfolioRepository.saveAndFlush(portfolio);

        // Get all the portfolioList
        restPortfolioMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(portfolio.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPortfoliosWithEagerRelationshipsIsEnabled() throws Exception {
        when(portfolioServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPortfolioMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(portfolioServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPortfoliosWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(portfolioServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPortfolioMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(portfolioRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getPortfolio() throws Exception {
        // Initialize the database
        insertedPortfolio = portfolioRepository.saveAndFlush(portfolio);

        // Get the portfolio
        restPortfolioMockMvc
            .perform(get(ENTITY_API_URL_ID, portfolio.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(portfolio.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingPortfolio() throws Exception {
        // Get the portfolio
        restPortfolioMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPortfolio() throws Exception {
        // Initialize the database
        insertedPortfolio = portfolioRepository.saveAndFlush(portfolio);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the portfolio
        Portfolio updatedPortfolio = portfolioRepository.findById(portfolio.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPortfolio are not directly saved in db
        em.detach(updatedPortfolio);
        updatedPortfolio.name(UPDATED_NAME).createdDate(UPDATED_CREATED_DATE);
        PortfolioDTO portfolioDTO = portfolioMapper.toDto(updatedPortfolio);

        restPortfolioMockMvc
            .perform(
                put(ENTITY_API_URL_ID, portfolioDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(portfolioDTO))
            )
            .andExpect(status().isOk());

        // Validate the Portfolio in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPortfolioToMatchAllProperties(updatedPortfolio);
    }

    @Test
    @Transactional
    void putNonExistingPortfolio() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        portfolio.setId(longCount.incrementAndGet());

        // Create the Portfolio
        PortfolioDTO portfolioDTO = portfolioMapper.toDto(portfolio);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPortfolioMockMvc
            .perform(
                put(ENTITY_API_URL_ID, portfolioDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(portfolioDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Portfolio in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPortfolio() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        portfolio.setId(longCount.incrementAndGet());

        // Create the Portfolio
        PortfolioDTO portfolioDTO = portfolioMapper.toDto(portfolio);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPortfolioMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(portfolioDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Portfolio in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPortfolio() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        portfolio.setId(longCount.incrementAndGet());

        // Create the Portfolio
        PortfolioDTO portfolioDTO = portfolioMapper.toDto(portfolio);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPortfolioMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(portfolioDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Portfolio in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePortfolioWithPatch() throws Exception {
        // Initialize the database
        insertedPortfolio = portfolioRepository.saveAndFlush(portfolio);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the portfolio using partial update
        Portfolio partialUpdatedPortfolio = new Portfolio();
        partialUpdatedPortfolio.setId(portfolio.getId());

        partialUpdatedPortfolio.name(UPDATED_NAME);

        restPortfolioMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPortfolio.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPortfolio))
            )
            .andExpect(status().isOk());

        // Validate the Portfolio in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPortfolioUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPortfolio, portfolio),
            getPersistedPortfolio(portfolio)
        );
    }

    @Test
    @Transactional
    void fullUpdatePortfolioWithPatch() throws Exception {
        // Initialize the database
        insertedPortfolio = portfolioRepository.saveAndFlush(portfolio);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the portfolio using partial update
        Portfolio partialUpdatedPortfolio = new Portfolio();
        partialUpdatedPortfolio.setId(portfolio.getId());

        partialUpdatedPortfolio.name(UPDATED_NAME).createdDate(UPDATED_CREATED_DATE);

        restPortfolioMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPortfolio.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPortfolio))
            )
            .andExpect(status().isOk());

        // Validate the Portfolio in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPortfolioUpdatableFieldsEquals(partialUpdatedPortfolio, getPersistedPortfolio(partialUpdatedPortfolio));
    }

    @Test
    @Transactional
    void patchNonExistingPortfolio() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        portfolio.setId(longCount.incrementAndGet());

        // Create the Portfolio
        PortfolioDTO portfolioDTO = portfolioMapper.toDto(portfolio);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPortfolioMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, portfolioDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(portfolioDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Portfolio in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPortfolio() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        portfolio.setId(longCount.incrementAndGet());

        // Create the Portfolio
        PortfolioDTO portfolioDTO = portfolioMapper.toDto(portfolio);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPortfolioMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(portfolioDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Portfolio in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPortfolio() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        portfolio.setId(longCount.incrementAndGet());

        // Create the Portfolio
        PortfolioDTO portfolioDTO = portfolioMapper.toDto(portfolio);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPortfolioMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(portfolioDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Portfolio in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePortfolio() throws Exception {
        // Initialize the database
        insertedPortfolio = portfolioRepository.saveAndFlush(portfolio);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the portfolio
        restPortfolioMockMvc
            .perform(delete(ENTITY_API_URL_ID, portfolio.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return portfolioRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Portfolio getPersistedPortfolio(Portfolio portfolio) {
        return portfolioRepository.findById(portfolio.getId()).orElseThrow();
    }

    protected void assertPersistedPortfolioToMatchAllProperties(Portfolio expectedPortfolio) {
        assertPortfolioAllPropertiesEquals(expectedPortfolio, getPersistedPortfolio(expectedPortfolio));
    }

    protected void assertPersistedPortfolioToMatchUpdatableProperties(Portfolio expectedPortfolio) {
        assertPortfolioAllUpdatablePropertiesEquals(expectedPortfolio, getPersistedPortfolio(expectedPortfolio));
    }
}
