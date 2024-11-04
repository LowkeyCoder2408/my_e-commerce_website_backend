package kimlam_do.my_e_commerce_website.service.order;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import kimlam_do.my_e_commerce_website.model.dto.OrderDTO;
import kimlam_do.my_e_commerce_website.model.entity.OrderStatus;

import java.util.List;
import java.util.Map;

public interface OrderService {
    List<OrderDTO> getAllOrders();

    ObjectNode addAnOrder(JsonNode jsonData);

    List<OrderDTO> getAllOrdersByUserId(int userId);

    OrderDTO findById(int id);

    ObjectNode cancelOrder(Integer orderId);

    ObjectNode requestReturnOrderItems(Integer orderId);

    Long calculateTotalAmountByUserId(Integer userId);

    Integer calculateTotalAmountByMonth(int month, int year);

    Map<String, Double> calculateOrderPercentageByStatus();
}