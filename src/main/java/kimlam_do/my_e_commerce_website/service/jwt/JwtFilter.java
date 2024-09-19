package kimlam_do.my_e_commerce_website.service.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kimlam_do.my_e_commerce_website.service.user.UserSecurityService;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@NoArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserSecurityService userDetailService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Lấy chuỗi token từ tiêu đề "Authorization"
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String email = null;
        List<String> roles = null;

        // Kiểm tra xem chuỗi token có tồn tại và có bắt đầu bằng "Bearer " không
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // Loại bỏ phần tiền tố "Bearer " để lấy token thực sự
            token = authHeader.substring(7);
            // Trích xuất email từ token
            email = jwtService.extractEmail(token);
            roles = jwtService.extractRoles(token);
        }

        // Kiểm tra xem email có tồn tại và người dùng chưa được xác thực
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Tải thông tin người dùng từ cơ sở dữ liệu bằng email
            UserDetails userDetails;
            userDetails = userDetailService.loadUserByUsername(email);

            // Kiểm tra tính hợp lệ của token và người dùng
            if (jwtService.validateToken(token, userDetails)) {
                // Tạo đối tượng UsernamePasswordAuthenticationToken mà không có quyền hạn
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                // Thiết lập chi tiết xác thực từ WebAuthenticationDetails
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        // Tiếp tục chuỗi filter
        filterChain.doFilter(request, response);
    }
}