package org.example.assetmanagement.repository;

import org.example.assetmanagement.entity.Asset;
import org.example.assetmanagement.entity.AssetStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
class AssetRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("test_db")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private AssetRepository assetRepository;

    @BeforeEach
    void setUp() {
        assetRepository.deleteAll();
    }

    @Test
    void findByAssetCode_存在する資産コードの場合_対象を取得できる() {
        Asset asset = new Asset(
                "PC-001",
                "業務PC",
                "開発用ノートPC",
                "PC",
                AssetStatus.AVAILABLE,
                "東京オフィス"
        );
        assetRepository.save(asset);

        var actual = assetRepository.findByAssetCode("PC-001");

        assertThat(actual).isPresent();
        assertThat(actual.orElseThrow().getName()).isEqualTo("業務PC");
    }

    @Test
    void findByAssetCode_存在しない資産コードの場合_空を返す() {
        var actual = assetRepository.findByAssetCode("NOT-FOUND");

        assertThat(actual).isEmpty();
    }

    @Test
    void findByStatus_該当ステータスの資産一覧を取得できる() {
        assetRepository.save(new Asset("PC-001", "業務PC", "開発用", "PC", AssetStatus.AVAILABLE, "東京"));
        assetRepository.save(new Asset("PC-002", "予備PC", "予備機", "PC", AssetStatus.AVAILABLE, "大阪"));
        assetRepository.save(new Asset("PRJ-001", "プロジェクター", "会議室用", "AV", AssetStatus.MAINTENANCE, "東京"));

        List<Asset> actual = assetRepository.findByStatus(AssetStatus.AVAILABLE);

        assertThat(actual)
                .hasSize(2)
                .extracting(Asset::getAssetCode)
                .containsExactlyInAnyOrder("PC-001", "PC-002");
    }

    @Test
    void findByStatus_該当ステータスがない場合_空リストを返す() {
        assetRepository.save(new Asset("PRJ-001", "プロジェクター", "会議室用", "AV", AssetStatus.MAINTENANCE, "東京"));

        List<Asset> actual = assetRepository.findByStatus(AssetStatus.LOANED);

        assertThat(actual).isEmpty();
    }

    @Test
    void findByCategory_該当カテゴリの資産一覧を取得できる() {
        assetRepository.save(new Asset("PC-001", "業務PC", "開発用", "PC", AssetStatus.AVAILABLE, "東京"));
        assetRepository.save(new Asset("PC-002", "予備PC", "予備機", "PC", AssetStatus.LOANED, "大阪"));
        assetRepository.save(new Asset("PRJ-001", "プロジェクター", "会議室用", "AV", AssetStatus.AVAILABLE, "東京"));

        List<Asset> actual = assetRepository.findByCategory("PC");

        assertThat(actual)
                .hasSize(2)
                .extracting(Asset::getAssetCode)
                .containsExactlyInAnyOrder("PC-001", "PC-002");
    }

    @Test
    void findByCategory_該当カテゴリがない場合_空リストを返す() {
        assetRepository.save(new Asset("PRJ-001", "プロジェクター", "会議室用", "AV", AssetStatus.AVAILABLE, "東京"));

        List<Asset> actual = assetRepository.findByCategory("PC");

        assertThat(actual).isEmpty();
    }

    @Test
    void existsByAssetCode_存在する資産コードの場合_trueを返す() {
        assetRepository.save(new Asset("PC-001", "業務PC", "開発用", "PC", AssetStatus.AVAILABLE, "東京"));

        boolean actual = assetRepository.existsByAssetCode("PC-001");

        assertThat(actual).isTrue();
    }

    @Test
    void existsByAssetCode_存在しない資産コードの場合_falseを返す() {
        boolean actual = assetRepository.existsByAssetCode("NOT-FOUND");

        assertThat(actual).isFalse();
    }
}
