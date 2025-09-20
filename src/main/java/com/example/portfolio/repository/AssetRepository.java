package com.example.portfolio.repository;

import com.example.portfolio.domain.Asset;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Asset entity.
 */
@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {
    default Optional<Asset> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Asset> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Asset> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(value = "select asset from Asset asset left join fetch asset.portfolio", countQuery = "select count(asset) from Asset asset")
    Page<Asset> findAllWithToOneRelationships(Pageable pageable);

    @Query("select asset from Asset asset left join fetch asset.portfolio")
    List<Asset> findAllWithToOneRelationships();

    @Query("select asset from Asset asset left join fetch asset.portfolio where asset.id =:id")
    Optional<Asset> findOneWithToOneRelationships(@Param("id") Long id);
}
