package kimlam_do.my_e_commerce_website.service.review;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import kimlam_do.my_e_commerce_website.model.dto.ReviewDTO;
import kimlam_do.my_e_commerce_website.model.entity.Review;

import java.util.List;

public interface ReviewService {
    List<ReviewDTO> getAllReviews();

    ObjectNode addReview(JsonNode jsonData);

    ObjectNode updateReview(JsonNode jsonData);

    ObjectNode deleteReview(int reviewId);

    Review getReviewByUserIdAndProductId(int userId, int productId);
}