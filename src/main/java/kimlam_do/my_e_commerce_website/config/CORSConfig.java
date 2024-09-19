package kimlam_do.my_e_commerce_website.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class CORSConfig {
    private static final Long MAX_AGE = 3600L; // Thời gian (tính bằng giây) mà kết quả preflight request được lưu trong cache của trình duyệt

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // Cho phép gửi cookie và thông tin xác thực từ các nguồn khác nhau (CORS requests có credentials)
        config.setAllowCredentials(true);

        // Đặt origin cho phép, ở đây là http://localhost:3000
        config.addAllowedOrigin("http://localhost:3000");

        // Đặt các headers mà server cho phép trong các yêu cầu CORS
        config.setAllowedHeaders(Arrays.asList(
                HttpHeaders.AUTHORIZATION, // Cho phép header Authorization
                HttpHeaders.CONTENT_TYPE,  // Cho phép header Content-Type
                HttpHeaders.ACCEPT));      // Cho phép header Accept

        // Đặt các HTTP methods mà server cho phép trong các yêu cầu CORS
        config.setAllowedMethods(Arrays.asList(
                HttpMethod.GET.name(),     // Cho phép method GET
                HttpMethod.POST.name(),    // Cho phép method POST
                HttpMethod.PUT.name(),     // Cho phép method PUT
                HttpMethod.DELETE.name()   // Cho phép method DELETE
        ));

        // Đặt thời gian lưu cache cho các preflight request
        config.setMaxAge(MAX_AGE);

        // Đăng ký cấu hình CORS cho tất cả các đường dẫn (/**)
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}