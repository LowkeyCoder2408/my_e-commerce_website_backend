package kimlam_do.my_e_commerce_website.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import kimlam_do.my_e_commerce_website.model.dto.LikedBlogCommentDTO;
import kimlam_do.my_e_commerce_website.service.liked_blog_comment.LikedBlogCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/liked-blog-comments")
@RequiredArgsConstructor
public class LikedBlogCommentController {
    private final LikedBlogCommentService likedBlogCommentService;

    @GetMapping("/find-by-user")
    public ResponseEntity<?> getAllLikedBlogCommentByUserId(@RequestParam(value = "userId") int userId) {
        try {
            List<LikedBlogCommentDTO> likedBlogCommentDTOs = likedBlogCommentService.getLikedBlogCommentsByUser(userId);
            return ResponseEntity.ok(likedBlogCommentDTOs);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Người dùng không tồn tại hoặc yêu cầu không hợp lệ");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã xảy ra lỗi trong quá trình xử lý yêu cầu lấy danh sách bình luận của bài đăng đã thích");
        }
    }

    @PostMapping("/like-comment")
    public ResponseEntity<ObjectNode> addLikedBlogComment(@RequestBody JsonNode jsonNode) {
        try {
            ObjectNode response = likedBlogCommentService.likeBlogComment(jsonNode);
            String status = response.get("status").asText();
            HttpStatus httpStatus = "error".equals(status) ? HttpStatus.BAD_REQUEST : HttpStatus.OK;
            return new ResponseEntity<>(response, httpStatus);
        } catch (Exception e) {
            e.printStackTrace();
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode errorResponse = mapper.createObjectNode();
            errorResponse.put("message", "Đã xảy ra lỗi khi thực hiện thao tác này");
            errorResponse.put("status", "error");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/unlike-comment")
    public ResponseEntity<ObjectNode> deleteLikedBlog(@RequestBody JsonNode jsonNode) {
        try {
            ObjectNode response = likedBlogCommentService.unLikeComment(jsonNode);
            String status = response.get("status").asText();
            HttpStatus httpStatus = "error".equals(status) ? HttpStatus.BAD_REQUEST : HttpStatus.OK;
            return new ResponseEntity<>(response, httpStatus);
        } catch (Exception e) {
            e.printStackTrace();
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode errorResponse = mapper.createObjectNode();
            errorResponse.put("message", "Đã xảy ra lỗi khi thực hiện thao tác này");
            errorResponse.put("status", "error");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}