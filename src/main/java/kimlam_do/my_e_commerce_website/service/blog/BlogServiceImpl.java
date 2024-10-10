package kimlam_do.my_e_commerce_website.service.blog;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import kimlam_do.my_e_commerce_website.model.entity.Blog;
import kimlam_do.my_e_commerce_website.model.entity.BlogCategory;
import kimlam_do.my_e_commerce_website.model.entity.User;
import kimlam_do.my_e_commerce_website.repository.BlogRepository;
import kimlam_do.my_e_commerce_website.repository.UserRepository;
import kimlam_do.my_e_commerce_website.service.blog_category.BlogCategoryService;
import kimlam_do.my_e_commerce_website.service.cloudinary.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class BlogServiceImpl implements BlogService {
    private final BlogRepository blogRepository;
    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;
    private final BlogCategoryService blogCategoryService;

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

    @Override
    public ObjectNode addBlog(String title, String content, String blogCategoryName, MultipartFile image) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();
        User author = userRepository.findByEmail(currentUserEmail);

        if (author == null) {
            response.put("message", "Không tồn tại người dùng (đang gọi API)");
            response.put("status", "error");
            return response;
        }
        if (title == null || title.trim().isEmpty()) {
            response.put("message", "Tiêu đề bài đăng không được để trống");
            response.put("status", "error");
            return response;
        }
        if (blogCategoryName == null || blogCategoryName.trim().isEmpty()) {
            response.put("message", "Danh mục bài đăng không được để trống");
            response.put("status", "error");
            return response;
        }

        Optional<BlogCategory> blogCategoryOptional = blogCategoryService.findByName(blogCategoryName);
        if (!blogCategoryOptional.isPresent()) {
            response.put("message", "Danh mục bài đăng không tồn tại");
            response.put("status", "error");
            return response;
        }
        BlogCategory blogCategory = blogCategoryOptional.get();

        if (content == null || Jsoup.parse(content).text().trim().isEmpty()) {
            response.put("message", "Nội dung bài đăng không được để trống");
            response.put("status", "error");
            return response;
        }

        // Tạo mới một blog
        Blog blog = new Blog();
        blog.setTitle(title);
        blog.setContent(content);
        blog.setBlogCategory(blogCategory);
        blog.setAuthor(author);
        blog.setCreatedAt(LocalDateTime.now());
        blog.setUpdatedAt(LocalDateTime.now());
        blog.setEnabled(true);
        blog.setLikesCount(0);

        // Xử lý upload hình ảnh nếu có
        if (image != null) {
            if (image.isEmpty()) {
                response.put("message", "Không có ảnh được tải lên");
                response.put("status", "error");
                return response;
            }

            // Kiểm tra kích thước của file ảnh (giới hạn 10MB)
            long maxSize = 10 * 1024 * 1024; // 10MB
            if (image.getSize() > maxSize) {
                response.put("message", "Kích thước tệp ảnh không được vượt quá 10MB");
                response.put("status", "error");
                return response;
            }

            // Kiểm tra định dạng file ảnh (nếu cần)
            String contentType = image.getContentType();
            if (!contentType.startsWith("image/")) {
                response.put("message", "Chỉ chấp nhận tệp hình ảnh");
                response.put("status", "error");
                return response;
            }

            try {
                Map<String, String> uploadResult = cloudinaryService.uploadImage(image);
                blog.setFeaturedImage(uploadResult.get("imageUrl"));
                blog.setFeaturedImagePublicId(uploadResult.get("publicId"));
            } catch (Exception e) {
                response.put("message", "Lỗi khi thêm ảnh");
                response.put("status", "error");
                return response;
            }
        } else {
            response.put("message", "Không có ảnh được tải lên");
            response.put("status", "warning");
        }

        // Lưu blog vào database
        blogRepository.save(blog);

        // Trả về phản hồi thành công
        response.put("message", "Thêm bài đăng mới thành công");
        response.put("status", "success");
        return response;
    }
}