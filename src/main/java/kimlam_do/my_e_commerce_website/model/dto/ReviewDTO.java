package kimlam_do.my_e_commerce_website.model.dto;

import kimlam_do.my_e_commerce_website.model.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDTO {
    private Integer id;
    private String content;
    private int rating;
    private LocalDateTime reviewTime;
    private Integer productId;
    private Integer userId;
    private String userPhoto;
    private String userFullName;
    private String productName;

    public static ReviewDTO toDTO(Review review) {
        return review == null ? null : ReviewDTO.builder().id(review.getId()).content(review.getContent()).rating(review.getRating()).reviewTime(review.getReviewTime()).productId(review.getProduct().getId()).userId(review.getUser() != null ? review.getUser().getId() : null).userPhoto(review.getUser().getPhoto()).userFullName(review.getUser() != null ? review.getUser().getFirstName() + " " + review.getUser().getLastName() : null).productName(review.getProduct().getName()).build();
    }
}