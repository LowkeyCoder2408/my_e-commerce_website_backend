package kimlam_do.my_e_commerce_website.service.cart_item;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import kimlam_do.my_e_commerce_website.model.dto.CartItemDTO;
import kimlam_do.my_e_commerce_website.model.entity.CartItem;
import kimlam_do.my_e_commerce_website.model.entity.Product;
import kimlam_do.my_e_commerce_website.model.entity.User;
import kimlam_do.my_e_commerce_website.repository.CartItemRepository;
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
public class CartItemServiceImpl implements CartItemService {
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final UserAuthorization userAuthorization;

    @Override
    public List<CartItemDTO> getAllCartItems() {
        List<CartItem> cartItems = cartItemRepository.findAll();
        return cartItems.stream().map(CartItemDTO::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<CartItemDTO> getCartItemsByUser(Integer userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();

        User currentUser = userRepository.findByEmail(currentUserEmail);
        if (currentUser == null) {
            throw new RuntimeException("Không tồn tại người dùng với email: " + currentUserEmail);
        }

        Integer currentUserId = currentUser.getId();
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("Quản trị hệ thống") || role.getAuthority().equals("Nhân viên bán hàng"));

        if (!isAdmin && !userId.equals(currentUserId)) {
            throw new AccessDeniedException("Bạn không có quyền truy cập dữ liệu này.");
        }

        userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Không tồn tại người dùng với id: " + userId));

        List<CartItem> cartItems = cartItemRepository.findByUser_Id(userId);
        return cartItems.stream().map(CartItemDTO::toDTO).collect(Collectors.toList());
    }

    @Override
    public ObjectNode addCartItem(JsonNode jsonData) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();

        int userId = Integer.parseInt(formatStringByJson(String.valueOf(jsonData.get("userId"))));
        int productId = Integer.parseInt(formatStringByJson(String.valueOf(jsonData.get("productId"))));
        int requestedQuantity = Integer.parseInt(formatStringByJson(String.valueOf(jsonData.get("quantity"))));

        // Kiểm tra nếu số lượng là số âm
        if (requestedQuantity <= 0) {
            response.put("message", "Số lượng phải lớn hơn 0");
            response.put("status", "error");
            return response;
        }

        // Xác thực và kiểm tra quyền hạn của người dùng
        User user = userAuthorization.getUserAuthorization(userId, response);
        if (user == null) {
            return response;
        }

        // Tiếp tục logic thêm sản phẩm vào giỏ hàng...
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (!optionalProduct.isPresent()) {
            response.put("message", "Không tìm thấy sản phẩm với id: " + productId);
            response.put("status", "error");
            return response;
        }
        Product product = optionalProduct.get();

        if (requestedQuantity > product.getQuantity()) {
            response.put("message", "Số lượng yêu cầu vượt quá số lượng tồn kho hiện có (" + product.getQuantity() + ")");
            response.put("status", "error");
            return response;
        }

        // Kiểm tra và cập nhật hoặc thêm mới sản phẩm vào giỏ hàng
        List<CartItem> cartItemsOfUser = cartItemRepository.findByUser_Id(userId);
        boolean isExisting = false;

        for (CartItem cartItemOfUser : cartItemsOfUser) {
            if (cartItemOfUser.getProduct().getId() == product.getId()) {
                int newQuantity = cartItemOfUser.getQuantity() + requestedQuantity;

                if (newQuantity > product.getQuantity()) {
                    response.put("message", "Số lượng tổng cộng vượt quá số lượng tồn kho hiện có (" + product.getQuantity() + ")");
                    response.put("status", "error");
                    return response;
                }
                cartItemOfUser.setQuantity(newQuantity);
                cartItemRepository.save(cartItemOfUser);
                isExisting = true;
                response.put("message", "Cập nhật số lượng sản phẩm trong giỏ hàng thành công");
                response.put("status", "success");
                return response;
            }
        }

        if (!isExisting) {
            CartItem newCartItem = new CartItem();
            newCartItem.setUser(user);
            newCartItem.setQuantity(requestedQuantity);
            newCartItem.setProduct(product);
            cartItemRepository.save(newCartItem);
            response.put("message", "Thêm sản phẩm vào giỏ hàng thành công");
            response.put("status", "success");
        }
        return response;
    }

    @Override
    public ObjectNode updateCartItem(JsonNode jsonData) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();

        int cartId = Integer.parseInt(formatStringByJson(String.valueOf(jsonData.get("cartId"))));
        int requestedQuantity = Integer.parseInt(formatStringByJson(String.valueOf(jsonData.get("quantity"))));

        // Kiểm tra nếu số lượng là số âm
        if (requestedQuantity <= 0) {
            response.put("message", "Số lượng phải lớn hơn 0");
            response.put("status", "error");
            return response;
        }

        Optional<CartItem> cartItemOptional = cartItemRepository.findById(cartId);
        if (!cartItemOptional.isPresent()) {
            response.put("message", "Không tìm thấy giỏ hàng với id: " + cartId);
            response.put("status", "error");
            return response;
        }
        CartItem cartItem = cartItemOptional.get();
        Product product = cartItem.getProduct();

        if (requestedQuantity > product.getQuantity()) {
            response.put("message", "Số lượng yêu cầu vượt quá số lượng tồn kho hiện có (" + product.getQuantity() + ")");
            response.put("status", "error");
            return response;
        }

        // Xác thực và kiểm tra quyền hạn của người dùng
        User user = userAuthorization.getUserAuthorization(cartItem.getUser().getId(), response);
        if (user == null) {
            return response;
        }

        cartItem.setQuantity(requestedQuantity);
        cartItemRepository.save(cartItem);
        response.put("message", "Cập nhật giỏ hàng thành công");
        response.put("status", "success");
        return response;
    }

    @Override
    public ObjectNode deleteCartItem(int cartItemId) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();

        Optional<CartItem> cartItemOptional = cartItemRepository.findById(cartItemId);
        if (!cartItemOptional.isPresent()) {
            response.put("message", "Không tồn tại sản phẩm trong giỏ hàng với id: " + cartItemId);
            response.put("status", "error");
            return response;
        }
        CartItem cartItem = cartItemOptional.get();

        // Xác thực và kiểm tra quyền hạn của người dùng (nếu id người đang gọi API ko trùng với id ng dùng của cart item thì ko thể xóa cart và return response đã xử lý ở hàm getUserAuth...)
        User user = userAuthorization.getUserAuthorization(cartItem.getUser().getId(), response);
        if (user == null) {
            return response;
        }

        cartItemRepository.delete(cartItem);
        response.put("message", "Xóa sản phẩm trong giỏ hàng thành công");
        response.put("status", "success");
        return response;
    }

    public static String formatStringByJson(String json) {
        return json.replaceAll("\"", "");
    }
}