package kimlam_do.my_e_commerce_website.model.dto;

import kimlam_do.my_e_commerce_website.model.entity.Order;
import kimlam_do.my_e_commerce_website.model.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
    private Integer id;
    private LocalDateTime createdTime;
    private String addressLine;
    private String province;
    private String district;
    private String ward;
    private String fullName;
    private String phoneNumber;
    private String email;
    private double totalPriceProduct;
    private double deliveryFee;
    private double totalPrice;
    private String status;
    private String note;
    private List<OrderDetailDTO> orderDetails;
    private Integer userId;
    private String paymentMethodName;
    private String deliveryMethodName;

    public static OrderDTO toDTO(Order order) {
        return OrderDTO.builder()
                .id(order.getId())
                .createdTime(order.getCreatedTime())
                .addressLine(order.getAddressLine())
                .province(order.getProvince())
                .district(order.getDistrict())
                .ward(order.getWard())
                .fullName(order.getFullName())
                .phoneNumber(order.getPhoneNumber())
                .email(order.getEmail())
                .totalPriceProduct(order.getTotalPriceProduct())
                .deliveryFee(order.getDeliveryFee())
                .totalPrice(order.getTotalPrice())
                .status(order.getStatus() != null ? order.getStatus().defaultDescription() : null)
                .note(order.getNote())
                .orderDetails(order.getOrderDetails() != null ?
                        order.getOrderDetails().stream()
                                .map(OrderDetailDTO::toDTO)
                                .toList() : null)
                .userId(order.getUser() != null ? order.getUser().getId() : null)
                .paymentMethodName(order.getPaymentMethod() != null ? order.getPaymentMethod().name() : null) // Convert Enum to String
                .deliveryMethodName(order.getDeliveryMethod() != null ? order.getDeliveryMethod().getName() : null) // Assuming a `getName` method
                .build();
    }
}