package kimlam_do.my_e_commerce_website.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import kimlam_do.my_e_commerce_website.model.dto.UserDTO;
import kimlam_do.my_e_commerce_website.model.entity.User;
import kimlam_do.my_e_commerce_website.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/existsByEmail")
    public boolean existsByEmail(@RequestParam(value = "email") String email) {
        return userService.existsByEmail(email);
    }

    @GetMapping("/check-current-password")
    public boolean checkCurrentPassword(@RequestParam(value = "currentPassword") String currentPassword, @RequestParam(value = "userId") Integer userId) {
        return userService.checkCurrentPassword(currentPassword, userId);
    }

    @PutMapping(path = "/change-avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ObjectNode> changeAvatar(@RequestParam("avatar") MultipartFile avatar, @RequestParam("userId") Integer userId) throws IOException {
        try {
            ObjectNode response = userService.changeAvatar(avatar, userId);
            String status = response.get("status").asText();
            HttpStatus httpStatus = "error".equals(status) ? HttpStatus.BAD_REQUEST : HttpStatus.OK;
            return new ResponseEntity<>(response, httpStatus);
        } catch (Exception e) {
            e.printStackTrace();
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode errorResponse = mapper.createObjectNode();
            errorResponse.put("message", "Đã xảy ra lỗi khi thay đổi ảnh đại diện");
            errorResponse.put("status", "error");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(path = "/change-information")
    public ResponseEntity<ObjectNode> changeInformation(@RequestBody JsonNode jsonData) {
        try {
            ObjectNode response = userService.changeInformation(jsonData);
            String status = response.get("status").asText();
            HttpStatus httpStatus = "error".equals(status) ? HttpStatus.BAD_REQUEST : HttpStatus.OK;
            return new ResponseEntity<>(response, httpStatus);
        } catch (Exception e) {
            e.printStackTrace();
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode errorResponse = mapper.createObjectNode();
            errorResponse.put("message", "Đã xảy ra lỗi khi thay đổi thông tin cá nhân");
            errorResponse.put("status", "error");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(path = "/forgot-password")
    public ResponseEntity<ObjectNode> forgotPassword(@RequestBody JsonNode jsonData) {
        try {
            ObjectNode response = userService.forgotPassword(jsonData);
            String status = response.get("status").asText();
            HttpStatus httpStatus = "error".equals(status) ? HttpStatus.BAD_REQUEST : HttpStatus.OK;
            return new ResponseEntity<>(response, httpStatus);
        } catch (Exception e) {
            e.printStackTrace();
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode errorResponse = mapper.createObjectNode();
            errorResponse.put("message", "Đã xảy ra lỗi khi yêu cầu lấy lại mật khẩu");
            errorResponse.put("status", "error");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(path = "/reset-password")
    public ResponseEntity<ObjectNode> resetPassword(@RequestBody JsonNode jsonData) {
        try {
            ObjectNode response = userService.resetPassword(jsonData);
            String status = response.get("status").asText();
            HttpStatus httpStatus = "error".equals(status) ? HttpStatus.BAD_REQUEST : HttpStatus.OK;
            return new ResponseEntity<>(response, httpStatus);
        } catch (Exception e) {
            e.printStackTrace();
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode errorResponse = mapper.createObjectNode();
            errorResponse.put("message", "Đã xảy ra lỗi khi yêu cầu đổi mật khẩu");
            errorResponse.put("status", "error");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(path = "/change-password")
    public ResponseEntity<ObjectNode> changePassword(@RequestBody JsonNode jsonData) {
        try {
            ObjectNode response = userService.changePassword(jsonData);
            String status = response.get("status").asText();
            HttpStatus httpStatus = "error".equals(status) ? HttpStatus.BAD_REQUEST : HttpStatus.OK;
            return new ResponseEntity<>(response, httpStatus);
        } catch (Exception e) {
            e.printStackTrace();
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode errorResponse = mapper.createObjectNode();
            errorResponse.put("message", "Đã xảy ra lỗi khi thay đổi mật khẩu");
            errorResponse.put("status", "error");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        try {
            List<UserDTO> userDTOs = userService.getAllUsers();
            return ResponseEntity.ok(userDTOs);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Người dùng không tồn tại hoặc yêu cầu không hợp lệ");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã xảy ra lỗi trong quá trình xử lý yêu cầu lấy danh sách người dùng");
        }
    }

    @GetMapping("{userId}")
    private ResponseEntity<?> getUserById(@PathVariable int userId) {
        try {
            Optional<User> optionalUser = userService.getUserById(userId);
            if (!optionalUser.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Người dùng không tồn tại.");
            }
            UserDTO userDTO = UserDTO.toDTO(optionalUser.get());
            return ResponseEntity.ok(userDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã xảy ra lỗi khi lấy dữ liệu người dùng theo id.");
        }
    }

    @GetMapping("/customers")
    private ResponseEntity<?> getCustomers() {
        try {
            List<User> users = userService.getCustomers();
            List<UserDTO> userDTOs = users.stream().map(UserDTO::toDTO).collect(Collectors.toList());
            return ResponseEntity.ok(userDTOs);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã xảy ra lỗi khi lấy dữ liệu khách hàng.");
        }
    }

    @GetMapping("/administrators")
    private ResponseEntity<?> getCommonAdministrators() {
        try {
            List<User> users = userService.getCommonAdministrators();
            List<UserDTO> userDTOs = users.stream().map(UserDTO::toDTO).collect(Collectors.toList());
            return ResponseEntity.ok(userDTOs);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã xảy ra lỗi khi lấy dữ liệu quản trị viên.");
        }
    }

    @PostMapping("/add-user")
    public ResponseEntity<ObjectNode> addUser(@RequestParam("firstName") String firstName, @RequestParam("lastName") String lastName, @RequestParam("password") String password, @RequestParam("email") String email, @RequestParam("phoneNumber") String phoneNumber, @RequestParam("rolesJson") String rolesJson, @RequestParam(value = "photo", required = false) MultipartFile photo) {
        try {
            ObjectNode response = userService.addUser(firstName, lastName, password, email, phoneNumber, rolesJson, photo);
            String status = response.get("status").asText();
            HttpStatus httpStatus = "error".equals(status) ? HttpStatus.BAD_REQUEST : HttpStatus.OK;
            return new ResponseEntity<>(response, httpStatus);
        } catch (Exception e) {
            e.printStackTrace();
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode errorResponse = mapper.createObjectNode();
            errorResponse.put("message", "Đã xảy ra lỗi khi thêm người dùng mới");
            errorResponse.put("status", "error");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update-user")
    public ResponseEntity<ObjectNode> updateUser(@RequestParam("userId") Integer userId, @RequestParam("firstName") String firstName, @RequestParam("lastName") String lastName, @RequestParam("phoneNumber") String phoneNumber, @RequestParam("rolesJson") String rolesJson, @RequestParam(value = "photo", required = false) MultipartFile photo) {
        try {
            ObjectNode response = userService.updateUser(userId, firstName, lastName, phoneNumber, rolesJson, photo);
            String status = response.get("status").asText();
            HttpStatus httpStatus = "error".equals(status) ? HttpStatus.BAD_REQUEST : HttpStatus.OK;
            return new ResponseEntity<>(response, httpStatus);
        } catch (Exception e) {
            e.printStackTrace();
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode errorResponse = mapper.createObjectNode();
            errorResponse.put("message", "Đã xảy ra lỗi khi cập nhật thông tin người dùng");
            errorResponse.put("status", "error");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(path = "/delete-user/{userId}")
    public ResponseEntity<ObjectNode> deleteUser(@PathVariable Integer userId) {
        try {
            ObjectNode response = userService.deleteUser(userId);
            String status = response.get("status").asText();
            HttpStatus httpStatus = "error".equals(status) ? HttpStatus.BAD_REQUEST : HttpStatus.OK;
            return new ResponseEntity<>(response, httpStatus);
        } catch (Exception e) {
            e.printStackTrace();
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode errorResponse = mapper.createObjectNode();
            errorResponse.put("message", "Đã xảy ra lỗi khi yêu cầu xóa người dùng");
            errorResponse.put("status", "error");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}