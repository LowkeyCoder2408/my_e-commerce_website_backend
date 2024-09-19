package kimlam_do.my_e_commerce_website.model.dto;

import kimlam_do.my_e_commerce_website.model.entity.OrderDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailDTO {
    private Integer id;
    private int quantity;
    private double productPriceAtOrderTime;
    private double subtotal;
    private Integer productId;
    private Integer orderId;
    public static OrderDetailDTO toDTO(OrderDetail orderDetail) {
        return OrderDetailDTO.builder()
                .id(orderDetail.getId())
                .quantity(orderDetail.getQuantity())
                .productPriceAtOrderTime(orderDetail.getProductPriceAtOrderTime())
                .subtotal(orderDetail.getSubtotal())
                .productId(orderDetail.getProduct() != null ? orderDetail.getProduct().getId() : null)
                .orderId(orderDetail.getOrder() != null ? orderDetail.getOrder().getId() : null)
                .build();
    }
}