package kimlam_do.my_e_commerce_website.service.user;

import kimlam_do.my_e_commerce_website.model.dto.UserDTO;
import kimlam_do.my_e_commerce_website.model.entity.Role;
import kimlam_do.my_e_commerce_website.model.entity.User;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface UserService {

    boolean existsByEmail(String email);

    List<UserDTO> getAllUsers();

    Optional<User> getUserById(int userId);
}