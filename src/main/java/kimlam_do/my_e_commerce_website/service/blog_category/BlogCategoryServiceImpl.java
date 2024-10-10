package kimlam_do.my_e_commerce_website.service.blog_category;

import kimlam_do.my_e_commerce_website.model.entity.BlogCategory;
import kimlam_do.my_e_commerce_website.repository.BlogCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BlogCategoryServiceImpl implements BlogCategoryService {
    private final BlogCategoryRepository blogCategoryRepository;

    @Override
    public List<BlogCategory> getAllBlogCategories() {
        return blogCategoryRepository.findAll();
    }

    @Override
    public Optional<BlogCategory> findByName(String blogCategoryName) {
        return blogCategoryRepository.findByName(blogCategoryName);
    }
}