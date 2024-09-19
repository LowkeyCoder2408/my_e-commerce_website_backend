package kimlam_do.my_e_commerce_website.service.vn_pay;

import jakarta.servlet.http.HttpServletRequest;

import java.io.UnsupportedEncodingException;

public interface VNPayService {
    String createPaymentUrl(HttpServletRequest request, long amountRequest) throws UnsupportedEncodingException;

    String getPaymentResult(String status);
}