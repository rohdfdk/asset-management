package org.example.assetmanagement.service;

import lombok.RequiredArgsConstructor;
import org.example.assetmanagement.dto.AssetRequest;
import org.example.assetmanagement.dto.AssetResponse;
import org.example.assetmanagement.entity.Asset;
import org.example.assetmanagement.entity.AssetStatus;
import org.example.assetmanagement.repository.AssetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AssetService {

    private final AssetRepository assetRepository;

    public List<AssetResponse> findAll() {
        return assetRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public AssetResponse findById(Long id) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asset not found: " + id));
        return toResponse(asset);
    }

    public List<AssetResponse> findByStatus(AssetStatus status) {
        return assetRepository.findByStatus(status).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public AssetResponse create(AssetRequest request) {
        if (assetRepository.existsByAssetCode(request.getAssetCode())) {
            throw new RuntimeException("Asset code already exists: " + request.getAssetCode());
        }

        Asset asset = new Asset(
                request.getAssetCode(),
                request.getName(),
                request.getDescription(),
                request.getCategory(),
                AssetStatus.AVAILABLE,
                request.getLocation()
        );

        Asset saved = assetRepository.save(asset);
        return toResponse(saved);
    }

    @Transactional
    public AssetResponse update(Long id, AssetRequest request) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asset not found: " + id));

        asset.updateDetails(
                request.getName(),
                request.getDescription(),
                request.getCategory(),
                request.getLocation()
        );

        Asset updated = assetRepository.save(asset);
        return toResponse(updated);
    }

    @Transactional
    public AssetResponse changeStatus(Long id, AssetStatus nextStatus) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asset not found: " + id));

        asset.changeStatus(nextStatus);

        Asset updated = assetRepository.save(asset);
        return toResponse(updated);
    }

    @Transactional
    public void delete(Long id) {
        if (!assetRepository.existsById(id)) {
            throw new RuntimeException("Asset not found: " + id);
        }
        assetRepository.deleteById(id);
    }

    private AssetResponse toResponse(Asset asset) {
        return new AssetResponse(
                asset.getId(),
                asset.getAssetCode(),
                asset.getName(),
                asset.getDescription(),
                asset.getCategory(),
                asset.getStatus().name(),
                asset.getLocation(),
                asset.getCreatedAt(),
                asset.getUpdatedAt()
        );
    }
}