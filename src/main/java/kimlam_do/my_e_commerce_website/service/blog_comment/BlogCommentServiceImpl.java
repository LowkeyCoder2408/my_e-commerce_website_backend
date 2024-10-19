package kimlam_do.my_e_commerce_website.service.blog_comment;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import kimlam_do.my_e_commerce_website.model.entity.*;
import kimlam_do.my_e_commerce_website.repository.BlogCommentRepository;
import kimlam_do.my_e_commerce_website.repository.BlogRepository;
import kimlam_do.my_e_commerce_website.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BlogCommentServiceImpl implements BlogCommentService {
    private final BlogCommentRepository blogCommentRepository;

    private final BlogRepository blogRepository;

    private final UserRepository userRepository;

    @Override
    public Optional<BlogComment> getBlogCommentById(int id) {
        return blogCommentRepository.findById(id);
    }

    @Override
    public ObjectNode addAComment(JsonNode jsonData) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();

        int blogId = Integer.parseInt(formatStringByJson(String.valueOf(jsonData.get("blogId"))));
        String content = formatStringByJson(jsonData.has("content") ? jsonData.get("content").asText() : "");
        String option = formatStringByJson(jsonData.has("option") ? jsonData.get("option").asText() : "");

        // Kiểm tra sự tồn tại của blog
        Optional<Blog> blogOptional = blogRepository.findById(blogId);
        if (!blogOptional.isPresent()) {
            response.put("message", "Không tồn tại bài đăng với id: " + blogId);
            response.put("status", "error");
            return response;
        }
        Blog blog = blogOptional.get();

        // Kiểm tra content hợp lệ
        if (content == null || content.trim().isEmpty() || content.length() > 100) {
            response.put("message", "Nội dung bình luận không được để trống hoặc vượt quá 100 ký tự");
            response.put("status", "error");
            return response;
        }

        // Kiểm tra option hợp lệ
        if (option == null || (!option.equals("add") && !option.equals("reply") && !option.equals("update"))) {
            response.put("message", "Lựa chọn đối với dữ liệu không hợp lệ");
            response.put("status", "error");
            return response;
        }

        // Lấy thông tin người dùng
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();
        User user = userRepository.findByEmail(currentUserEmail);

        if (user == null) {
            response.put("message", "Không tồn tại người dùng (đang gọi API)");
            response.put("status", "error");
            return response;
        }

        BlogComment blogComment = new BlogComment();
        blogComment.setBlog(blog);
        blogComment.setContent(content);
        blogComment.setUser(user);

        if (option.equals("reply")) {
            int parentBlogCommentId = Integer.parseInt(formatStringByJson(String.valueOf(jsonData.get("parentBlogCommentId"))));
            Optional<BlogComment> parentBlogCommentOptional = blogCommentRepository.findById(parentBlogCommentId);
            if (!parentBlogCommentOptional.isPresent()) {
                response.put("message", "Không tồn tại bình luận với id: " + parentBlogCommentId);
                response.put("status", "error");
                return response;
            }
            blogComment.setParentComment(parentBlogCommentOptional.get());
        }

        if (option.equals("update")) {
            response.put("message", "Vui lòng gọi phương thức cập nhật bình luận");
            response.put("status", "error");
            return response;
        }

        blogCommentRepository.save(blogComment);

        response.put("message", option.equals("add") ? "Thêm bình luận thành công" : "Thêm phản hồi thành công");
        response.put("status", "success");

        return response;
    }

    @Override
    public ObjectNode updateAComment(JsonNode jsonData) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();

        int blogCommentId = Integer.parseInt(formatStringByJson(String.valueOf(jsonData.get("blogCommentId"))));
        String content = formatStringByJson(jsonData.has("content") ? jsonData.get("content").asText() : "");
        String option = formatStringByJson(jsonData.has("option") ? jsonData.get("option").asText() : "");

        if (!option.equals("update")) {
            response.put("message", "Lựa chọn đối với bình luận không hợp lệ");
            response.put("status", "error");
            return response;
        }
        Optional<BlogComment> blogCommentOptional = blogCommentRepository.findById(blogCommentId);
        if (!blogCommentOptional.isPresent()) {
            response.put("message", "Không tồn tại bình luận với id: " + blogCommentId);
            response.put("status", "error");
            return response;
        }
        BlogComment blogComment = blogCommentOptional.get();

        // Kiểm tra content hợp lệ
        if (content == null || content.trim().isEmpty() || content.length() > 100) {
            response.put("message", "Nội dung bình luận không được để trống hoặc vượt quá 100 ký tự");
            response.put("status", "error");
            return response;
        }

        if (blogComment.getContent().equals(content)) {
            response.put("message", "Nội dung bình luận chưa thay đổi");
            response.put("status", "error");
            return response;
        }

        // Lấy thông tin người dùng hiện tại từ SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        // Kiểm tra quyền sửa comment
        if (!currentUsername.equals(blogComment.getUser().getEmail())) {
            response.put("message", "Bạn không có quyền sửa bình luận này");
            response.put("status", "error");
            return response;
        }

        blogComment.setContent(content);
        blogCommentRepository.save(blogComment);

        response.put("message", "Chỉnh sửa bình luận thành công");
        response.put("status", "success");
        return response;
    }

    @Override
    public ObjectNode deleteComment(int blogCommentId) {
        // Lấy thông tin người dùng hiện tại từ SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();

        // Tìm comment theo ID
        Optional<BlogComment> blogCommentOptional = blogCommentRepository.findById(blogCommentId);
        if (!blogCommentOptional.isPresent()) {
            response.put("message", "Không tồn tại bình luận với id: " + blogCommentId);
            response.put("status", "error");
            return response;
        }
        BlogComment blogComment = blogCommentOptional.get();

        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("Quản trị hệ thống") || role.getAuthority().equals("Quản lý nội dung"));

        // Kiểm tra quyền xóa comment
        if (!blogComment.getUser().getEmail().equals(currentUsername) && !isAdmin) {
            response.put("message", "Bạn không có quyền xóa bình luận này");
            response.put("status", "error");
            return response;
        }

        // Xóa comment
        blogCommentRepository.delete(blogComment);

        response.put("message", "Xóa bình luận thành công");
        response.put("status", "success");
        return response;
    }

    public static String formatStringByJson(String json) {
        return json.replaceAll("\"", "");
    }
}