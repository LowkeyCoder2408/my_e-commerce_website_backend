package kimlam_do.my_e_commerce_website.service.review;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import kimlam_do.my_e_commerce_website.model.dto.ReviewDTO;
import kimlam_do.my_e_commerce_website.model.entity.Product;
import kimlam_do.my_e_commerce_website.model.entity.Review;
import kimlam_do.my_e_commerce_website.model.entity.User;
import kimlam_do.my_e_commerce_website.repository.ProductRepository;
import kimlam_do.my_e_commerce_website.repository.ReviewRepository;
import kimlam_do.my_e_commerce_website.repository.UserRepository;
import kimlam_do.my_e_commerce_website.security.UserAuthorization;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final UserAuthorization userAuthorization;

    @Override
    public List<ReviewDTO> getAllReviews() {
        List<Review> reviews = reviewRepository.findAll();
        return reviews.stream()
                .map(ReviewDTO::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ObjectNode addReview(JsonNode jsonData) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();

        int rating = Integer.parseInt(formatStringByJson(String.valueOf(jsonData.get("rating"))));
        String content = formatStringByJson(jsonData.has("content") ? jsonData.get("content").asText() : "");
        int userId = Integer.parseInt(formatStringByJson(String.valueOf(jsonData.get("userId"))));
        int productId = Integer.parseInt(formatStringByJson(String.valueOf(jsonData.get("productId"))));

        // Kiểm tra rating và content
        if (rating < 1 || rating > 5) {
            response.put("message", "Số sao khi đánh giá phải nằm trong phạm vi từ 1 đến 5");
            response.put("status", "error");
            return response;
        }
        if (content == null || content.trim().isEmpty() || content.length() > 300) {
            response.put("message", "Nội dung đánh giá không được để trống hoặc vượt quá 300 ký tự");
            response.put("status", "error");
            return response;
        }

        // Tìm user và product
        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            response.put("message", "Không tồn tại user với id: " + userId);
            response.put("status", "error");
            return response;
        }
        User user = userOptional.get();

        Optional<Product> productOptional = productRepository.findById(productId);
        if (!productOptional.isPresent()) {
            response.put("message", "Không tồn tại sản phẩm với id: " + productId);
            response.put("status", "error");
            return response;
        }
        Product product = productOptional.get();

        // Xác thực và kiểm tra quyền hạn của người dùng (phải thêm cho userId của mình, không thêm cho người khác)
        User currentUser = userAuthorization.getUserAuthorization(userId, response);
        if (currentUser == null) {
            return response;
        }

        // Tạo và thiết lập review
        Review review = new Review();
        review.setContent(content);
        review.setRating(rating);
        review.setUser(user);
        review.setProduct(product);
        review.setReviewTime(LocalDateTime.now());
        product.addReview(review);

        // Lưu review và product
        reviewRepository.save(review);
        productRepository.save(product);

        response.put("message", "Đánh giá sản phẩm thành công");
        response.put("status", "success");
        return response;
    }

    @Override
    public ObjectNode updateReview(JsonNode jsonData) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();

        int rating = Integer.parseInt(formatStringByJson(String.valueOf(jsonData.get("rating"))));
        String content = formatStringByJson(jsonData.has("content") ? jsonData.get("content").asText() : "");
        int reviewId = Integer.parseInt(formatStringByJson(String.valueOf(jsonData.get("reviewId"))));
        int productId = Integer.parseInt(formatStringByJson(String.valueOf(jsonData.get("productId"))));

        // Kiểm tra rating và content
        if (rating < 1 || rating > 5) {
            response.put("message", "Số sao khi đánh giá phải nằm trong phạm vi từ 1 đến 5");
            response.put("status", "error");
            return response;
        }
        if (content == null || content.trim().isEmpty() || content.length() > 300) {
            response.put("message", "Nội dung đánh giá không được để trống hoặc vượt quá 300 ký tự");
            response.put("status", "error");
            return response;
        }

        // Tìm review và product
        Optional<Review> reviewOptional = reviewRepository.findById(reviewId);
        if (!reviewOptional.isPresent()) {
            response.put("message", "Không tồn tại đánh giá với id: " + reviewId);
            response.put("status", "error");
            return response;
        }
        Review review = reviewOptional.get();

        Optional<Product> productOptional = productRepository.findById(productId);
        if (!productOptional.isPresent()) {
            response.put("message", "Không tồn tại sản phẩm với id: " + productId);
            response.put("status", "error");
            return response;
        }
        Product product = productOptional.get();

        // Kiểm tra nếu đánh giá thuộc sản phẩm không đúng
        if (!review.getProduct().getId().equals(productId)) {
            response.put("message", "Đánh giá không thuộc sản phẩm này");
            response.put("status", "error");
            return response;
        }

        // Xác thực và kiểm tra quyền hạn của người dùng (phải cập nhật cho userId của mình, không cập nhật cho người khác)
        User currentUser = userAuthorization.getUserAuthorization(review.getUser().getId(), response);
        if (currentUser == null) {
            return response;
        }

        // Cập nhật thông tin đánh giá
        review.setContent(content);
        review.setRating(rating);
        review.setReviewTime(LocalDateTime.now());

        // Cập nhật sản phẩm
        product.updateReview(review);

        // Lưu thay đổi
        reviewRepository.save(review);
        productRepository.save(product);

        response.put("message", "Chỉnh sửa đánh giá thành công");
        response.put("status", "success");
        return response;
    }

    @Override
    public ObjectNode deleteReview(int reviewId) {
        // Lấy thông tin người dùng hiện tại từ SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();

        // Tìm review theo ID
        Optional<Review> reviewOptional = reviewRepository.findById(reviewId);
        if (!reviewOptional.isPresent()) {
            response.put("message", "Không tồn tại đánh giá với id: " + reviewId);
            response.put("status", "error");
            return response;
        }
        Review review = reviewOptional.get();

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("Quản trị hệ thống")
                        || role.getAuthority().equals("Quản lý nội dung"));

        // Kiểm tra quyền xóa review
        if (!review.getUser().getEmail().equals(currentUsername) && !isAdmin) {
            response.put("message", "Bạn không có quyền xóa đánh giá này");
            response.put("status", "error");
            return response;
        }

        // Xóa review từ sản phẩm
        Product product = review.getProduct();
        product.removeReview(review);

        // Xóa review và lưu lại sản phẩm
        reviewRepository.delete(review);
        productRepository.save(product);

        response.put("message", "Xóa đánh giá thành công");
        response.put("status", "success");
        return response;
    }

    public Review getReviewByUserIdAndProductId(int userId, int productId) {
        return reviewRepository.findByUser_IdAndProduct_Id(userId, productId);
    }

    public static String formatStringByJson(String json) {
        return json.replaceAll("\"", "");
    }
}