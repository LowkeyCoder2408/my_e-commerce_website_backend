package kimlam_do.my_e_commerce_website.service.category;

import kimlam_do.my_e_commerce_website.model.entity.Category;
import kimlam_do.my_e_commerce_website.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
}