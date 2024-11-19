package kimlam_do.my_e_commerce_website.model.dto;

import kimlam_do.my_e_commerce_website.model.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    private Integer id;
    private String name;
    private String shortDescription;
    private String fullDescription;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
    private boolean enabled;
    private int quantity;
    private int soldQuantity;
    private int listedPrice;
    private int currentPrice;
    private int discountPercent;
    private float length;
    private float width;
    private float height;
    private float weight;
    private String operatingSystem;
    private String mainImage;
    private String mainImagePublicId;
    private CategoryDTO category;
    private BrandDTO brand;
    private int ratingCount;
    private float averageRating;
    private List<ProductImageDTO> images;
    private List<ReviewDTO> reviews;

    public static ProductDTO toDTO(Product product) {
        return product == null ? null : ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .shortDescription(product.getShortDescription())
                .fullDescription(product.getFullDescription())
                .createdTime(product.getCreatedTime())
                .updatedTime(product.getUpdatedTime())
                .enabled(product.isEnabled())
                .quantity(product.getQuantity())
                .soldQuantity(product.getSoldQuantity())
                .listedPrice(product.getListedPrice())
                .currentPrice(product.getCurrentPrice())
                .discountPercent(product.getDiscountPercent())
                .length(product.getLength())
                .width(product.getWidth())
                .height(product.getHeight())
                .weight(product.getWeight())
                .operatingSystem(product.getOperatingSystem())
                .mainImage(product.getMainImage())
                .mainImagePublicId(product.getMainImagePublicId())
                .category(product.getCategory() != null ? CategoryDTO.toDTO(product.getCategory()) : null)
                .brand(product.getBrand() != null ? BrandDTO.toDTO(product.getBrand()) : null)
                .images(product.getImages() != null ? product.getImages().stream()
                        .map(ProductImageDTO::toDTO)
                        .collect(Collectors.toList()) : null)
                .ratingCount(product.getRatingCount())
                .averageRating(product.getAverageRating())
                .reviews(product.getReviews() != null ? product.getReviews().stream().map(ReviewDTO::toDTO).collect(Collectors.toList()) : null)
                .build();
    }
}