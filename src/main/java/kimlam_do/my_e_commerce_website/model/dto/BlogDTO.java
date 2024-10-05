package kimlam_do.my_e_commerce_website.model.dto;

import kimlam_do.my_e_commerce_website.model.entity.Blog;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BlogDTO {
    private Integer id;
    private String title;
    private String content;
    private UserDTO author;
    private BlogCategoryDTO blogCategory;
    private String featuredImage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static BlogDTO toDTO(Blog blog) {
        return (blog == null) ? null : BlogDTO.builder()
                .id(blog.getId())
                .title(blog.getTitle())
                .content(blog.getContent())
                .author(blog.getAuthor() != null ? UserDTO.toDTO(blog.getAuthor()) : null)
                .blogCategory(blog.getBlogCategory() != null ? BlogCategoryDTO.toDTO(blog.getBlogCategory()) : null)
                .featuredImage(blog.getFeaturedImage())
                .createdAt(blog.getCreatedAt())
                .updatedAt(blog.getUpdatedAt())
                .build();
    }
}