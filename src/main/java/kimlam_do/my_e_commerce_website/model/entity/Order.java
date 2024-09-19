package kimlam_do.my_e_commerce_website.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "product_order")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private LocalDateTime createdTime;

    @Column(name = "address_line", nullable = false, length = 64)
    private String addressLine;

    @Column(nullable = false, length = 45)
    private String province;

    @Column(nullable = false, length = 45)
    private String district;

    @Column(nullable = false, length = 45)
    private String ward;

    @Column(name = "full_name", nullable = false, length = 45)
    private String fullName;

    @Column(name = "phone_number", nullable = false, length = 15)
    private String phoneNumber;

    @Column(name = "email", length = 64)
    private String email;

    @Column(name = "total_price_product")
    private double totalPriceProduct;

    @Column(nullable = false)
    private double deliveryFee;

    @Column(nullable = false, name = "total_price")
    private double totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.NEW;

    @Column(columnDefinition = "TEXT")
    private String note;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetail> orderDetails = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "delivery_method_id")
    private DeliveryMethod deliveryMethod;

    @PrePersist
    protected void onCreate() {
        this.createdTime = LocalDateTime.now();
    }

    public void updateTotalPrice() {
        this.totalPrice = this.totalPriceProduct + this.deliveryFee;
    }
}