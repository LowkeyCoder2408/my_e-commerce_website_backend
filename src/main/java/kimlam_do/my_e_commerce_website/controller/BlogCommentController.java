package kimlam_do.my_e_commerce_website.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import kimlam_do.my_e_commerce_website.model.dto.BlogCommentDTO;
import kimlam_do.my_e_commerce_website.model.dto.BlogDTO;
import kimlam_do.my_e_commerce_website.model.entity.Blog;
import kimlam_do.my_e_commerce_website.model.entity.BlogComment;
import kimlam_do.my_e_commerce_website.service.blog_comment.BlogCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/blog-comments")
@RequiredArgsConstructor
public class BlogCommentController {
    private final BlogCommentService blogCommentService;

    @GetMapping("{id}")
    public ResponseEntity<?> getBlogCommentById(@PathVariable("id") int id) {
        try {
            Optional<BlogComment> optionalBlogComment = blogCommentService.getBlogCommentById(id);
            if (!optionalBlogComment.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Bình luận không tồn tại");
            }
            BlogCommentDTO blogCommentDTO = BlogCommentDTO.toDTO(optionalBlogComment.get());
            return ResponseEntity.ok(blogCommentDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã xảy ra lỗi khi lấy dữ liệu bình luận theo id.");
        }
    }

    @PostMapping("/add-comment")
    public ResponseEntity<ObjectNode> addAComment(@RequestBody JsonNode jsonData) {
        try {
            ObjectNode response = blogCommentService.addAComment(jsonData);
            String status = response.get("status").asText();
            HttpStatus httpStatus = "error".equals(status) ? HttpStatus.BAD_REQUEST : HttpStatus.OK;
            return new ResponseEntity<>(response, httpStatus);
        } catch (Exception e) {
            e.printStackTrace();
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode errorResponse = mapper.createObjectNode();
            errorResponse.put("message", "Đã xảy ra lỗi khi thêm bình luận");
            errorResponse.put("status", "error");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update-comment")
    public ResponseEntity<ObjectNode> updateAComment(@RequestBody JsonNode jsonData) {
        try {
            ObjectNode response = blogCommentService.updateAComment(jsonData);
            String status = response.get("status").asText();
            HttpStatus httpStatus = "error".equals(status) ? HttpStatus.BAD_REQUEST : HttpStatus.OK;
            return new ResponseEntity<>(response, httpStatus);
        } catch (Exception e) {
            e.printStackTrace();
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode errorResponse = mapper.createObjectNode();
            errorResponse.put("message", "Đã xảy ra lỗi khi chỉnh sửa bình luận");
            errorResponse.put("status", "error");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete-comment/{blogCommentId}")
    public ResponseEntity<ObjectNode> deleteComment(@PathVariable int blogCommentId) {
        try {
            ObjectNode response = blogCommentService.deleteComment(blogCommentId);
            String status = response.get("status").asText();
            HttpStatus httpStatus = "error".equals(status) ? HttpStatus.BAD_REQUEST : HttpStatus.OK;
            return new ResponseEntity<>(response, httpStatus);
        } catch (Exception e) {
            e.printStackTrace();
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode errorResponse = mapper.createObjectNode();
            errorResponse.put("message", "Đã xảy ra lỗi khi xóa bình luận");
            errorResponse.put("status", "error");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}