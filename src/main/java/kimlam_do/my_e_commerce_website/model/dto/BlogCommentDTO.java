package kimlam_do.my_e_commerce_website.model.dto;

import kimlam_do.my_e_commerce_website.model.entity.BlogComment;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BlogCommentDTO {
    private Integer id;
    private String content;
    private Integer blogId;
    private UserDTO user;
    private LocalDateTime createdAt;

    public static BlogCommentDTO toDTO(BlogComment blogComment) {
        return (blogComment == null) ? null : BlogCommentDTO.builder()
                .id(blogComment.getId())
                .content(blogComment.getContent())
                .blogId(blogComment.getBlog() != null ? blogComment.getBlog().getId() : null)
                .user(blogComment.getUser() != null ? UserDTO.toDTO(blogComment.getUser()) : null)
                .createdAt(blogComment.getCreatedAt())
                .build();
    }
}