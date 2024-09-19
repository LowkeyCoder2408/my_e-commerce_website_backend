package kimlam_do.my_e_commerce_website.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.http.HttpServletRequest;
import kimlam_do.my_e_commerce_website.service.vn_pay.VNPayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/vn-pay")
@RequiredArgsConstructor
public class VNPayController {
    private final VNPayService vnPayService;

    @PostMapping("/create-payment")
    public ResponseEntity<?> createPayment(HttpServletRequest request, @RequestParam("amount") long amountRequest) {
        try {
            String paymentUrl = vnPayService.createPaymentUrl(request, amountRequest);
            return ResponseEntity.status(HttpStatus.OK).body(paymentUrl);
        } catch (UnsupportedEncodingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating payment URL");
        }
    }

    @GetMapping("/payment-result")
    public ResponseEntity<ObjectNode> paymentSuccess(@RequestParam(value = "vnp_ResponseCode") String status) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();
        response.put("result", vnPayService.getPaymentResult(status));
        return ResponseEntity.ok(response);
    }
}