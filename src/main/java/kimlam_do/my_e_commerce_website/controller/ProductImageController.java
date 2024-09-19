package kimlam_do.my_e_commerce_website.controller;

import kimlam_do.my_e_commerce_website.model.dto.ProductImageDTO;
import kimlam_do.my_e_commerce_website.model.entity.ProductImage;
import kimlam_do.my_e_commerce_website.service.product_image.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/product-images")
@RequiredArgsConstructor
public class ProductImageController {
    private final ProductImageService productImageService;

    @GetMapping
    public ResponseEntity<?> getAllProductImages() {
        try {
            List<ProductImage> productImages = productImageService.getAllProductImages();
            List<ProductImageDTO> productImageDTOs = productImages.stream()
                    .map(ProductImageDTO::toDTO)
                    .collect(Collectors.toList());
            return new ResponseEntity<>(productImageDTOs, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Đã xảy ra lỗi khi lấy dữ liệu ảnh sản phẩm.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}