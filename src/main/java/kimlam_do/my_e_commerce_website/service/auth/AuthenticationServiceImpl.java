package kimlam_do.my_e_commerce_website.service.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import kimlam_do.my_e_commerce_website.auth.AuthenticationRequest;
import kimlam_do.my_e_commerce_website.model.entity.AuthenticationType;
import kimlam_do.my_e_commerce_website.model.entity.Role;
import kimlam_do.my_e_commerce_website.model.entity.User;
import kimlam_do.my_e_commerce_website.repository.RoleRepository;
import kimlam_do.my_e_commerce_website.repository.UserRepository;
import kimlam_do.my_e_commerce_website.service.email.EmailService;
import kimlam_do.my_e_commerce_website.service.jwt.JwtService;
import kimlam_do.my_e_commerce_website.validator.EmailValidator;
import kimlam_do.my_e_commerce_website.validator.PasswordValidator;
import kimlam_do.my_e_commerce_website.validator.PhoneNumberValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final JwtService jwtService;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Override
    public ObjectNode authenticate(AuthenticationRequest authenticationRequest) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();
        // Kiểm tra nếu email hoặc mật khẩu bị thiếu
        if (authenticationRequest.getEmail() == null || authenticationRequest.getEmail().isEmpty()) {
            response.put("message", "Email không được để trống");
            response.put("status", "error");
            return response;
        }
        if (authenticationRequest.getPassword() == null || authenticationRequest.getPassword().isEmpty()) {
            response.put("message", "Mật khẩu không được để trống");
            response.put("status", "error");
            return response;
        }

        try {
            // Xác thực người dùng
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authenticationRequest.getEmail(),
                            authenticationRequest.getPassword()
                    )
            );
            // Kiểm tra xem xác thực có thành công
            if (authentication.isAuthenticated()) {
                // Lấy thông tin người dùng từ cơ sở dữ liệu
                User user = userRepository.findByEmail(authenticationRequest.getEmail());
                if (user == null) {
                    response.put("message", "Người dùng không tồn tại");
                    response.put("status", "error");
                    return response;
                }
                // Kiểm tra trạng thái `enabled`
                if (!user.isEnabled()) {
                    response.put("message", "Tài khoản của bạn đã bị vô hiệu hóa. Vui lòng liên hệ với quản trị viên để được hỗ trợ");
                    response.put("status", "error");
                    return response;
                }
                user.setLastLoginTime(LocalDateTime.now());
                userRepository.save(user);
                String jwt = jwtService.generateToken(authenticationRequest.getEmail());
                response.put("token", jwt);
                response.put("message", "Đăng nhập thành công");
                response.put("status", "success");
            } else {
                response.put("message", "Xác thực không thành công");
                response.put("status", "error");
            }
        } catch (AuthenticationException e) {
            response.put("message", "Thông tin xác thực không chính xác");
            response.put("status", "error");
        } catch (Exception e) {
            response.put("message", "Đã xảy ra lỗi trong quá trình đăng nhập");
            response.put("status", "error");
        }
        return response;
    }

    @Override
    public ObjectNode registerUser(JsonNode jsonData) {
        // Lấy các tham số đã được POST
        String email = formatStringByJson(jsonData.has("email") ? jsonData.get("email").asText() : null);
        String firstName = formatStringByJson(jsonData.has("firstName") ? jsonData.get("firstName").asText() : null);
        String lastName = formatStringByJson(jsonData.has("lastName") ? jsonData.get("lastName").asText() : null);
        String phoneNumber = formatStringByJson(jsonData.has("phoneNumber") ? jsonData.get("phoneNumber").asText() : null);
        String password = formatStringByJson(jsonData.has("password") ? jsonData.get("password").asText() : null);

        // Tạo cơ sở phản hồi
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();
        // Kiểm tra tính hợp lệ của email
        if (!EmailValidator.isValidEmail(email)) {
            response.put("message", "Email không hợp lệ");
            response.put("status", "error");
            return response;
        }
        if (userRepository.existsByEmail(email)) {
            response.put("message", "Email đã tồn tại");
            response.put("status", "error");
            return response;
        }
        // Kiểm tra tính hợp lệ của họ và tên
        if (firstName == null || firstName.trim().isEmpty() || lastName == null || lastName.trim().isEmpty()) {
            response.put("message", "Họ, tên không được để trống");
            response.put("status", "error");
            return response;
        }
        // Kiểm tra tính hợp lệ của số điện thoại
        if (!PhoneNumberValidator.isValidPhoneNumber(phoneNumber)) {
            response.put("message", "Số điện thoại không hợp lệ");
            response.put("status", "error");
            return response;
        }
        // Kiểm tra tính hợp lệ của mật khẩu
        if (password == null || password.trim().isEmpty() ||
                !PasswordValidator.hasUppercase(password) ||
                !PasswordValidator.hasLowercase(password) ||
                !PasswordValidator.hasDigit(password) ||
                !PasswordValidator.hasSpecialChar(password) ||
                !PasswordValidator.hasMinLength(password, 8)) {
            response.put("message", " phải chứa ít nhất 1 ký tự in hoa, 1 ký tự in thường, 1 chữ số, 1 ký tự đặc biệt và có độ dài ít nhất 8 ký tự");
            response.put("status", "error");
            return response;
        }

        // Mã hóa mật khẩu
        String encodedPassword = passwordEncoder.encode(password);
        // Tạo và mã hóa mã kích hoạt
        String verificationCode = createVerificationCode();

        // Tạo đối tượng User và thiết lập các thuộc tính
        User user = new User();
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhoneNumber(phoneNumber);
        user.setPassword(encodedPassword);
        user.setVerificationCode(verificationCode);
        user.setEnabled(false);
        user.setCreatedTime(LocalDateTime.now());
        user.setAuthenticationType(AuthenticationType.DATABASE);
        user.setPhoto("");
        // Thêm vai trò "Khách hàng" vào danh sách vai trò của user này
        List<Role> roles = new ArrayList<>();
        Role defaultRole = roleRepository.findByName("Khách hàng");
        if (defaultRole != null) {
            roles.add(defaultRole);
        }
        user.setRoles(roles);

        // Lưu người dùng vào CSDL và phản hồi thành công
        User registeredUser = userRepository.save(user);
        sendVerificationEmail(firstName, email, user.getVerificationCode());
        response.put("message", "Đăng ký thành công, hãy kiểm tra email để kích hoạt tài khoản");
        response.put("status", "success");
        return response;
    }

    @Override
    public ObjectNode enableUserAccount(String email, String verificationCode) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();

        // Tìm theo email
        User user = userRepository.findByEmail(email);
        if (user == null) {
            response.put("message", "Địa chỉ email không tồn tại trong hệ thống của chúng tôi. Vui lòng kiểm tra lại.");
            response.put("status", "error");
            return response;
        }
        if (user.isEnabled()) {
            response.put("message", "Tài khoản của bạn đã được kích hoạt trước đó. Bạn có thể đăng nhập ngay.");
            response.put("status", "info");
            return response;
        }
        if (verificationCode.equals(user.getVerificationCode())) {
            user.setEnabled(true);
            userRepository.save(user);
            response.put("message", "Tài khoản của bạn đã được kích hoạt thành công! Hãy đăng nhập và khám phá nền tảng của chúng tôi.");
            response.put("status", "success");
            return response;
        } else {
            response.put("message", "Mã kích hoạt không chính xác. Vui lòng kiểm tra lại mã và thử lại.");
            response.put("status", "error");
            return response;
        }
    }

    private String createVerificationCode() {
        return UUID.randomUUID().toString();
    }

    private void sendVerificationEmail(String userName, String email, String verificationCode) {
        String url = "http://localhost:3000/enable/" + email + "/" + verificationCode;
        String subject = "[TECH HUB] Kích hoạt tài khoản người dùng";
        String text = "<html>\n" +
                "<body>\n" +
                "    <p>Xin chào <strong>" + userName + "</strong>,</p>\n" +
                "    <p>Chào mừng bạn đến với Tech Hub! Cảm ơn bạn đã đăng ký tài khoản với chúng tôi.</p>\n" +
                "    <p>Để hoàn tất quá trình đăng ký và bắt đầu sử dụng các dịch vụ của chúng tôi, vui lòng kích hoạt tài khoản của bạn bằng cách nhấp vào liên kết dưới đây:</p>\n" +
                "    <p><a href=\"" + url + "\">Kích hoạt tài khoản của bạn</a></p>\n" +
                "    <p>Nếu bạn không thực hiện yêu cầu này, vui lòng bỏ qua email này hoặc liên hệ với chúng tôi ngay lập tức.</p>\n" +
                "    <p>Trân trọng,<br>\n" +
                "    Đỗ Kim Lâm<br>\n" +
                "    Co-founder, Tech Hub</p>\n" +
                "</body>\n" +
                "</html>";
        emailService.sendEmail("dokimlamut@gmail.com", email, subject, text);
    }

    public static String formatStringByJson(String json) {
        return json.replaceAll("\"", "");
    }
}