package kimlam_do.my_e_commerce_website.model.dto;

import kimlam_do.my_e_commerce_website.model.entity.LikedBlog;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LikedBlogDTO {
    private Integer id;
    private Integer blogId;
    private Integer userId;
    private LocalDateTime likedAt;

    public static LikedBlogDTO toDTO(LikedBlog likedBlog) {
        return likedBlog == null ? null : LikedBlogDTO.builder()
                .id(likedBlog.getId())
                .blogId(likedBlog.getBlog() != null ? likedBlog.getBlog().getId() : null)
                .userId(likedBlog.getUser() != null ? likedBlog.getUser().getId() : null)
                .likedAt(likedBlog.getLikedAt())
                .build();
    }
}