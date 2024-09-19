package kimlam_do.my_e_commerce_website.service.cart_item;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import kimlam_do.my_e_commerce_website.model.dto.CartItemDTO;

import java.util.List;

public interface CartItemService {
    List<CartItemDTO> getAllCartItems();

    List<CartItemDTO> getCartItemsByUser(Integer userId);

    ObjectNode addCartItem(JsonNode jsonData);

    ObjectNode updateCartItem(JsonNode jsonData);

    ObjectNode deleteCartItem(int cartItemId);
}