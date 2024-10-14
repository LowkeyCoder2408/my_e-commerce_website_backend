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

import java.io.IOException;
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
//        blog.setLikesCount(0);

        // Xử lý upload hình ảnh nếu có
        if (image != null) {
            if (image.isEmpty()) {
                response.put("message", "Không có ảnh được tải lên");
                response.put("status", "error");
                return response;
            }

            // Kiểm tra kích thước của file ảnh (giới hạn 10MB)
            long maxSize = 10 * 1024 * 1024;
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
            response.put("status", "error");
            return response;
        }
        blogRepository.save(blog);

        response.put("message", "Thêm bài đăng mới thành công");
        response.put("status", "success");
        return response;
    }

    @Override
    public ObjectNode updateBlog(int blogId, String title, String content, String blogCategoryName, MultipartFile image) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();

        // Lấy thông tin người dùng hiện tại
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();
        User author = userRepository.findByEmail(currentUserEmail);

        if (author == null) {
            response.put("message", "Không tồn tại người dùng (đang gọi API)");
            response.put("status", "error");
            return response;
        }

        // Tìm blog theo ID
        Optional<Blog> blogOptional = blogRepository.findById(blogId);
        if (!blogOptional.isPresent()) {
            response.put("message", "Bài đăng không tồn tại");
            response.put("status", "error");
            return response;
        }
        Blog blog = blogOptional.get();

        // Kiểm tra quyền sở hữu: Chỉ tác giả của bài đăng mới có quyền cập nhật
        if (!blog.getAuthor().getId().equals(author.getId())) {
            response.put("message", "Bạn không có quyền cập nhật bài đăng này");
            response.put("status", "error");
            return response;
        }

        // Kiểm tra tiêu đề không được để trống
        if (title == null || title.trim().isEmpty()) {
            response.put("message", "Tiêu đề bài đăng không được để trống");
            response.put("status", "error");
            return response;
        }

        // Kiểm tra danh mục bài đăng
        if (blogCategoryName != null && !blogCategoryName.trim().isEmpty()) {
            Optional<BlogCategory> blogCategoryOptional = blogCategoryService.findByName(blogCategoryName);
            if (!blogCategoryOptional.isPresent()) {
                response.put("message", "Danh mục bài đăng không tồn tại");
                response.put("status", "error");
                return response;
            }
            blog.setBlogCategory(blogCategoryOptional.get());
        }

        // Kiểm tra nội dung không được để trống
        if (content == null || Jsoup.parse(content).text().trim().isEmpty()) {
            response.put("message", "Nội dung bài đăng không được để trống");
            response.put("status", "error");
            return response;
        }

        // Kiểm tra nếu có thay đổi so với bài đăng hiện tại
        boolean isTitleChanged = !blog.getTitle().equals(title);
        boolean isContentChanged = !blog.getContent().equals(content);
        boolean isCategoryChanged = blogCategoryName != null && blog.getBlogCategory().getName() != blogCategoryName;
        boolean isImageChanged = image != null && !image.isEmpty();

        if (!isTitleChanged && !isContentChanged && !isCategoryChanged && !isImageChanged) {
            response.put("message", "Không có thay đổi nào được thực hiện");
            response.put("status", "info");
            return response;
        }

        // Cập nhật thông tin bài viết nếu có thay đổi
        if (isTitleChanged) {
            blog.setTitle(title);
        }
        if (isContentChanged) {
            blog.setContent(content);
        }
        blog.setUpdatedAt(LocalDateTime.now());

        // Xử lý upload hình ảnh nếu có ảnh được tải lên
        if (isImageChanged) {
            long maxSize = 10 * 1024 * 1024;
            if (image.getSize() > maxSize) {
                response.put("message", "Kích thước tệp ảnh không được vượt quá 10MB");
                response.put("status", "error");
                return response;
            }

            String contentType = image.getContentType();
            if (!contentType.startsWith("image/")) {
                response.put("message", "Chỉ chấp nhận tệp hình ảnh");
                response.put("status", "error");
                return response;
            }

            try {
                if (blog.getFeaturedImagePublicId() != null && !blog.getFeaturedImagePublicId().isEmpty()) {
                    cloudinaryService.deleteImage(blog.getFeaturedImagePublicId());
                }

                Map<String, String> uploadResult = cloudinaryService.uploadImage(image);
                blog.setFeaturedImage(uploadResult.get("imageUrl"));
                blog.setFeaturedImagePublicId(uploadResult.get("publicId"));
            } catch (Exception e) {
                response.put("message", "Lỗi khi cập nhật ảnh");
                response.put("status", "error");
                return response;
            }
        }
        blogRepository.save(blog);

        response.put("message", "Cập nhật bài đăng thành công");
        response.put("status", "success");
        return response;
    }

    @Override
    public ObjectNode deleteBlog(int blogId) throws IOException {
        // Lấy thông tin người dùng hiện tại từ SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();

        // Tìm blog theo ID
        Optional<Blog> blogOptional = blogRepository.findById(blogId);
        if (!blogOptional.isPresent()) {
            response.put("message", "Không tồn tại bài đăng với id: " + blogId);
            response.put("status", "error");
            return response;
        }
        Blog blog = blogOptional.get();

        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("Quản trị hệ thống") || role.getAuthority().equals("Quản lý nội dung"));

        // Kiểm tra quyền xóa blog
        if (!blog.getAuthor().getEmail().equals(currentUsername) && !isAdmin) {
            response.put("message", "Bạn không có quyền xóa bài đăng này");
            response.put("status", "error");
            return response;
        }

        // LOGIC XÓA BÀI ĐĂNG
        if (blog.getFeaturedImagePublicId() != null && !blog.getFeaturedImagePublicId().isEmpty()) {
            cloudinaryService.deleteImage(blog.getFeaturedImagePublicId());
        }
        blogRepository.delete(blog);

        response.put("message", "Xóa bài đăng thành công");
        response.put("status", "success");
        return response;
    }
}