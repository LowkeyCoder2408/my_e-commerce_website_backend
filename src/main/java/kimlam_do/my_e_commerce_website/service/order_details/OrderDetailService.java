package kimlam_do.my_e_commerce_website.service.order_details;

import kimlam_do.my_e_commerce_website.model.dto.OrderDetailDTO;

import java.util.List;

public interface OrderDetailService {
    List<OrderDetailDTO> getAllOrderDetails();
}