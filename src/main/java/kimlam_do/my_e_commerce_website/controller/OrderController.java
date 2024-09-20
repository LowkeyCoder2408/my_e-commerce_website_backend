package kimlam_do.my_e_commerce_website.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import kimlam_do.my_e_commerce_website.model.dto.OrderDTO;
import kimlam_do.my_e_commerce_website.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<?> addAnOrder() {
        System.out.println("Đã gọi add order lúc: " + LocalDateTime.now());
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();
        response.put("message", "Đã gọi add order lúc: " + LocalDateTime.now());
        response.put("status", "success");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}