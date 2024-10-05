package kimlam_do.my_e_commerce_website.model.dto;

import jakarta.persistence.*;
import kimlam_do.my_e_commerce_website.model.entity.Brand;
import kimlam_do.my_e_commerce_website.model.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDTO {
    private Integer id;
    private String name;
    private String alias;
    private String image;
    private boolean enabled;
    private List<Integer> brandIds = new ArrayList<>();

    public static CategoryDTO toDTO(Category category) {
        return category == null ? null : CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .alias(category.getAlias())
                .image(category.getImage())
                .brandIds(category.getBrands() != null ? category.getBrands().stream()
                        .map(Brand::getId)
                        .collect(Collectors.toList()) : null)
                .build();
    }
}