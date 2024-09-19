package kimlam_do.my_e_commerce_website.model.dto;

import kimlam_do.my_e_commerce_website.model.entity.ProductImage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductImageDTO {
    private Integer id;
    private String name;
    private String url;
    private String publicId;
    private Integer productId;

    public static ProductImageDTO toDTO(ProductImage productImage) {
        return productImage == null ? null : ProductImageDTO.builder()
                .id(productImage.getId())
                .name(productImage.getName())
                .url(productImage.getUrl())
                .publicId(productImage.getPublicId())
                .productId(productImage.getProduct() != null ? productImage.getProduct().getId() : null)
                .build();
    }
}