package kimlam_do.my_e_commerce_website.service.user;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import kimlam_do.my_e_commerce_website.model.dto.UserDTO;
import kimlam_do.my_e_commerce_website.model.entity.User;
import kimlam_do.my_e_commerce_website.repository.UserRepository;
import kimlam_do.my_e_commerce_website.service.cloudinary.CloudinaryService;
import kimlam_do.my_e_commerce_website.service.email.EmailService;
import kimlam_do.my_e_commerce_website.service.jwt.JwtService;
import kimlam_do.my_e_commerce_website.validator.EmailValidator;
import kimlam_do.my_e_commerce_website.validator.PasswordValidator;
import kimlam_do.my_e_commerce_website.validator.PhoneNumberValidator;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtService jwtService;
    private final CloudinaryService cloudinaryService;

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean checkCurrentPassword(String currentPassword, Integer userId) {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return false;
        }
        if (!user.getEmail().equals(currentEmail)) {
            return false;
        }
        if (passwordEncoder.matches(currentPassword, user.getPassword())) {
            return true;
        }
        return false;
    }

    @Override
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(UserDTO::toDTO).collect(Collectors.toList());
    }

    @Override
    public Optional<User> getUserById(int userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();

        User currentUser = userRepository.findByEmail(currentUserEmail);
//        if (currentUser == null) {
//            throw new RuntimeException("Không tồn tại người dùng với email: " + currentUserEmail);
//        }

        Integer currentUserId = currentUser.getId();
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("Quản trị hệ thống"));
        if (!isAdmin && userId != currentUserId) {
            throw new AccessDeniedException("Bạn không có quyền truy cập dữ liệu này");
        }

        return userRepository.findById(userId);
    }

    @Override
    public ObjectNode changeAvatar(MultipartFile avatar, Integer userId) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();

        // Lấy thông tin của người gọi API
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentEmail = authentication.getName();
        User currentUser = userRepository.findByEmail(currentEmail);

        // Kiểm tra người gọi API có quyền thay đổi avatar hay không
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("Quản trị hệ thống"));

        // Lấy thông tin người dùng cần thay đổi avatar theo userId
        User user = userRepository.findById(userId).orElse(null);

        // Nếu người dùng không tồn tại hoặc người gọi API không có quyền, trả về lỗi
        if (user == null) {
            response.put("message", "Người dùng không tồn tại");
            response.put("status", "error");
            return response;
        }

        if (!currentUser.getId().equals(userId) && !isAdmin) {
            response.put("message", "Bạn không có quyền thay đổi ảnh đại diện cho người dùng này");
            response.put("status", "error");
            return response;
        }

        boolean isUpdated = false;

        if (avatar != null && !avatar.isEmpty()) {
            try {
                // Nếu người dùng có avatar cũ, xóa nó trước khi cập nhật avatar mới
                if (user.getPhotoPublicId() != null && !user.getPhotoPublicId().isEmpty()) {
                    cloudinaryService.deleteImage(user.getPhotoPublicId());
                }

                // Upload avatar mới
                Map<String, String> uploadAvatarImageResult = cloudinaryService.uploadImage(avatar);
                String avatarUrl = uploadAvatarImageResult.get("imageUrl");
                String avatarPublicId = uploadAvatarImageResult.get("publicId");

                // Cập nhật thông tin avatar cho người dùng cần thay đổi
                user.setPhoto(avatarUrl);
                user.setPhotoPublicId(avatarPublicId);

                // Lưu người dùng sau khi cập nhật
                userRepository.save(user);
                isUpdated = true;
            } catch (IOException e) {
                e.printStackTrace();
                response.put("message", "Đã xảy ra lỗi trong quá trình lưu ảnh");
                response.put("status", "error");
                return response;
            }
        }

        if (isUpdated) {
            final String jwtToken = jwtService.generateToken(user.getEmail());
            response.put("message", "Ảnh đại diện đã được thay đổi thành công");
            response.put("status", "success");
            response.put("token", jwtToken);
        } else {
            response.put("message", "File ảnh không hợp lệ hoặc không có ảnh nào được cập nhật");
            response.put("status", "error");
        }

        return response;
    }

    @Override
    public ObjectNode changeInformation(JsonNode jsonData) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();

        Integer userId = jsonData.has("userId") ? jsonData.get("userId").asInt() : null;
        if (userId == null) {
            response.put("message", "Thiếu thông tin userId");
            response.put("status", "error");
            return response;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();
        User currentUser = userRepository.findByEmail(currentUserEmail);

        if (currentUser == null) {
            response.put("message", "Không tồn tại người dùng (đang gọi API) với email: " + currentUserEmail);
            response.put("status", "error");
            return response;
        }

        Integer currentUserId = currentUser.getId();
        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            response.put("message", "Người dùng không tồn tại");
            response.put("status", "error");
            return response;
        }

        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("Quản trị hệ thống"));

        if (!isAdmin && !userId.equals(currentUserId)) {
            response.put("message", "Bạn không được phép đổi thông tin cho người khác");
            response.put("status", "error");
            return response;
        }

        boolean isUpdated = false;

        // Kiểm tra và cập nhật họ nếu hợp lệ
        String lastName = jsonData.has("lastName") ? formatStringByJson(jsonData.get("lastName").asText()) : null;
        if (lastName != null && !lastName.trim().isEmpty()) {
            user.setLastName(lastName);
            isUpdated = true;
        } else {
            response.put("lastName", "Họ không được cập nhật do không hợp lệ");
        }

        // Kiểm tra và cập nhật tên nếu hợp lệ
        String firstName = jsonData.has("firstName") ? formatStringByJson(jsonData.get("firstName").asText()) : null;
        if (firstName != null && !firstName.trim().isEmpty()) {
            user.setFirstName(firstName);
            isUpdated = true;
        } else {
            response.put("firstName", "Tên không được cập nhật do không hợp lệ");
        }

        // Kiểm tra và cập nhật số điện thoại nếu hợp lệ
        String phoneNumber = jsonData.has("phoneNumber") ? formatStringByJson(jsonData.get("phoneNumber").asText()) : null;
        if (phoneNumber != null && !phoneNumber.trim().isEmpty() && PhoneNumberValidator.isValidPhoneNumber(phoneNumber)) {
            user.setPhoneNumber(phoneNumber);
            isUpdated = true;
        } else {
            response.put("phoneNumber", "Số điện thoại không được cập nhật do không hợp lệ");
        }

        // Chỉ lưu thông tin nếu có thay đổi
        if (isUpdated) {
            User newUser = userRepository.save(user);
            final String jwtToken = jwtService.generateToken(newUser.getEmail());

            response.put("message", "Cập nhật thông tin thành công");
            response.put("status", "success");
            response.put("token", jwtToken);
        } else {
            response.put("message", "Không có thông tin nào được cập nhật");
            response.put("status", "warning");
        }

        return response;
    }

    @Override
    public ObjectNode forgotPassword(JsonNode jsonData) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();

        String email = jsonData.has("email") ? formatStringByJson(jsonData.get("email").asText()) : null;

        if (email == null || !EmailValidator.isValidEmail(email)) {
            response.put("message", "Email không hợp lệ");
            response.put("status", "error");
            return response;
        }

        User user = userRepository.findByEmail(email);
        if (user == null) {
            response.put("message", "Không tìm thấy người dùng với email này");
            response.put("status", "error");
            return response;
        }

        // Sinh token và lưu vào cơ sở dữ liệu
        String resetToken = RandomStringUtils.randomAlphanumeric(30);
        user.setResetPasswordToken(resetToken);
        user.setResetPasswordTokenExpiryTime(LocalDateTime.now().plusHours(1));
        userRepository.save(user);

        // Gửi email chứa link reset mật khẩu
        String resetPasswordLink = "http://localhost:3000/reset-password?token=" + resetToken;
        sendResetPasswordEmail(user.getEmail(), resetPasswordLink);

        response.put("message", "Liên kết đặt lại mật khẩu đã được gửi đến email của bạn");
        response.put("status", "success");
        return response;
    }

    @Override
    public ObjectNode resetPassword(JsonNode jsonData) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();

        // Lấy token và mật khẩu mới từ yêu cầu
        String token = jsonData.has("resetPasswordToken") ? jsonData.get("resetPasswordToken").asText() : null;
        String newPassword = jsonData.has("password") ? jsonData.get("password").asText() : null;

        // Kiểm tra token và tìm user
        User user = userRepository.findByResetPasswordToken(token);
        if (user == null) {
            response.put("message", "Token không hợp lệ hoặc đã được sử dụng");
            response.put("status", "error");
            return response;
        }
        if (user.getResetPasswordTokenExpiryTime().isBefore(LocalDateTime.now())) {
            response.put("message", "Token đã hết hạn, xin vui lòng gửi lại yêu cầu lấy lại mật khẩu");
            response.put("status", "error");
            return response;
        }

        // Kiểm tra tính hợp lệ của mật khẩu
        if (newPassword == null || newPassword.trim().isEmpty() || !PasswordValidator.hasUppercase(newPassword) || !PasswordValidator.hasLowercase(newPassword) || !PasswordValidator.hasDigit(newPassword) || !PasswordValidator.hasSpecialChar(newPassword) || !PasswordValidator.hasMinLength(newPassword, 8)) {
            response.put("message", "Mật khẩu phải chứa ít nhất 1 ký tự in hoa, 1 ký tự in thường, 1 chữ số, 1 ký tự đặc biệt và có độ dài ít nhất 8 ký tự");
            response.put("status", "error");
            return response;
        }

        // Cập nhật mật khẩu mới
        user.setPassword(passwordEncoder.encode(newPassword));

        // Xóa token sau khi đặt lại mật khẩu thành công
        user.setResetPasswordToken(null);
        user.setResetPasswordTokenExpiryTime(null);
        userRepository.save(user);

        response.put("message", "Mật khẩu đã được đặt lại, vui lòng đăng nhập để tiếp tục");
        response.put("status", "success");
        return response;
    }

    @Override
    public ObjectNode changePassword(JsonNode jsonData) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();

        // Lấy thông tin từ JSON và kiểm tra tính hợp lệ của userId trước khi chuyển đổi
        if (!jsonData.has("userId") || jsonData.get("userId").isNull()) {
            response.put("message", "Mã người dùng không hợp lệ");
            response.put("status", "error");
            return response;
        }

        // Lấy userId từ JSON
        int userId = jsonData.get("userId").asInt();
        String currentPassword = jsonData.has("currentPassword") ? jsonData.get("currentPassword").asText() : null;
        String newPassword = jsonData.has("newPassword") ? jsonData.get("newPassword").asText() : null;
        String confirmNewPassword = jsonData.has("confirmNewPassword") ? jsonData.get("confirmNewPassword").asText() : null;

        // Lấy userId của người dùng hiện tại từ token (hoặc bất kỳ cơ chế xác thực nào)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();
        User currentUser = userRepository.findByEmail(currentUserEmail);
        if (currentUser == null) {
            response.put("message", "Không tồn tại người dùng (đang gọi API) với email: " + currentUserEmail);
            response.put("status", "error");
            return response;
        }
        Integer currentUserId = currentUser.getId();

        // Kiểm tra người gọi API có cùng id với userId
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            response.put("message", "Người dùng không tồn tại");
            response.put("status", "error");
            return response;
        }
        if (userId != currentUserId) {
            response.put("message", "Bạn không được phép đổi mật khẩu cho người khác");
            response.put("status", "error");
            return response;
        }

        // Kiểm tra mật khẩu hiện tại có chính xác không
        if (currentPassword == null || !passwordEncoder.matches(currentPassword, user.getPassword())) {
            response.put("message", "Mật khẩu hiện tại không chính xác");
            response.put("status", "error");
            return response;
        }

        // Kiểm tra tính hợp lệ của mật khẩu mới
        if (newPassword == null || newPassword.trim().isEmpty() || !PasswordValidator.hasUppercase(newPassword) || !PasswordValidator.hasLowercase(newPassword) || !PasswordValidator.hasDigit(newPassword) || !PasswordValidator.hasSpecialChar(newPassword) || !PasswordValidator.hasMinLength(newPassword, 8)) {
            response.put("message", "Mật khẩu phải có ít nhất 8 ký tự, bao gồm chữ in hoa, chữ in thường, số và ký tự đặc biệt.");
            response.put("status", "error");
            return response;
        }

        // Kiểm tra mật khẩu xác nhận có khớp với mật khẩu mới
        if (!newPassword.equals(confirmNewPassword)) {
            response.put("message", "Mật khẩu xác nhận không khớp");
            response.put("status", "error");
            return response;
        }

        // Cập nhật mật khẩu mới
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        response.put("message", "Thay đổi mật khẩu thành công, vui lòng đăng nhập lại");
        response.put("status", "success");
        return response;
    }

    private void sendResetPasswordEmail(String email, String resetLink) {
        String subject = "Yêu cầu đặt lại mật khẩu - Tech Hub";
        String message = "<p>Xin chào,</p>";
        message += "<p>Bạn đã yêu cầu đặt lại mật khẩu trên <strong>Tech Hub</strong>. Vui lòng nhấp vào liên kết dưới đây để đặt lại mật khẩu:</p>";
        message += "<a href=\"" + resetLink + "\">Đặt lại mật khẩu</a>";
        message += "<p>Liên kết này sẽ hết hạn sau 1 giờ.</p>";
        message += "<p>Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.</p>";

        emailService.sendEmail("dokimlamut@gmail.com", email, subject, message);
    }

    public static String formatStringByJson(String json) {
        return json.replaceAll("\"", "");
    }
}