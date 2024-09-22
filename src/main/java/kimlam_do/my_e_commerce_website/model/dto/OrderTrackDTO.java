package kimlam_do.my_e_commerce_website.model.dto;

import kimlam_do.my_e_commerce_website.model.entity.OrderTrack;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderTrackDTO {
    private Integer id;
    private String note;
    private LocalDateTime updatedTime;
    private String status;
    private Integer orderId;

    public static OrderTrackDTO toDTO(OrderTrack orderTrack) {
        return orderTrack == null ? null : OrderTrackDTO.builder()
                .id(orderTrack.getId())
                .note(orderTrack.getNote())
                .updatedTime(orderTrack.getUpdatedTime())
                .status(orderTrack.getStatus() != null ? orderTrack.getStatus().defaultDescription() : null)
                .orderId(orderTrack.getOrder() != null ? orderTrack.getOrder().getId() : null)
                .build();
    }
}