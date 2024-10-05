package kimlam_do.my_e_commerce_website.model.dto;

import kimlam_do.my_e_commerce_website.model.entity.BlogCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlogCategoryDTO {
    private Integer id;
    private String name;

    public static BlogCategoryDTO toDTO(BlogCategory blogCategory) {
        return blogCategory == null ? null : BlogCategoryDTO.builder().id(blogCategory.getId()).name(blogCategory.getName()).build();
    }
}