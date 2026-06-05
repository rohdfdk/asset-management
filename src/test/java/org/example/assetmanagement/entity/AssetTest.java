package org.example.assetmanagement.entity;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AssetTest {

    @Nested
    class constructorのテスト {

        @Test
        void constructor_有効な値を指定した場合_資産を生成できる() {
            Asset asset = new Asset(
                    "IT-ASSET-001",
                    "MacBook Pro",
                    "開発用PC",
                    "HARDWARE",
                    AssetStatus.AVAILABLE,
                    "東京本社"
            );

            assertThat(asset.getAssetCode()).isEqualTo("IT-ASSET-001");
            assertThat(asset.getName()).isEqualTo("MacBook Pro");
            assertThat(asset.getDescription()).isEqualTo("開発用PC");
            assertThat(asset.getCategory()).isEqualTo("HARDWARE");
            assertThat(asset.getStatus()).isEqualTo(AssetStatus.AVAILABLE);
            assertThat(asset.getLocation()).isEqualTo("東京本社");
        }

        @Test
        void constructor_statusがnullの場合_AVAILABLEで生成される() {
            Asset asset = new Asset(
                    "IT-ASSET-001",
                    "MacBook Pro",
                    "開発用PC",
                    "HARDWARE",
                    null,
                    "東京本社"
            );

            assertThat(asset.getStatus()).isEqualTo(AssetStatus.AVAILABLE);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   "})
        void constructor_assetCodeがnullまたは空の場合_IllegalArgumentExceptionをスローする(String invalidCode) {
            assertThatThrownBy(() -> new Asset(
                    invalidCode,
                    "MacBook Pro",
                    "開発用PC",
                    "HARDWARE",
                    AssetStatus.AVAILABLE,
                    "東京本社"
            )).isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("assetCode is required");
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "IT@ASSET",
                "資産コード",
                "IT ASSET",
                "IT.ASSET",
                "IT/ASSET"
        })
        void constructor_assetCodeに許可されていない文字が含まれる場合_IllegalArgumentExceptionをスローする(String invalidCode) {
            assertThatThrownBy(() -> new Asset(
                    invalidCode,
                    "MacBook Pro",
                    "開発用PC",
                    "HARDWARE",
                    AssetStatus.AVAILABLE,
                    "東京本社"
            )).isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("assetCode must contain only alphanumeric characters, hyphen, or underscore");
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "ABC",
                "abc",
                "123",
                "IT-ASSET-001",
                "IT_ASSET_001",
                "IT-ASSET_001"
        })
        void constructor_assetCodeが半角英数字ハイフンアンダースコアの場合_資産を生成できる(String validCode) {
            Asset asset = new Asset(
                    validCode,
                    "MacBook Pro",
                    "開発用PC",
                    "HARDWARE",
                    AssetStatus.AVAILABLE,
                    "東京本社"
            );

            assertThat(asset.getAssetCode()).isEqualTo(validCode);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   "})
        void constructor_nameがnullまたは空の場合_IllegalArgumentExceptionをスローする(String invalidName) {
            assertThatThrownBy(() -> new Asset(
                    "IT-ASSET-001",
                    invalidName,
                    "開発用PC",
                    "HARDWARE",
                    AssetStatus.AVAILABLE,
                    "東京本社"
            )).isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("name is required");
        }

        @Test
        void constructor_nameが100文字の場合_資産を生成できる() {
            String name = "あ".repeat(100);

            Asset asset = new Asset(
                    "IT-ASSET-001",
                    name,
                    "開発用PC",
                    "HARDWARE",
                    AssetStatus.AVAILABLE,
                    "東京本社"
            );

            assertThat(asset.getName()).hasSize(100);
        }

        @Test
        void constructor_nameが101文字の場合_IllegalArgumentExceptionをスローする() {
            String name = "あ".repeat(101);

            assertThatThrownBy(() -> new Asset(
                    "IT-ASSET-001",
                    name,
                    "開発用PC",
                    "HARDWARE",
                    AssetStatus.AVAILABLE,
                    "東京本社"
            )).isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("name must be between 1 and 100 characters");
        }
    }

    @Nested
    class updateAssetCodeのテスト {

        @Test
        void updateAssetCode_有効な値を指定した場合_assetCodeを更新できる() {
            Asset asset = availableAsset();

            asset.updateAssetCode("IT-ASSET_002");

            assertThat(asset.getAssetCode()).isEqualTo("IT-ASSET_002");
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   "})
        void updateAssetCode_nullまたは空の場合_IllegalArgumentExceptionをスローする(String invalidCode) {
            Asset asset = availableAsset();

            assertThatThrownBy(() -> asset.updateAssetCode(invalidCode))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("assetCode is required");
        }

        @ParameterizedTest
        @ValueSource(strings = {"IT@ASSET", "資産コード", "IT ASSET", "IT/ASSET"})
        void updateAssetCode_許可されていない文字が含まれる場合_IllegalArgumentExceptionをスローする(String invalidCode) {
            Asset asset = availableAsset();

            assertThatThrownBy(() -> asset.updateAssetCode(invalidCode))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("assetCode must contain only alphanumeric characters, hyphen, or underscore");
        }
    }

    @Nested
    class updateNameのテスト {

        @Test
        void updateName_有効な値を指定した場合_nameを更新できる() {
            Asset asset = availableAsset();

            asset.updateName("更新後MacBook Pro");

            assertThat(asset.getName()).isEqualTo("更新後MacBook Pro");
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   "})
        void updateName_nullまたは空の場合_IllegalArgumentExceptionをスローする(String invalidName) {
            Asset asset = availableAsset();

            assertThatThrownBy(() -> asset.updateName(invalidName))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("name is required");
        }

        @Test
        void updateName_101文字の場合_IllegalArgumentExceptionをスローする() {
            Asset asset = availableAsset();
            String name = "あ".repeat(101);

            assertThatThrownBy(() -> asset.updateName(name))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("name must be between 1 and 100 characters");
        }
    }

    @Nested
    class updateDetailsのテスト {

        @Test
        void updateDetails_有効な値を指定した場合_詳細を更新できる() {
            Asset asset = availableAsset();

            asset.updateDetails(
                    "更新後MacBook Pro",
                    "更新後説明",
                    "UPDATED_CATEGORY",
                    "大阪支社"
            );

            assertThat(asset.getName()).isEqualTo("更新後MacBook Pro");
            assertThat(asset.getDescription()).isEqualTo("更新後説明");
            assertThat(asset.getCategory()).isEqualTo("UPDATED_CATEGORY");
            assertThat(asset.getLocation()).isEqualTo("大阪支社");
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   "})
        void updateDetails_nameがnullまたは空の場合_IllegalArgumentExceptionをスローする(String invalidName) {
            Asset asset = availableAsset();

            assertThatThrownBy(() -> asset.updateDetails(
                    invalidName,
                    "更新後説明",
                    "UPDATED_CATEGORY",
                    "大阪支社"
            )).isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("name is required");
        }

        @Test
        void updateDetails_nameが101文字の場合_IllegalArgumentExceptionをスローする() {
            Asset asset = availableAsset();
            String name = "あ".repeat(101);

            assertThatThrownBy(() -> asset.updateDetails(
                    name,
                    "更新後説明",
                    "UPDATED_CATEGORY",
                    "大阪支社"
            )).isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("name must be between 1 and 100 characters");
        }
    }

    @Nested
    class changeStatusのテスト {

        @Nested
        class AVAILABLEからの遷移 {

            @ParameterizedTest
            @EnumSource(
                    value = AssetStatus.class,
                    names = {"LOANED", "MAINTENANCE", "RETIRED"}
            )
            void changeStatus_AVAILABLEから許可された状態の場合_状態変更できる(AssetStatus nextStatus) {
                Asset asset = availableAsset();

                asset.changeStatus(nextStatus);

                assertThat(asset.getStatus()).isEqualTo(nextStatus);
            }

            @Test
            void changeStatus_AVAILABLEからAVAILABLEの場合_IllegalStateExceptionをスローする() {
                Asset asset = availableAsset();

                assertThatThrownBy(() -> asset.changeStatus(AssetStatus.AVAILABLE))
                        .isInstanceOf(IllegalStateException.class)
                        .hasMessage("Invalid asset status transition: AVAILABLE -> AVAILABLE");
            }

            @Test
            void changeStatus_AVAILABLEからnullの場合_IllegalStateExceptionをスローする() {
                Asset asset = availableAsset();

                assertThatThrownBy(() -> asset.changeStatus(null))
                        .isInstanceOf(IllegalStateException.class)
                        .hasMessage("Invalid asset status transition: AVAILABLE -> null");
            }
        }

        @Nested
        class LOANEDからの遷移 {

            @ParameterizedTest
            @EnumSource(
                    value = AssetStatus.class,
                    names = {"AVAILABLE", "MAINTENANCE"}
            )
            void changeStatus_LOANEDから許可された状態の場合_状態変更できる(AssetStatus nextStatus) {
                Asset asset = loanedAsset();

                asset.changeStatus(nextStatus);

                assertThat(asset.getStatus()).isEqualTo(nextStatus);
            }

            @ParameterizedTest
            @EnumSource(
                    value = AssetStatus.class,
                    names = {"LOANED", "RETIRED"}
            )
            void changeStatus_LOANEDから許可されていない状態の場合_IllegalStateExceptionをスローする(AssetStatus nextStatus) {
                Asset asset = loanedAsset();

                assertThatThrownBy(() -> asset.changeStatus(nextStatus))
                        .isInstanceOf(IllegalStateException.class)
                        .hasMessage("Invalid asset status transition: LOANED -> " + nextStatus);
            }

            @Test
            void changeStatus_LOANEDからnullの場合_IllegalStateExceptionをスローする() {
                Asset asset = loanedAsset();

                assertThatThrownBy(() -> asset.changeStatus(null))
                        .isInstanceOf(IllegalStateException.class)
                        .hasMessage("Invalid asset status transition: LOANED -> null");
            }
        }

        @Nested
        class MAINTENANCEからの遷移 {

            @Test
            void changeStatus_MAINTENANCEからAVAILABLEの場合_状態変更できる() {
                Asset asset = maintenanceAsset();

                asset.changeStatus(AssetStatus.AVAILABLE);

                assertThat(asset.getStatus()).isEqualTo(AssetStatus.AVAILABLE);
            }

            @ParameterizedTest
            @EnumSource(
                    value = AssetStatus.class,
                    names = {"LOANED", "MAINTENANCE", "RETIRED"}
            )
            void changeStatus_MAINTENANCEから許可されていない状態の場合_IllegalStateExceptionをスローする(AssetStatus nextStatus) {
                Asset asset = maintenanceAsset();

                assertThatThrownBy(() -> asset.changeStatus(nextStatus))
                        .isInstanceOf(IllegalStateException.class)
                        .hasMessage("Invalid asset status transition: MAINTENANCE -> " + nextStatus);
            }

            @Test
            void changeStatus_MAINTENANCEからnullの場合_IllegalStateExceptionをスローする() {
                Asset asset = maintenanceAsset();

                assertThatThrownBy(() -> asset.changeStatus(null))
                        .isInstanceOf(IllegalStateException.class)
                        .hasMessage("Invalid asset status transition: MAINTENANCE -> null");
            }
        }

        @Nested
        class RETIREDからの遷移 {

            @ParameterizedTest
            @EnumSource(AssetStatus.class)
            void changeStatus_RETIREDから任意の状態の場合_IllegalStateExceptionをスローする(AssetStatus nextStatus) {
                Asset asset = retiredAsset();

                assertThatThrownBy(() -> asset.changeStatus(nextStatus))
                        .isInstanceOf(IllegalStateException.class)
                        .hasMessage("Invalid asset status transition: RETIRED -> " + nextStatus);
            }

            @Test
            void changeStatus_RETIREDからnullの場合_IllegalStateExceptionをスローする() {
                Asset asset = retiredAsset();

                assertThatThrownBy(() -> asset.changeStatus(null))
                        .isInstanceOf(IllegalStateException.class)
                        .hasMessage("Invalid asset status transition: RETIRED -> null");
            }
        }
    }

    @Nested
    class AssetStatusのテスト {

        @Test
        void canTransitTo_nextStatusがnullの場合_falseを返す() {
            assertThat(AssetStatus.AVAILABLE.canTransitTo(null)).isFalse();
        }
    }

    private Asset availableAsset() {
        return new Asset(
                "CODE-01",
                "MacBook Pro",
                "説明",
                "HARDWARE",
                AssetStatus.AVAILABLE,
                "東京本社"
        );
    }

    private Asset loanedAsset() {
        return new Asset(
                "CODE-01",
                "MacBook Pro",
                "説明",
                "HARDWARE",
                AssetStatus.LOANED,
                "東京本社"
        );
    }

    private Asset maintenanceAsset() {
        return new Asset(
                "CODE-01",
                "MacBook Pro",
                "説明",
                "HARDWARE",
                AssetStatus.MAINTENANCE,
                "東京本社"
        );
    }

    private Asset retiredAsset() {
        return new Asset(
                "CODE-01",
                "MacBook Pro",
                "説明",
                "HARDWARE",
                AssetStatus.RETIRED,
                "東京本社"
        );
    }
}