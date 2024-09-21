package kimlam_do.my_e_commerce_website.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import kimlam_do.my_e_commerce_website.model.dto.OrderDTO;
import kimlam_do.my_e_commerce_website.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

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
}