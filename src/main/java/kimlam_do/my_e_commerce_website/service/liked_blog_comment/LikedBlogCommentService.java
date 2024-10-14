package kimlam_do.my_e_commerce_website.service.liked_blog_comment;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import kimlam_do.my_e_commerce_website.model.dto.LikedBlogCommentDTO;

import java.util.List;

public interface LikedBlogCommentService {
    List<LikedBlogCommentDTO> getLikedBlogCommentsByUser(int userId);

    ObjectNode likeBlogComment(JsonNode jsonNode);

    ObjectNode unLikeComment(JsonNode jsonNode);
}