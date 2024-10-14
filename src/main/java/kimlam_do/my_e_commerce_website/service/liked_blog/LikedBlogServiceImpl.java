package kimlam_do.my_e_commerce_website.service.liked_blog;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import kimlam_do.my_e_commerce_website.model.dto.LikedBlogDTO;
import kimlam_do.my_e_commerce_website.model.entity.*;
import kimlam_do.my_e_commerce_website.model.entity.LikedBlog;
import kimlam_do.my_e_commerce_website.repository.BlogRepository;
import kimlam_do.my_e_commerce_website.repository.LikedBlogRepository;
import kimlam_do.my_e_commerce_website.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LikedBlogServiceImpl implements LikedBlogService {
    private final BlogRepository blogRepository;

    private final LikedBlogRepository likedBlogRepository;

    private final UserRepository userRepository;

    @Override
    public List<LikedBlogDTO> getLikedBlogsByUser(int userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();

        User currentUser = userRepository.findByEmail(currentUserEmail);
        if (currentUser == null) {
            throw new RuntimeException("Không tồn tại người dùng với email: " + currentUserEmail);
        }

        Integer currentUserId = currentUser.getId();
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("Quản trị hệ thống") || role.getAuthority().equals("Quản lý nội dung"));

        if (!isAdmin && userId != currentUserId) {
            throw new AccessDeniedException("Bạn không có quyền truy cập dữ liệu này.");
        }

        userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Không tồn tại người dùng với id: " + userId));

        List<LikedBlog> likedBlogs = likedBlogRepository.findByUser_Id(userId);
        return likedBlogs.stream().map(LikedBlogDTO::toDTO).collect(Collectors.toList());
    }

    @Override
    public ObjectNode likeBlog(JsonNode jsonNode) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();

        int blogId;
        try {
            blogId = Integer.parseInt(formatStringByJson(jsonNode.get("blogId").toString()));
        } catch (NumberFormatException e) {
            response.put("message", "ID bài đăng không hợp lệ");
            response.put("status", "error");
            return response;
        }

        // Kiểm tra sự tồn tại của bài đăng
        Optional<Blog> blogOptional = blogRepository.findById(blogId);
        if (!blogOptional.isPresent()) {
            response.put("message", "Không tìm thấy bài đăng với id: " + blogId);
            response.put("status", "error");
            return response;
        }

        // Lấy thông tin người dùng hiện tại
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            response.put("message", "Người dùng chưa đăng nhập");
            response.put("status", "error");
            return response;
        }

        String currentUserEmail = authentication.getName();
        User currentUser = userRepository.findByEmail(currentUserEmail);
        if (currentUser == null) {
            response.put("message", "Không tìm thấy người dùng với email: " + currentUserEmail);
            response.put("status", "error");
            return response;
        }

        // Kiểm tra xem người dùng đã like bài đăng này chưa
        if (likedBlogRepository.existsByBlog_IdAndUser_Id(blogId, currentUser.getId())) {
            response.put("message", "Người dùng đã thích bài đăng này rồi");
            response.put("status", "error");
            return response;
        }

        // Thêm like mới
        LikedBlog likedBlog = new LikedBlog();
        likedBlog.setBlog(blogOptional.get());
        likedBlog.setUser(currentUser);
        likedBlogRepository.save(likedBlog);

        response.put("message", "Đã thích bài đăng");
        response.put("status", "success");
        return response;
    }

    @Override
    public ObjectNode unLikeBlog(JsonNode jsonNode) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();

        int blogId;
        try {
            blogId = Integer.parseInt(formatStringByJson(jsonNode.get("blogId").toString()));
        } catch (NumberFormatException e) {
            response.put("message", "ID bài đăng không hợp lệ");
            response.put("status", "error");
            return response;
        }

        // Kiểm tra sự tồn tại của bài đăng
        Optional<Blog> blogOptional = blogRepository.findById(blogId);
        if (!blogOptional.isPresent()) {
            response.put("message", "Không tìm thấy bài đăng với id: " + blogId);
            response.put("status", "error");
            return response;
        }

        // Lấy thông tin người dùng hiện tại
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            response.put("message", "Người dùng chưa đăng nhập");
            response.put("status", "error");
            return response;
        }

        String currentUserEmail = authentication.getName();
        User currentUser = userRepository.findByEmail(currentUserEmail);
        if (currentUser == null) {
            response.put("message", "Không tìm thấy người dùng với email: " + currentUserEmail);
            response.put("status", "error");
            return response;
        }

        // Kiểm tra xem người dùng đã like bài đăng chưa
        LikedBlog likedBlog = likedBlogRepository.findByBlog_IdAndUser_Id(blogId, currentUser.getId());
        if (likedBlog == null) {
            response.put("message", "Không tìm thấy bài đăng yêu thích với id: " + blogId + " và id người dùng: " + currentUser.getId());
            response.put("status", "error");
            return response;
        }

        // Xóa like
        likedBlogRepository.delete(likedBlog);

        response.put("message", "Đã bỏ thích bài đăng");
        response.put("status", "success");
        return response;
    }

    public static String formatStringByJson(String json) {
        return json.replaceAll("\"", "");
    }
}