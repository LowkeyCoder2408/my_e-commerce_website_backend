package kimlam_do.my_e_commerce_website.service.user;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
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

    ObjectNode forgotPassword(JsonNode jsonData);

    ObjectNode resetPassword(JsonNode jsonData);

    ObjectNode validateResetToken(String token);
}