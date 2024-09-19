package kimlam_do.my_e_commerce_website.controller;

import kimlam_do.my_e_commerce_website.model.dto.AddressDTO;
import kimlam_do.my_e_commerce_website.model.entity.Address;
import kimlam_do.my_e_commerce_website.service.address.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/addresses")
@RequiredArgsConstructor
public class AddressController {
    private final AddressService addressService;

    @GetMapping
    public ResponseEntity<?> getAllAddresses() {
        try {
            System.out.println("SECRET_KEYS: " + System.getenv("SECRET_KEYS"));
            List<AddressDTO> addressDTOs = addressService.getAllAddresses();
            return ResponseEntity.ok(addressDTOs);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Người dùng không tồn tại hoặc yêu cầu không hợp lệ");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã xảy ra lỗi trong quá trình xử lý yêu cầu lấy danh sách tất cả các địa chỉ");
        }
    }

    @GetMapping("/find-by-user-id")
    public ResponseEntity<?> getAllAddressesByUserId(@RequestParam(value = "userId") int userId) {
        try {
            List<AddressDTO> addressDTOs = addressService.getAllAddressesByUserId(userId);
            return ResponseEntity.ok(addressDTOs);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Người dùng không tồn tại hoặc yêu cầu không hợp lệ");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã xảy ra lỗi trong quá trình xử lý yêu cầu lấy danh sách các địa chỉ theo id người dùng");
        }
    }

    @GetMapping("/find-default-address-by-user-id")
    public ResponseEntity<?> getDefaultAddressByUserId(@RequestParam(value = "userId") int userId) {
        try {
            Address address = addressService.getDefaultAddressByUserId(userId);
            if (address == null) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(AddressDTO.toDTO(address));
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Người dùng không tồn tại hoặc yêu cầu không hợp lệ");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã xảy ra lỗi trong quá trình xử lý yêu cầu lấy địa chỉ mặc định theo id người dùng");
        }
    }
}