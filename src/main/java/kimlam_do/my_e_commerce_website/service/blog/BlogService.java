package kimlam_do.my_e_commerce_website.service.blog;

import kimlam_do.my_e_commerce_website.model.entity.Blog;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface BlogService {
    Page<Blog> getAllBlogs(int page, int size, String sortBy, String sortDir);

    Optional<Blog> getBlogById(int id);

    Page<Blog> findByNameContaining(int page, int size, String sortBy, String sortDir, String keyword);

    Page<Blog> findByBlogCategoryName(int page, int size, String sortBy, String sortDir, String blogCategoryName);

    Page<Blog> findByNameContainingAndBlogCategoryName(int page, int size, String sortBy, String sortDir, String blogCategoryName, String keyword);

    Page<Blog> findByUser(int userId, int page, int size, String sortBy, String sortDir);
}