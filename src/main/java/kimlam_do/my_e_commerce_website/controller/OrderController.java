package kimlam_do.my_e_commerce_website.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import kimlam_do.my_e_commerce_website.model.dto.OrderDTO;
import kimlam_do.my_e_commerce_website.model.entity.OrderStatus;
import kimlam_do.my_e_commerce_website.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<?> getAllOrders() {
        try {
            List<OrderDTO> orderDTOs = orderService.getAllOrders();
            return ResponseEntity.ok(orderDTOs);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Người dùng không tồn tại hoặc yêu cầu không hợp lệ");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã xảy ra lỗi trong quá trình xử lý yêu cầu lấy danh sách tất cả các đơn hàng");
        }
    }

    @GetMapping("/calculate-total-amount-by-user-id")
    public ResponseEntity<?> calculateTotalAmountByUserId(@RequestParam("userId") Integer userId) {
        Long totalAmount = orderService.calculateTotalAmountByUserId(userId);
        if (totalAmount == null) {
            totalAmount = 0L;
        }
        return ResponseEntity.ok(totalAmount);
    }

    @GetMapping("/calculate-total-amount-by-month")
    public ResponseEntity<Integer> calculateTotalAmountByMonth(
            @RequestParam("month") int month,
            @RequestParam("year") int year) {
        Integer totalAmount = orderService.calculateTotalAmountByMonth(month, year);

        if (totalAmount == null) {
            totalAmount = 0;
        }
        return ResponseEntity.ok(totalAmount);
    }

    @GetMapping("/find-by-id/{id}")
    public ResponseEntity<?> findById(@PathVariable int id) {
        try {
            OrderDTO orderDTO = orderService.findById(id);
            return ResponseEntity.ok(orderDTO);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Người dùng không tồn tại hoặc yêu cầu không hợp lệ");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã xảy ra lỗi trong quá trình xử lý yêu cầu lấy đơn hàng theo id");
        }
    }

    @GetMapping("/find-by-user")
    public ResponseEntity<?> getAllOrdersByUser(@RequestParam(value = "userId") int userId) {
        try {
            List<OrderDTO> orderDTOs = orderService.getAllOrdersByUserId(userId);
            return ResponseEntity.ok(orderDTOs);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Người dùng không tồn tại hoặc yêu cầu không hợp lệ");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã xảy ra lỗi trong quá trình xử lý yêu cầu lấy danh sách tất cả các đơn hàng của người dùng");
        }
    }

    @PostMapping("/add-order")
    public ResponseEntity<?> addAnOrder(@RequestBody JsonNode jsonData) {
        try {
            ObjectNode response = orderService.addAnOrder(jsonData);
            String status = response.get("status").asText();
            HttpStatus httpStatus = "error".equals(status) ? HttpStatus.BAD_REQUEST : HttpStatus.OK;
            return new ResponseEntity<>(response, httpStatus);
        } catch (Exception e) {
            e.printStackTrace();
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode errorResponse = mapper.createObjectNode();
            errorResponse.put("message", "Đã xảy ra lỗi khi thêm đơn hàng");
            errorResponse.put("status", "error");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/cancel-order/{orderId}")
    public ResponseEntity<ObjectNode> cancelOrder(@PathVariable Integer orderId) {
        try {
            ObjectNode response = orderService.cancelOrder(orderId);
            String status = response.get("status").asText();
            HttpStatus httpStatus = "error".equals(status) ? HttpStatus.BAD_REQUEST : HttpStatus.OK;
            return new ResponseEntity<>(response, httpStatus);
        } catch (Exception e) {
            e.printStackTrace();
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode errorResponse = mapper.createObjectNode();
            errorResponse.put("message", "Đã xảy ra lỗi khi hủy đơn hàng");
            errorResponse.put("status", "error");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/return-request/{orderId}")
    public ResponseEntity<ObjectNode> requestReturnOrderItems(@PathVariable Integer orderId) {
        try {
            ObjectNode response = orderService.requestReturnOrderItems(orderId);
            String status = response.get("status").asText();
            HttpStatus httpStatus = "error".equals(status) ? HttpStatus.BAD_REQUEST : HttpStatus.OK;
            return new ResponseEntity<>(response, httpStatus);
        } catch (Exception e) {
            e.printStackTrace();
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode errorResponse = mapper.createObjectNode();
            errorResponse.put("message", "Đã xảy ra lỗi khi yêu cầu hoàn trả đơn hàng");
            errorResponse.put("status", "error");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/status-percentage")
    public Map<String, Double> getOrderStatusPercentage() {
        return orderService.calculateOrderPercentageByStatus();
    }

    @PutMapping("/update-order")
    public ResponseEntity<ObjectNode> updateAnOrder(@RequestBody JsonNode requestBody) {
        try {
            Integer orderId = requestBody.get("orderId").asInt();
            String status = requestBody.get("status").asText();

            // Gọi service để cập nhật trạng thái đơn hàng
            ObjectNode response = orderService.updateOrderStatus(orderId, status);

            String responseStatus = response.get("status").asText();
            HttpStatus httpStatus = "error".equals(responseStatus) ? HttpStatus.BAD_REQUEST : HttpStatus.OK;
            return new ResponseEntity<>(response, httpStatus);
        } catch (Exception e) {
            e.printStackTrace();
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode errorResponse = mapper.createObjectNode();
            errorResponse.put("message", "Đã xảy ra lỗi khi cập nhật trạng thái đơn hàng");
            errorResponse.put("status", "error");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}