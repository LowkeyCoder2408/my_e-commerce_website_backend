package kimlam_do.my_e_commerce_website.service.liked_blog_comment;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import kimlam_do.my_e_commerce_website.model.dto.LikedBlogCommentDTO;
import kimlam_do.my_e_commerce_website.model.entity.*;
import kimlam_do.my_e_commerce_website.repository.BlogCommentRepository;
import kimlam_do.my_e_commerce_website.repository.BlogRepository;
import kimlam_do.my_e_commerce_website.repository.LikedBlogCommentRepository;
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
public class LikedBlogCommentServiceImpl implements LikedBlogCommentService {
    private final BlogCommentRepository blogCommentRepository;

    private final LikedBlogCommentRepository likedBlogCommentRepository;

    private final UserRepository userRepository;

    @Override
    public List<LikedBlogCommentDTO> getLikedBlogCommentsByUser(int userId) {
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

        List<LikedBlogComment> likedBlogComments = likedBlogCommentRepository.findByUser_Id(userId);
        return likedBlogComments.stream().map(LikedBlogCommentDTO::toDTO).collect(Collectors.toList());
    }

    @Override
    public ObjectNode likeBlogComment(JsonNode jsonNode) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();

        int blogCommentId;
        try {
            blogCommentId = Integer.parseInt(formatStringByJson(jsonNode.get("blogCommentId").toString()));
        } catch (NumberFormatException e) {
            response.put("message", "ID bình luận bài đăng không hợp lệ");
            response.put("status", "error");
            return response;
        }

        Optional<BlogComment> blogCommentOptional = blogCommentRepository.findById(blogCommentId);
        if (!blogCommentOptional.isPresent()) {
            response.put("message", "Không tìm thấy bình luận bài đăng với id: " + blogCommentId);
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
        if (likedBlogCommentRepository.existsByBlogComment_IdAndUser_Id(blogCommentId, currentUser.getId())) {
            response.put("message", "Người dùng đã thích bài đăng này rồi");
            response.put("status", "error");
            return response;
        }

        // Thêm like mới
        LikedBlogComment likedBlogComment = new LikedBlogComment();
        likedBlogComment.setBlogComment(blogCommentOptional.get());
        likedBlogComment.setUser(currentUser);
        likedBlogCommentRepository.save(likedBlogComment);

        response.put("message", "Đã thích bài đăng");
        response.put("status", "success");
        return response;
    }

    @Override
    public ObjectNode unLikeComment(JsonNode jsonNode) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();

        int blogCommentId;
        try {
            blogCommentId = Integer.parseInt(formatStringByJson(jsonNode.get("blogCommentId").toString()));
        } catch (NumberFormatException e) {
            response.put("message", "ID bình luận không hợp lệ");
            response.put("status", "error");
            return response;
        }

        // Kiểm tra sự tồn tại của bình luận
        Optional<BlogComment> blogCommentOptional = blogCommentRepository.findById(blogCommentId);
        if (!blogCommentOptional.isPresent()) {
            response.put("message", "Không tìm thấy bình luận với id: " + blogCommentId);
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

        LikedBlogComment likedBlogComment = likedBlogCommentRepository.findByBlogComment_IdAndUser_Id(blogCommentId, currentUser.getId());
        if (likedBlogComment == null) {
            response.put("message", "Không tìm thấy bình luận yêu thích với id: " + blogCommentId + " và id người dùng: " + currentUser.getId());
            response.put("status", "error");
            return response;
        }

        // Xóa like
        likedBlogCommentRepository.delete(likedBlogComment);

        response.put("message", "Đã bỏ thích bình luận");
        response.put("status", "success");
        return response;
    }

    public static String formatStringByJson(String json) {
        return json.replaceAll("\"", "");
    }
}