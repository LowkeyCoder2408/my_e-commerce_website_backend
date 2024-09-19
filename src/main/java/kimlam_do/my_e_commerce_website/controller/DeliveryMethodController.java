package kimlam_do.my_e_commerce_website.controller;

import kimlam_do.my_e_commerce_website.model.entity.DeliveryMethod;
import kimlam_do.my_e_commerce_website.service.delivery_method.DeliveryMethodService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/delivery-methods")
@RequiredArgsConstructor
public class DeliveryMethodController {
    private final DeliveryMethodService deliveryMethodService;

    @GetMapping
    public ResponseEntity<?> getAllDeliveryMethods() {
        try {
            List<DeliveryMethod> deliveryMethods = deliveryMethodService.getAllDeliveryMethods();
            return ResponseEntity.ok(deliveryMethods);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Người dùng không tồn tại hoặc yêu cầu không hợp lệ");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã xảy ra lỗi trong quá trình xử lý yêu cầu lấy danh sách tất cả hình thức giao hàng");
        }
    }
}