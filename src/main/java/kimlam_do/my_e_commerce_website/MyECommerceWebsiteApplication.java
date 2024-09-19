package kimlam_do.my_e_commerce_website;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@EnableWebSecurity
@EnableJpaRepositories
public class MyECommerceWebsiteApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyECommerceWebsiteApplication.class, args);
    }
}