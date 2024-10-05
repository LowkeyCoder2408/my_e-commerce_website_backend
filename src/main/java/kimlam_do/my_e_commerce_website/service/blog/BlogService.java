package kimlam_do.my_e_commerce_website.service.blog;

import kimlam_do.my_e_commerce_website.model.entity.Blog;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BlogService {
    Page<Blog> getAllBlogs(int page, int size, String sortBy, String sortDir);

    Page<Blog> findByBlogCategoryName(int page, int size, String sortBy, String sortDir, String blogCategoryName);
}