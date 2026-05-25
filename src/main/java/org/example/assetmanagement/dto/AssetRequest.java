package org.example.assetmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssetRequest {

    @NotBlank(message = "資産コードは必須です")
    @Size(max = 50, message = "資産コードは50文字以内で入力してください")
    private String assetCode;

    @NotBlank(message = "資産名は必須です")
    @Size(max = 100, message = "資産名は100文字以内で入力してください")
    private String name;

    @Size(max = 500, message = "説明は500文字以内で入力してください")
    private String description;

    @NotBlank(message = "カテゴリは必須です")
    private String category;

    @Size(max = 200, message = "設置場所は200文字以内で入力してください")
    private String location;
}