package kimlam_do.my_e_commerce_website.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, length = 256, nullable = false)
    private String name;

//    @Column(unique = true, length = 256, nullable = false)
//    private String alias;

    @Column(nullable = false, name = "short_description", columnDefinition = "TEXT")
    private String shortDescription;

    @Column(name = "full_description", columnDefinition = "TEXT")
    private String fullDescription;

    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    private boolean enabled;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "soldQuantity")
    private int soldQuantity;

    @Column(name = "listed_price", nullable = false)
    private int listedPrice;

    @Column(name = "current_price", nullable = false)
    private int currentPrice;

    @Column(name = "discount_percent")
    private int discountPercent;

    private float length;

    private float width;

    private float height;

    private float weight;

    @Column(name = "operating_system")
    private String operatingSystem;

    @Column(name = "main_image", nullable = false)
    private String mainImage;

    @Column(name = "main_image_public_id")
    private String mainImagePublicId;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "brand_id")
    private Brand brand;

    private int ratingCount;

    private float averageRating;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<FavoriteProduct> favoriteProducts = new ArrayList<>();

    public void addReview(Review review) {
        this.reviews.add(review);
        updateRatingStatistics();
    }

    public void updateReview(Review updatedReview) {
        // Tìm đánh giá cũ dựa trên ID
        Optional<Review> existingReviewOptional = reviews.stream().filter(review -> review.getId().equals(updatedReview.getId())).findFirst();

        if (existingReviewOptional.isPresent()) {
            Review existingReview = existingReviewOptional.get();
            // Cập nhật thông tin của đánh giá
            existingReview.setRating(updatedReview.getRating());
            existingReview.setContent(updatedReview.getContent());

            // Cập nhật thông tin đánh giá trong danh sách
            reviews.remove(existingReview);
            reviews.add(existingReview);
            updateRatingStatistics();
        } else {
            throw new RuntimeException("Không tồn tại đánh giá với ID: " + updatedReview.getId());
        }
    }

    public void removeReview(Review review) {
        this.reviews.remove(review);
        updateRatingStatistics();
    }

    private void updateRatingStatistics() {
        int totalRating = 0;
        int count = reviews.size();

        for (Review review : reviews) {
            totalRating += review.getRating();
        }

        if (count > 0) {
            this.averageRating = (float) totalRating / count;
        } else {
            this.averageRating = 0;
        }
        this.ratingCount = count;
    }
}