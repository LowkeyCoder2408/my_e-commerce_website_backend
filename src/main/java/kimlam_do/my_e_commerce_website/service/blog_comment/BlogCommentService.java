package kimlam_do.my_e_commerce_website.service.blog_comment;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import kimlam_do.my_e_commerce_website.model.entity.BlogComment;

import java.util.Optional;

public interface BlogCommentService {
    Optional<BlogComment> getBlogCommentById(int id);

    ObjectNode addAComment(JsonNode jsonData);

    ObjectNode updateAComment(JsonNode jsonData);

    ObjectNode deleteComment(int blogCommentId);
}