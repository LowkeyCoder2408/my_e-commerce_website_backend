package kimlam_do.my_e_commerce_website.service.jwt;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Map;

public interface JwtService {
    // Tạo JWT dựa trên email
    String generateToken(String email);

    // Tạo JWT với các claim đã chọn
    String createToken(Map<String, Object> claims, String email);

    // Kiểm tra tời gian hết hạn từ JWT
    String extractEmail(String token);

    // Kiểm tra tính hợp lệ
    Boolean validateToken(String token, UserDetails userDetails);

    List<String> extractRoles(String token);
}