package kimlam_do.my_e_commerce_website.controller;

import kimlam_do.my_e_commerce_website.model.dto.PaginatedResponse;
import kimlam_do.my_e_commerce_website.model.dto.BlogDTO;
import kimlam_do.my_e_commerce_website.model.entity.Blog;
import kimlam_do.my_e_commerce_website.service.blog.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
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
}