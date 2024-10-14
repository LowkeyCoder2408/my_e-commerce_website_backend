package kimlam_do.my_e_commerce_website.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import kimlam_do.my_e_commerce_website.model.dto.LikedBlogDTO;
import kimlam_do.my_e_commerce_website.service.liked_blog.LikedBlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/liked-blogs")
@RequiredArgsConstructor
public class LikedBlogController {
    private final LikedBlogService likedBlogService;

//    @GetMapping
//    public ResponseEntity<?> getAllLikedBlogs() {
//        try {
//            List<LikedBlogDTO> likedBlogDTOs = likedBlogService.getAllLikedBlogs();
//            return ResponseEntity.ok(likedBlogDTOs);
//        } catch (RuntimeException e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Người dùng không tồn tại hoặc yêu cầu không hợp lệ");
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã xảy ra lỗi trong quá trình xử lý yêu cầu lấy danh sách bài đăng đã thích");
//        }
//    }

    @GetMapping("/find-by-user")
    public ResponseEntity<?> getAllLikedBlogByUserId(@RequestParam(value = "userId") int userId) {
        try {
            List<LikedBlogDTO> likedBlogTOs = likedBlogService.getLikedBlogsByUser(userId);
            return ResponseEntity.ok(likedBlogTOs);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Người dùng không tồn tại hoặc yêu cầu không hợp lệ");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã xảy ra lỗi trong quá trình xử lý yêu cầu lấy danh sách bài đăng đã thích");
        }
    }

    @PostMapping("/like-blog")
    public ResponseEntity<ObjectNode> addLikedBlog(@RequestBody JsonNode jsonNode) {
        try {
            ObjectNode response = likedBlogService.likeBlog(jsonNode);
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

    @DeleteMapping("/unlike-blog")
    public ResponseEntity<ObjectNode> deleteLikedBlog(@RequestBody JsonNode jsonNode) {
        try {
            ObjectNode response = likedBlogService.unLikeBlog(jsonNode);
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