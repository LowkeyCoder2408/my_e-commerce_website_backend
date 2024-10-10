package kimlam_do.my_e_commerce_website.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import kimlam_do.my_e_commerce_website.model.dto.PaginatedResponse;
import kimlam_do.my_e_commerce_website.model.dto.BlogDTO;
import kimlam_do.my_e_commerce_website.model.entity.Blog;
import kimlam_do.my_e_commerce_website.service.blog.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/blogs")
@RequiredArgsConstructor
public class BlogController {
    private final BlogService blogService;

    @GetMapping
    public ResponseEntity<?> getAllBlogs(@RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "size", defaultValue = "6") int size, @RequestParam(value = "sortBy", defaultValue = "id") String sortBy, @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir) {
        try {
            Page<Blog> blogPage = blogService.getAllBlogs(page, size, sortBy, sortDir);
            List<BlogDTO> blogDTOs = blogPage.getContent().stream().map(BlogDTO::toDTO).collect(Collectors.toList());
            PaginatedResponse<BlogDTO> response = new PaginatedResponse<>(blogDTOs, blogPage.getTotalPages(), blogPage.getTotalElements());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã xảy ra lỗi khi lấy dữ liệu bài đăng.");
        }
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getBlogById(@PathVariable("id") int id) {
        try {
            Optional<Blog> optionalBlog = blogService.getBlogById(id);
            if (!optionalBlog.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Bài đăng không tồn tại.");
            }
            BlogDTO blogDTO = BlogDTO.toDTO(optionalBlog.get());
            return ResponseEntity.ok(blogDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã xảy ra lỗi khi lấy dữ liệu bài đăng theo id.");
        }
    }

    @GetMapping("/find-by-name-containing")
    public ResponseEntity<?> findByNameContaining(@RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "size", defaultValue = "20") int size, @RequestParam(value = "sortBy", defaultValue = "id") String sortBy, @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir, @RequestParam(value = "keyword") String keyword) {
        try {
            Page<Blog> blogPage = blogService.findByNameContaining(page, size, sortBy, sortDir, keyword);
            List<BlogDTO> blogDTOs = blogPage.stream().map(BlogDTO::toDTO).collect(Collectors.toList());
            PaginatedResponse<BlogDTO> response = new PaginatedResponse<>(blogDTOs, blogPage.getTotalPages(), blogPage.getTotalElements());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Đã xảy ra lỗi khi lấy dữ liệu bài đăng theo tên.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/find-by-blog-category-name")
    public ResponseEntity<?> findByBlogCategoryName(@RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "size", defaultValue = "20") int size, @RequestParam(value = "sortBy", defaultValue = "id") String sortBy, @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir, @RequestParam(value = "blogCategoryName") String blogCategoryName) {
        try {
            Page<Blog> blogPage = blogService.findByBlogCategoryName(page, size, sortBy, sortDir, blogCategoryName);
            List<BlogDTO> blogDTOs = blogPage.stream().map(BlogDTO::toDTO).collect(Collectors.toList());
            PaginatedResponse<BlogDTO> response = new PaginatedResponse<>(blogDTOs, blogPage.getTotalPages(), blogPage.getTotalElements());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Đã xảy ra lỗi khi lấy dữ liệu bài đăng theo danh mục.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/find-by-name-containing-and-blog-category-name")
    public ResponseEntity<?> findByNameContainingAndBlogCategoryName(@RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "size", defaultValue = "20") int size, @RequestParam(value = "sortBy", defaultValue = "id") String sortBy, @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir, @RequestParam(value = "blogCategoryName") String blogCategoryName, @RequestParam(value = "keyword") String keyword) {
        try {
            Page<Blog> blogPage = blogService.findByNameContainingAndBlogCategoryName(page, size, sortBy, sortDir, blogCategoryName, keyword);
            List<BlogDTO> blogDTOs = blogPage.stream().map(BlogDTO::toDTO).collect(Collectors.toList());
            PaginatedResponse<BlogDTO> response = new PaginatedResponse<>(blogDTOs, blogPage.getTotalPages(), blogPage.getTotalElements());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Đã xảy ra lỗi khi lấy dữ liệu bài đăng theo tên và danh mục.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/find-by-user")
    public ResponseEntity<?> findByUser(@RequestParam(value = "userId") int userId, @RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "size", defaultValue = "6") int size, @RequestParam(value = "sortBy", defaultValue = "id") String sortBy, @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir) {
        try {
            Page<Blog> blogPage = blogService.findByUser(userId, page, size, sortBy, sortDir);
            List<BlogDTO> blogDTOs = blogPage.getContent().stream().map(BlogDTO::toDTO).collect(Collectors.toList());
            PaginatedResponse<BlogDTO> response = new PaginatedResponse<>(blogDTOs, blogPage.getTotalPages(), blogPage.getTotalElements());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã xảy ra lỗi khi lấy dữ liệu bài đăng theo mã người dùng.");
        }
    }

    @PostMapping("/add-blog")
    public ResponseEntity<ObjectNode> addBlog(@RequestParam("title") String title, @RequestParam("content") String content, @RequestParam(value = "blogCategoryName", required = false) String blogCategoryName, @RequestParam(value = "image", required = false) MultipartFile image) {
        try {
            ObjectNode response = blogService.addBlog(title, content, blogCategoryName, image);
            String status = response.get("status").asText();
            HttpStatus httpStatus = "error".equals(status) ? HttpStatus.BAD_REQUEST : HttpStatus.OK;
            return new ResponseEntity<>(response, httpStatus);
        } catch (Exception e) {
            e.printStackTrace();
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode errorResponse = mapper.createObjectNode();
            errorResponse.put("message", "Đã xảy ra lỗi khi thêm bài đăng mới");
            errorResponse.put("status", "error");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}