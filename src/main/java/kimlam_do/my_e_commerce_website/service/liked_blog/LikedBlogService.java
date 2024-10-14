package kimlam_do.my_e_commerce_website.service.liked_blog;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import kimlam_do.my_e_commerce_website.model.dto.LikedBlogDTO;

import java.util.List;

public interface LikedBlogService {
    List<LikedBlogDTO> getLikedBlogsByUser(int userId);

    ObjectNode likeBlog(JsonNode jsonNode);

    ObjectNode unLikeBlog(JsonNode jsonNode);
}