package kimlam_do.my_e_commerce_website.service.blog_category;

import kimlam_do.my_e_commerce_website.model.entity.BlogCategory;

import java.util.List;
import java.util.Optional;

public interface BlogCategoryService {
    List<BlogCategory> getAllBlogCategories();

    Optional<BlogCategory> findByName(String blogCategoryName);
}