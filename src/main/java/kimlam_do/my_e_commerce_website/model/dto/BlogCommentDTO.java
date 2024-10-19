package kimlam_do.my_e_commerce_website.model.dto;

import kimlam_do.my_e_commerce_website.model.entity.BlogComment;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class BlogCommentDTO {
    private Integer id;
    private String content;
    private Integer blogId;
    private UserDTO user;
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdatedTime;
    private boolean isAuthorComment;
    private Integer parentCommentId;
    private List<BlogCommentDTO> replies;
    private List<LikedBlogCommentDTO> likedBlogComments;
    private String replyTo;

    public static BlogCommentDTO toDTO(BlogComment blogComment) {
        return (blogComment == null) ? null : BlogCommentDTO.builder()
                .id(blogComment.getId())
                .content(blogComment.getContent())
                .blogId(blogComment.getBlog() != null ? blogComment.getBlog().getId() : null)
                .user(blogComment.getUser() != null ? UserDTO.toDTO(blogComment.getUser()) : null)
                .createdAt(blogComment.getCreatedAt())
                .lastUpdatedTime(blogComment.getUpdatedAt())
                .isAuthorComment(blogComment.getBlog().getAuthor().getId() == blogComment.getUser().getId())
                .parentCommentId(blogComment.getParentComment() != null ? blogComment.getParentComment().getId() : null)
                .replies(blogComment.getReplies() != null ? blogComment.getReplies().stream()
                        .map(BlogCommentDTO::toDTO)
                        .collect(Collectors.toList()) : null)
                .replyTo(blogComment.getParentComment() != null && blogComment.getParentComment().getUser() != null
                        ? blogComment.getParentComment().getUser().getLastName() + " " + blogComment.getParentComment().getUser().getFirstName()
                        : null)
                .likedBlogComments(blogComment.getLikedBlogComments() != null ? blogComment.getLikedBlogComments().stream().map(LikedBlogCommentDTO::toDTO).collect(Collectors.toList()) : null)
                .build();
    }
}