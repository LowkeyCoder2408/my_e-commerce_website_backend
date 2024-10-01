package kimlam_do.my_e_commerce_website.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import kimlam_do.my_e_commerce_website.model.dto.UserDTO;
import kimlam_do.my_e_commerce_website.model.entity.User;
import kimlam_do.my_e_commerce_website.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
}