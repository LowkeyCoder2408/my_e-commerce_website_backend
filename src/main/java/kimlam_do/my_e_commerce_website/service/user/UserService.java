package kimlam_do.my_e_commerce_website.service.user;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import kimlam_do.my_e_commerce_website.model.dto.UserDTO;
import kimlam_do.my_e_commerce_website.model.entity.Role;
import kimlam_do.my_e_commerce_website.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface UserService {

    boolean existsByEmail(String email);

    List<UserDTO> getAllUsers();

    Optional<User> getUserById(int userId);

    ObjectNode forgotPassword(JsonNode jsonData);

    ObjectNode resetPassword(JsonNode jsonData);

    boolean checkCurrentPassword(String currentPassword, Integer userId);

    ObjectNode changePassword(JsonNode jsonData);

    ObjectNode changeInformation(JsonNode jsonData);

    ObjectNode changeAvatar(MultipartFile avatar, Integer userId);

    List<User> getCustomers();

    List<User> getCommonAdministrators();

    ObjectNode addUser(String firstName, String lastName, String password, String email, String phoneNumber, String rolesJson, MultipartFile photo);

    ObjectNode updateUser(String firstName, String lastName, String phoneNumber, String rolesJson, MultipartFile photo);

    ObjectNode deleteUser(Integer userId);
}