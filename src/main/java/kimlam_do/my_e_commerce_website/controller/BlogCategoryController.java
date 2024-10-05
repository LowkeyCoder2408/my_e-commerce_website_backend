package kimlam_do.my_e_commerce_website.controller;

import kimlam_do.my_e_commerce_website.model.dto.BlogCategoryDTO;
import kimlam_do.my_e_commerce_website.model.entity.BlogCategory;
import kimlam_do.my_e_commerce_website.service.blog_category.BlogCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/blog-categories")
@RequiredArgsConstructor
public class BlogCategoryController {
    private final BlogCategoryService blogCategoryService;

    @GetMapping
    public ResponseEntity<?> getAllBlogCategories() {
        try {
            List<BlogCategory> blogCategories = blogCategoryService.getAllBlogCategories();
            List<BlogCategoryDTO> blogCategoryDTOs = blogCategories.stream().map(BlogCategoryDTO::toDTO).collect(Collectors.toList());
            return new ResponseEntity<>(blogCategoryDTOs, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Đã xảy ra lỗi khi lấy dữ liệu danh mục bài đăng", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}