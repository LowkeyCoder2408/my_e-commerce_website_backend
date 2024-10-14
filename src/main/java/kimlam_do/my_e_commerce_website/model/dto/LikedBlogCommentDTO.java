package kimlam_do.my_e_commerce_website.model.dto;

import kimlam_do.my_e_commerce_website.model.entity.LikedBlog;
import kimlam_do.my_e_commerce_website.model.entity.LikedBlogComment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LikedBlogCommentDTO {
    private Integer id;
    private Integer blogCommentId;
    private Integer userId;
    private LocalDateTime likedAt;

    public static LikedBlogCommentDTO toDTO(LikedBlogComment likedBlogComment) {
        return likedBlogComment == null ? null : LikedBlogCommentDTO.builder()
                .id(likedBlogComment.getId())
                .blogCommentId(likedBlogComment.getBlogComment() != null ? likedBlogComment.getBlogComment().getId() : null)
                .userId(likedBlogComment.getUser() != null ? likedBlogComment.getUser().getId() : null)
                .likedAt(likedBlogComment.getLikedAt())
                .build();
    }
}