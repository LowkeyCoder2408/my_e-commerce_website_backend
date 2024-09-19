package kimlam_do.my_e_commerce_website.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {
    @Value("${jwt.secret_key}")
    private String secretKey;

    public String getSecretKey() {
        return secretKey;
    }
}