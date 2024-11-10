package kimlam_do.my_e_commerce_website.service.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import kimlam_do.my_e_commerce_website.model.dto.UserDTO;
import kimlam_do.my_e_commerce_website.model.entity.AuthenticationType;
import kimlam_do.my_e_commerce_website.model.entity.Role;
import kimlam_do.my_e_commerce_website.model.entity.User;
import kimlam_do.my_e_commerce_website.repository.RoleRepository;
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
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
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
    public List<User> getCustomers() {
        Role customerRole = roleRepository.findByName("Khách hàng");
        return userRepository.findAll().stream().filter(user -> user.getRoles().contains(customerRole)).collect(Collectors.toList());
    }

    @Override
    public List<User> getCommonAdministrators() {
        Role adminRole = roleRepository.findByName("Quản trị hệ thống");
        Role contentManagerRole = roleRepository.findByName("Quản lý nội dung");
        Role salesRole = roleRepository.findByName("Nhân viên bán hàng");

        // Lọc danh sách người dùng có chứa ít nhất một trong ba vai trò trên
        return userRepository.findAll().stream().filter(user -> user.getRoles().contains(adminRole) || user.getRoles().contains(contentManagerRole) || user.getRoles().contains(salesRole)).collect(Collectors.toList());
    }

    @Override
    public ObjectNode addUser(String firstName, String lastName, String password, String email, String phoneNumber, String rolesJson, MultipartFile photo) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();

        // Lấy người dùng hiện tại từ SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();
        User currentUser = userRepository.findByEmail(currentUserEmail);
        if (currentUser == null) {
            response.put("message", "Không tồn tại người dùng (đang gọi API)");
            response.put("status", "error");
            return null;
        }

        // Kiểm tra quyền "Quản trị hệ thống" hoặc "Quản lý nội dung"
        boolean isAdmin = currentUser.getRoles().stream().anyMatch(role -> role.getName().equals("Quản trị hệ thống") || role.getName().equals("Quản lý nội dung"));

        if (!isAdmin) {
            response.put("message", "Bạn không có quyền thêm người dùng");
            response.put("status", "error");
            return response;
        }

        // Kiểm tra email hợp lệ
        if (!EmailValidator.isValidEmail(email)) {
            response.put("message", "Email không hợp lệ");
            response.put("status", "error");
            return response;
        }

        // Kiểm tra email đã tồn tại
        if (userRepository.findByEmail(email) != null) {
            response.put("message", "Email đã được sử dụng");
            response.put("status", "error");
            return response;
        }

        // Kiểm tra các trường thông tin người dùng
        if (firstName == null || firstName.trim().isEmpty()) {
            response.put("message", "Tên không được để trống");
            response.put("status", "error");
            return response;
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            response.put("message", "Họ không được để trống");
            response.put("status", "error");
            return response;
        }
        if (password == null || password.trim().isEmpty() || !PasswordValidator.hasUppercase(password) || !PasswordValidator.hasLowercase(password) || !PasswordValidator.hasDigit(password) || !PasswordValidator.hasSpecialChar(password) || !PasswordValidator.hasMinLength(password, 8)) {
            response.put("message", "Mật khẩu phải có ít nhất 8 ký tự, bao gồm chữ in hoa, chữ in thường, số và ký tự đặc biệt");
            response.put("status", "error");
            return response;
        }

        // Chuyển đổi chuỗi JSON roles sang danh sách các vai trò
        List<String> roleNames;
        try {
            roleNames = mapper.readValue(rolesJson, new TypeReference<List<String>>() {
            });
        } catch (JsonProcessingException e) {
            response.put("message", "Định dạng roles không hợp lệ");
            response.put("status", "error");
            return response;
        }

        if (roleNames.isEmpty()) {
            response.put("message", "Người dùng phải có ít nhất một vai trò");
            response.put("status", "error");
            return response;
        }
        if (roleNames.size() > 2) {
            response.put("message", "Người dùng chỉ có thể có tối đa 2 vai trò");
            response.put("status", "error");
            return response;
        }

        List<Role> roles = new ArrayList<>();
        for (String roleName : roleNames) {
            Role role = roleRepository.findByName(roleName);
            if (role == null) {
                response.put("message", "Vai trò không hợp lệ: " + roleName);
                response.put("status", "error");
                return response;
            }
            roles.add(role);
        }

        // Xử lý ảnh đại diện nếu có
        String photoUrl = null;
        String photoPublicId = null;
        if (photo != null && !photo.isEmpty()) {
            if (photo.getSize() > 10 * 1024 * 1024) { // Giới hạn 10MB
                response.put("message", "Kích thước ảnh vượt quá 10MB");
                response.put("status", "error");
                return response;
            }
            if (!photo.getContentType().startsWith("image/")) {
                response.put("message", "Chỉ chấp nhận ảnh");
                response.put("status", "error");
                return response;
            }
            try {
                Map<String, String> uploadResult = cloudinaryService.uploadImage(photo);
                photoUrl = uploadResult.get("imageUrl");
                photoPublicId = uploadResult.get("publicId");
            } catch (Exception e) {
                response.put("message", "Lỗi khi tải ảnh lên");
                response.put("status", "error");
                return response;
            }
        }

        // Tạo người dùng mới
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);
        user.setPhoto(photoUrl);
        user.setPhotoPublicId(photoPublicId);
        user.setAuthenticationType(AuthenticationType.DATABASE);
        user.setRoles(roles);

        userRepository.save(user);

        response.put("message", "Thêm người dùng mới thành công");
        response.put("status", "success");
        return response;
    }

    @Override
    public ObjectNode updateUser(Integer userId, String firstName, String lastName, String phoneNumber, String rolesJson, MultipartFile photo) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();

        // Lấy thông tin người dùng đang gọi API
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();
        User currentUser = userRepository.findByEmail(currentUserEmail);

        if (currentUser == null) {
            response.put("message", "Không tồn tại người dùng (đang gọi API)");
            response.put("status", "error");
            return response;
        }

        // Kiểm tra quyền "Quản trị hệ thống" hoặc "Quản lý nội dung"
        boolean isAdmin = currentUser.getRoles().stream().anyMatch(role -> role.getName().equals("Quản trị hệ thống") || role.getName().equals("Quản lý nội dung"));

        if (!isAdmin) {
            response.put("message", "Bạn không có quyền cập nhật thông tin người dùng này");
            response.put("status", "error");
            return response;
        }

        // Tìm người dùng cần cập nhật theo userId
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            response.put("message", "Không tìm thấy người dùng với ID: " + userId);
            response.put("status", "error");
            return response;
        }

        // Kiểm tra và xử lý FirstName
        if (firstName == null || firstName.trim().isEmpty()) {
            response.put("message", "Tên người dùng không được để trống");
            response.put("status", "error");
            return response;
        }

        // Kiểm tra và xử lý LastName
        if (lastName == null || lastName.trim().isEmpty()) {
            response.put("message", "Họ người dùng không được để trống");
            response.put("status", "error");
            return response;
        }

        // Kiểm tra và xử lý PhoneNumber
        if (phoneNumber == null || phoneNumber.trim().isEmpty() || !PhoneNumberValidator.isValidPhoneNumber(phoneNumber)) {
            response.put("message", "Số điện thoại không hợp lệ");
            response.put("status", "error");
            return response;
        }

        // Kiểm tra và xử lý Roles
        if (rolesJson == null || rolesJson.trim().isEmpty()) {
            response.put("message", "Vai trò không được để trống");
            response.put("status", "error");
            return response;
        }

        // Chuyển đổi vai trò từ JSON
        List<String> roleNames;
        try {
            roleNames = mapper.readValue(rolesJson, new TypeReference<List<String>>() {
            });
        } catch (JsonProcessingException e) {
            response.put("message", "Định dạng roles không hợp lệ");
            response.put("status", "error");
            return response;
        }

        if (roleNames.size() > 2) {
            response.put("message", "Người dùng chỉ có thể có tối đa 2 vai trò");
            response.put("status", "error");
            return response;
        }

        // Kiểm tra trường hợp "Quản trị hệ thống" là vai trò duy nhất và bị thay thế
        boolean isAdminRoleBeingRemoved = user.getRoles().stream().anyMatch(role -> role.getName().equals("Quản trị hệ thống"));
        if (isAdminRoleBeingRemoved && roleNames.stream().noneMatch(role -> role.equals("Quản trị hệ thống"))) {
            // Kiểm tra nếu người dùng duy nhất có "Quản trị hệ thống"
            long adminCount = userRepository.countByRoleName("Quản trị hệ thống");
            if (adminCount == 1) {
                response.put("message", "Hệ thống phải có ít nhất một Quản trị hệ thống");
                response.put("status", "error");
                return response;
            }
        }

        // Cập nhật FirstName, LastName, PhoneNumber
        boolean isFirstNameChanged = !firstName.equals(user.getFirstName());
        boolean isLastNameChanged = !lastName.equals(user.getLastName());
        boolean isPhoneNumberChanged = !phoneNumber.equals(user.getPhoneNumber());

        if (isFirstNameChanged) {
            user.setFirstName(firstName);
        }
        if (isLastNameChanged) {
            user.setLastName(lastName);
        }
        if (isPhoneNumberChanged) {
            user.setPhoneNumber(phoneNumber);
        }

        // Cập nhật Roles (thay thế hoàn toàn vai trò cũ)
        List<Role> roles = new ArrayList<>();
        for (String roleName : roleNames) {
            Role role = roleRepository.findByName(roleName);
            if (role == null) {
                response.put("message", "Vai trò không hợp lệ: " + roleName);
                response.put("status", "error");
                return response;
            }
            roles.add(role);
        }
        user.setRoles(roles);

        // Xử lý ảnh đại diện nếu có thay đổi
        boolean isPhotoChanged = photo != null && !photo.isEmpty();
        if (isPhotoChanged) {
            try {
                // Nếu có ảnh cũ, xóa ảnh cũ trước khi cập nhật
                if (user.getPhotoPublicId() != null && !user.getPhotoPublicId().isEmpty()) {
                    cloudinaryService.deleteImage(user.getPhotoPublicId());
                }

                Map<String, String> uploadResult = cloudinaryService.uploadImage(photo);
                user.setPhoto(uploadResult.get("imageUrl"));
                user.setPhotoPublicId(uploadResult.get("publicId"));
            } catch (Exception e) {
                response.put("message", "Lỗi khi cập nhật ảnh");
                response.put("status", "error");
                return response;
            }
        }

        // Lưu thông tin người dùng
        userRepository.save(user);

        // Phản hồi thành công nếu có thay đổi
        response.put("message", "Cập nhật thông tin người dùng thành công");
        response.put("status", "success");
        return response;
    }

    @Override
    public ObjectNode deleteUser(Integer userId) {
        // Lấy thông tin người dùng hiện tại từ SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();

        // Tìm user theo ID
        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            response.put("message", "Không tồn tại người dùng với id: " + userId);
            response.put("status", "error");
            return response;
        }
        User userToDelete = userOptional.get();

        // Kiểm tra nếu người dùng có quyền "Quản trị hệ thống"
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("Quản trị hệ thống") || role.getAuthority().equals("Quản lý nội dung"));

        // Kiểm tra quyền xóa
        if (!userToDelete.getEmail().equals(currentUsername) && !isAdmin) {
            response.put("message", "Bạn không có quyền xóa người dùng này");
            response.put("status", "error");
            return response;
        }

        // Kiểm tra xem có bao nhiêu người dùng có quyền "Quản trị hệ thống"
        Role role = roleRepository.findByName("Quản trị hệ thống");

        if (role == null) {
            response.put("message", "Quyền Quản trị hệ thống không tồn tại");
            response.put("status", "error");
            return response;
        }

        long systemAdminCount = userRepository.countByRoleName(role.getName());

        // Nếu chỉ còn một người có quyền "Quản trị hệ thống" và người này bị xóa
        if (systemAdminCount == 1 && userToDelete.getRoles().contains(role)) {
            response.put("message", "Không thể xóa tài khoản duy nhất có quyền Quản trị hệ thống");
            response.put("status", "error");
            return response;
        }

        // Kiểm tra xem người dùng có ảnh không và xóa ảnh từ Cloudinary nếu có
        if (userToDelete.getPhotoPublicId() != null && !userToDelete.getPhotoPublicId().isEmpty()) {
            try {
                cloudinaryService.deleteImage(userToDelete.getPhotoPublicId());
            } catch (Exception e) {
                e.printStackTrace();
                response.put("message", "Đã xảy ra lỗi khi xóa ảnh của người dùng");
                response.put("status", "error");
                return response;
            }
        }

        // Xóa người dùng
        try {
            userRepository.delete(userToDelete);
            response.put("message", "Xóa người dùng thành công");
            response.put("status", "success");

            // Nếu xóa chính tài khoản của mình, gửi thêm "logout" để frontend biết cần đăng xuất
            if (userToDelete.getEmail().equals(currentUsername)) {
                response.put("logout", true);
            }

            return response;
        } catch (Exception e) {
            e.printStackTrace();
            response.put("message", "Đã xảy ra lỗi khi xóa người dùng");
            response.put("status", "error");
            return response;
        }
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
            response.put("message", "Mật khẩu phải có ít nhất 8 ký tự, bao gồm chữ in hoa, chữ in thường, số và ký tự đặc biệt");
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