package kimlam_do.my_e_commerce_website.security;

import kimlam_do.my_e_commerce_website.service.jwt.JwtFilter;
import kimlam_do.my_e_commerce_website.service.user.UserSecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {
    private final JwtFilter jwtFilter;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserSecurityService userSecurityService) {
        DaoAuthenticationProvider dap = new DaoAuthenticationProvider();
        dap.setUserDetailsService(userSecurityService);
        dap.setPasswordEncoder(passwordEncoder());
        return dap;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(config -> config
                        // Các endpoint công khai
                        .requestMatchers(HttpMethod.GET, Endpoints.PUBLIC_GET_ENDPOINTS).permitAll()
                        .requestMatchers(HttpMethod.POST, Endpoints.PUBLIC_POST_ENDPOINTS).permitAll()
                        .requestMatchers(HttpMethod.PUT, Endpoints.PUBLIC_PUT_ENDPOINTS).permitAll()
                        .requestMatchers(HttpMethod.DELETE, Endpoints.PUBLIC_DELETE_ENDPOINTS).permitAll()
                        // Các endpoint dành cho tất cả quản trị và nhân viên
                        .requestMatchers(HttpMethod.GET, Endpoints.COMMON_ADMIN_ENDPOINTS).hasAnyAuthority("Quản lý nội dung", "Nhân viên bán hàng", "Quản trị hệ thống")
                        .requestMatchers(HttpMethod.POST, Endpoints.COMMON_ADMIN_ENDPOINTS).hasAnyAuthority("Quản lý nội dung", "Nhân viên bán hàng", "Quản trị hệ thống")
                        .requestMatchers(HttpMethod.PUT, Endpoints.COMMON_ADMIN_ENDPOINTS).hasAnyAuthority("Quản lý nội dung", "Nhân viên bán hàng", "Quản trị hệ thống")
                        .requestMatchers(HttpMethod.DELETE, Endpoints.COMMON_ADMIN_ENDPOINTS).hasAnyAuthority("Quản lý nội dung", "Nhân viên bán hàng", "Quản trị hệ thống")
                        // Các endpoint dành cho nhân viên bán hàng
                        .requestMatchers(HttpMethod.GET, Endpoints.EMPLOYEE_ENDPOINTS).hasAnyAuthority("Nhân viên bán hàng", "Quản trị hệ thống")
                        .requestMatchers(HttpMethod.POST, Endpoints.EMPLOYEE_ENDPOINTS).hasAnyAuthority("Nhân viên bán hàng", "Quản trị hệ thống")
                        .requestMatchers(HttpMethod.PUT, Endpoints.EMPLOYEE_ENDPOINTS).hasAnyAuthority("Nhân viên bán hàng", "Quản trị hệ thống")
                        .requestMatchers(HttpMethod.DELETE, Endpoints.EMPLOYEE_ENDPOINTS).hasAnyAuthority("Nhân viên bán hàng", "Quản trị hệ thống")
                        // Các endpoint dành cho quản lý nội dung
                        .requestMatchers(HttpMethod.GET, Endpoints.CONTENT_ADMIN_ENDPOINTS).hasAnyAuthority("Quản lý nội dung", "Quản trị hệ thống")
                        .requestMatchers(HttpMethod.POST, Endpoints.CONTENT_ADMIN_ENDPOINTS).hasAnyAuthority("Quản lý nội dung", "Quản trị hệ thống")
                        .requestMatchers(HttpMethod.PUT, Endpoints.CONTENT_ADMIN_ENDPOINTS).hasAnyAuthority("Quản lý nội dung", "Quản trị hệ thống")
                        .requestMatchers(HttpMethod.DELETE, Endpoints.CONTENT_ADMIN_ENDPOINTS).hasAnyAuthority("Quản lý nội dung", "Quản trị hệ thống")
                        // Các endpoint dành cho quản trị hệ thống
                        .requestMatchers(HttpMethod.GET, Endpoints.SYSTEM_ADMIN_ENDPOINTS).hasAuthority("Quản trị hệ thống")
                        .requestMatchers(HttpMethod.POST, Endpoints.SYSTEM_ADMIN_ENDPOINTS).hasAuthority("Quản trị hệ thống")
                        .requestMatchers(HttpMethod.PUT, Endpoints.SYSTEM_ADMIN_ENDPOINTS).hasAuthority("Quản trị hệ thống")
                        .requestMatchers(HttpMethod.DELETE, Endpoints.SYSTEM_ADMIN_ENDPOINTS).hasAuthority("Quản trị hệ thống")
                        .anyRequest().authenticated()  // Đảm bảo rằng tất cả các yêu cầu khác đều cần phải được xác thực
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}