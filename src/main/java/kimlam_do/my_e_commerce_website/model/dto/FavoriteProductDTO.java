package kimlam_do.my_e_commerce_website.model.dto;

import kimlam_do.my_e_commerce_website.model.entity.FavoriteProduct;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FavoriteProductDTO {
    private Integer id;
    private Integer productId;
    private Integer userId;

    public static FavoriteProductDTO toDTO(FavoriteProduct favoriteProduct) {
        return favoriteProduct == null ? null : FavoriteProductDTO.builder()
                .id(favoriteProduct.getId())
                .productId(favoriteProduct.getProduct() != null ? favoriteProduct.getProduct().getId() : null)
                .userId(favoriteProduct.getUser() != null ? favoriteProduct.getUser().getId() : null)
                .build();
    }
}