package kimlam_do.my_e_commerce_website.model.dto;

import kimlam_do.my_e_commerce_website.model.entity.Brand;
import kimlam_do.my_e_commerce_website.model.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BrandDTO {
    private Integer id;

    private String name;

    private String logo;

    private List<Integer> categoryIds;

    public static BrandDTO toDTO(Brand brand) {
        return brand == null ? null : BrandDTO.builder()
                .id(brand.getId())
                .name(brand.getName())
                .logo(brand.getLogo())
                .categoryIds(brand.getCategories() != null ? brand.getCategories().stream()
                        .map(Category::getId)
                        .collect(Collectors.toList()) : null)
                .build();
    }
}