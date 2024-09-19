package kimlam_do.my_e_commerce_website.security;

import com.fasterxml.jackson.databind.node.ObjectNode;
import kimlam_do.my_e_commerce_website.model.entity.User;
import kimlam_do.my_e_commerce_website.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserAuthorization {
    private final UserRepository userRepository;

    public User getUserAuthorization(int userId, ObjectNode response) {
        // Lấy thông tin người dùng hiện tại
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();
        User currentUser = userRepository.findByEmail(currentUserEmail);
        if (currentUser == null) {
            response.put("message", "Không tồn tại người dùng (đang gọi API)");
            response.put("status", "error");
            return null;
        }
        // Kiểm tra quyền hạn
        if (!currentUser.getId().equals(userId)) {
            response.put("message", "Bạn không có quyền thực hiện hành động này cho người dùng khác");
            response.put("status", "error");
            return null;
        }
        return currentUser;
    }
}