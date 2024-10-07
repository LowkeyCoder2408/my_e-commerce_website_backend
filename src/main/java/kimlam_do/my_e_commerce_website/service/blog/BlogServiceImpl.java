package kimlam_do.my_e_commerce_website.service.blog;

import kimlam_do.my_e_commerce_website.model.entity.Blog;
import kimlam_do.my_e_commerce_website.repository.BlogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class BlogServiceImpl implements BlogService {
    private final BlogRepository blogRepository;

    @Override
    public Page<Blog> getAllBlogs(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return blogRepository.findAll(pageable);
    }

    @Override
    public Optional<Blog> getBlogById(int id) {
        return blogRepository.findById(id);
    }

    @Override
    public Page<Blog> findByNameContaining(int page, int size, String sortBy, String sortDir, String keyword) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return blogRepository.findByTitleContaining(keyword, pageable);
    }

    @Override
    public Page<Blog> findByBlogCategoryName(int page, int size, String sortBy, String sortDir, String blogCategoryName) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return blogRepository.findByBlogCategory_Name(blogCategoryName, pageable);
    }

    @Override
    public Page<Blog> findByNameContainingAndBlogCategoryName(int page, int size, String sortBy, String sortDir, String blogCategoryName, String keyword) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return blogRepository.findByTitleContainingAndBlogCategory_Name(keyword, blogCategoryName, pageable);
    }

    @Override
    public Page<Blog> findByUser(int userId, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return blogRepository.findByAuthor_Id(userId, pageable);
    }
}