package kimlam_do.my_e_commerce_website.controller;

import kimlam_do.my_e_commerce_website.model.dto.WardDTO;
import kimlam_do.my_e_commerce_website.service.ward.WardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/wards")
@RequiredArgsConstructor
public class WardController {
    private final WardService wardService;

    @GetMapping
    public ResponseEntity<?> getAllWards() {
        try {
            List<WardDTO> wardDTOs = wardService.getAllWards();
            return ResponseEntity.ok(wardDTOs);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Người dùng không tồn tại hoặc yêu cầu không hợp lệ");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã xảy ra lỗi trong quá trình xử lý yêu cầu lấy danh sách các xã");
        }
    }

    @GetMapping("/find-by-province-name-and-district-name")
    public ResponseEntity<?> getAllWardsByProvinceName(@RequestParam(value = "provinceName") String provinceName, @RequestParam(value = "districtName") String districtName) {
        try {
            List<WardDTO> wardDTOs = wardService.getAllWardsByProvinceNameAndDistrictName(provinceName, districtName);
            return ResponseEntity.ok(wardDTOs);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Người dùng không tồn tại hoặc yêu cầu không hợp lệ");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã xảy ra lỗi trong quá trình xử lý yêu cầu lấy danh sách các xã theo huyện và tỉnh");
        }
    }
}