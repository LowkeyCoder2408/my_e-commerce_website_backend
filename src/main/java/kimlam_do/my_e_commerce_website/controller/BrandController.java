package kimlam_do.my_e_commerce_website.controller;

import kimlam_do.my_e_commerce_website.model.dto.BrandDTO;
import kimlam_do.my_e_commerce_website.model.entity.Brand;
import kimlam_do.my_e_commerce_website.service.brand.BrandService;
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
@RequestMapping("/brands")
@RequiredArgsConstructor
public class BrandController {
    private final BrandService brandService;

    @GetMapping
    public ResponseEntity<?> getAllBrands() {
        try {
            List<Brand> brands = brandService.getAllBrands();
            List<BrandDTO> brandDTOs = brands.stream()
                    .map(BrandDTO::toDTO)
                    .collect(Collectors.toList());
            return new ResponseEntity<>(brandDTOs, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Đã xảy ra lỗi khi lấy dữ liệu thương hiệu.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}