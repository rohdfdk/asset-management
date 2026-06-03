package org.example.assetmanagement.repository;

import org.example.assetmanagement.entity.Asset;
import org.example.assetmanagement.entity.AssetStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {
    Optional<Asset> findByAssetCode(String assetCode);
    List<Asset> findByStatus(AssetStatus status);
    List<Asset> findByCategory(String category);
    boolean existsByAssetCode(String assetCode);
}