package kimlam_do.my_e_commerce_website.model.dto;

import kimlam_do.my_e_commerce_website.model.entity.CartItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDTO {
    private Integer id;
    private Integer userId;
    private ProductDTO product;
    private int quantity;

    public static CartItemDTO toDTO(CartItem cartItem) {
        return cartItem == null ? null : CartItemDTO.builder()
                .id(cartItem.getId())
                .userId(cartItem.getUser() != null ? cartItem.getUser().getId() : null)
                .product(cartItem.getProduct() != null ? ProductDTO.toDTO(cartItem.getProduct()) : null)
                .quantity(cartItem.getQuantity())
                .build();
    }
}