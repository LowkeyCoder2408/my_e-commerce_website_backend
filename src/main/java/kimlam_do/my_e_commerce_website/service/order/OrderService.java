package kimlam_do.my_e_commerce_website.service.order;

import kimlam_do.my_e_commerce_website.model.dto.OrderDTO;

import java.util.List;

public interface OrderService {
    List<OrderDTO> getAllOrders();
}