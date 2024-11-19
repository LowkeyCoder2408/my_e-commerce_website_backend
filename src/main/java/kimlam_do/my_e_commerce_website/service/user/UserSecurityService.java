package kimlam_do.my_e_commerce_website.service.user;

import kimlam_do.my_e_commerce_website.model.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserSecurityService extends UserDetailsService {
    public User findByEmail(String email);
}