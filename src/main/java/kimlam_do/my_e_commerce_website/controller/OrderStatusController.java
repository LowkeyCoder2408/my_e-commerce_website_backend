package kimlam_do.my_e_commerce_website.controller;

import kimlam_do.my_e_commerce_website.model.dto.OrderStatusDTO;
import kimlam_do.my_e_commerce_website.model.entity.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/order-status")
@RequiredArgsConstructor
public class OrderStatusController {
    @GetMapping("/descriptions")
    public List<OrderStatusDTO> getAllOrderStatusDescriptions() {
        List<OrderStatusDTO> descriptions = new ArrayList<>();

        for (OrderStatus status : OrderStatus.values()) {
            descriptions.add(new OrderStatusDTO(status.name(), status.defaultDescription()));
        }

        return descriptions;
    }
}