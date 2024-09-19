package kimlam_do.my_e_commerce_website.service.favorite_product;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import kimlam_do.my_e_commerce_website.model.dto.FavoriteProductDTO;
import kimlam_do.my_e_commerce_website.model.entity.FavoriteProduct;
import kimlam_do.my_e_commerce_website.model.entity.Product;
import kimlam_do.my_e_commerce_website.model.entity.User;
import kimlam_do.my_e_commerce_website.repository.FavoriteProductRepository;
import kimlam_do.my_e_commerce_website.repository.ProductRepository;
import kimlam_do.my_e_commerce_website.repository.UserRepository;
import kimlam_do.my_e_commerce_website.security.UserAuthorization;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteProductServiceImpl implements FavoriteProductService {
    private final FavoriteProductRepository favoriteProductRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final UserAuthorization userAuthorization;

    @Override
    public List<FavoriteProductDTO> getAllFavoriteProducts() {
        List<FavoriteProduct> favoriteProducts = favoriteProductRepository.findAll();
        return favoriteProducts.stream()
                .map(FavoriteProductDTO::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<FavoriteProductDTO> getFavoriteProductsByUser(Integer userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();

        User currentUser = userRepository.findByEmail(currentUserEmail);
        if (currentUser == null) {
            throw new RuntimeException("Không tồn tại người dùng với email: " + currentUserEmail);
        }

        Integer currentUserId = currentUser.getId();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("Quản trị hệ thống")
                        || role.getAuthority().equals("Nhân viên bán hàng"));

        if (!isAdmin && !userId.equals(currentUserId)) {
            throw new AccessDeniedException("Bạn không có quyền truy cập dữ liệu này.");
        }

        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tồn tại người dùng với id: " + userId));

        List<FavoriteProduct> favoriteProducts = favoriteProductRepository.findByUser_Id(userId);
        return favoriteProducts.stream()
                .map(FavoriteProductDTO::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ObjectNode addFavoriteProduct(JsonNode jsonNode) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();

        // Lấy productId và userId từ request
        int productId = Integer.parseInt(formatStringByJson(jsonNode.get("productId").toString()));
        int userId = Integer.parseInt(formatStringByJson(jsonNode.get("userId").toString()));

        // Kiểm tra sự tồn tại của người dùng
        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            response.put("message", "Không tồn tại người dùng với id: " + userId);
            response.put("status", "error");
            return response;
        }
        // Kiểm tra sự tồn tại của sản phẩm
        Optional<Product> productOptional = productRepository.findById(productId);
        if (!productOptional.isPresent()) {
            response.put("message", "Không tìm thấy sản phẩm với id: " + productId);
            response.put("status", "error");
            return response;
        }

        // Xác thực người dùng và quyền hạn
        User currentUser = userAuthorization.getUserAuthorization(userId, response);
        if (currentUser == null) {
            return response;
        }

        // Tạo mới FavoriteProduct và lưu vào database
        FavoriteProduct favoriteProduct = new FavoriteProduct();
        favoriteProduct.setProduct(productOptional.get());
        favoriteProduct.setUser(userOptional.get());
        favoriteProductRepository.save(favoriteProduct);

        response.put("message", "Đã thêm sản phẩm vào danh sách yêu thích");
        response.put("status", "success");
        return response;
    }

    @Override
    public ObjectNode deleteFavoriteProduct(JsonNode jsonNode) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();

        // Lấy productId và userId từ request
        int productId = Integer.parseInt(formatStringByJson(jsonNode.get("productId").toString()));
        int userId = Integer.parseInt(formatStringByJson(jsonNode.get("userId").toString()));

        // Kiểm tra người đang gọi API và phân quyền
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();

        User currentUser = userRepository.findByEmail(currentUserEmail);
        if (currentUser == null) {
            response.put("message", "Không tồn tại người dùng (đang gọi API) với email: " + currentUserEmail);
            response.put("status", "error");
            return response;
        }

        Integer currentUserId = currentUser.getId();
        boolean isSystemAdmin = authentication.getAuthorities().stream()
                .anyMatch(role ->
                        role.getAuthority().equals("Quản trị hệ thống"));

        if (!isSystemAdmin && userId != currentUserId) {
            response.put("message", "Bạn không có quyền truy cập dữ liệu này");
            response.put("status", "error");
            return response;
        }

        // Kiểm tra sự tồn tại của người dùng
        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            response.put("message", "Không tồn tại người dùng với id: " + userId);
            response.put("status", "error");
            return response;
        }

        // Kiểm tra sự tồn tại của sản phẩm
        Optional<Product> productOptional = productRepository.findById(productId);
        if (!productOptional.isPresent()) {
            response.put("message", "Không tìm thấy sản phẩm với id: " + productId);
            response.put("status", "error");
            return response;
        }

        // Xóa sản phẩm yêu thích nếu tồn tại
        FavoriteProduct favoriteProduct = favoriteProductRepository.findByProduct_IdAndUser_Id(productId, userId);
        if (favoriteProduct == null) {
            response.put("message", "Không tìm thấy sản phẩm yêu thích với id sản phẩm: " + productId + " và id người dùng: " + userId);
            response.put("status", "error");
            return response;
        }

        // Xóa sản phẩm yêu thích
        favoriteProductRepository.delete(favoriteProduct);

        response.put("message", "Đã xóa sản phẩm khỏi danh sách yêu thích");
        response.put("status", "success");
        return response;
    }

    public static String formatStringByJson(String json) {
        return json.replaceAll("\"", "");
    }
}