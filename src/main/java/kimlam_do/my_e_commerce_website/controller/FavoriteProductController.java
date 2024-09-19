package kimlam_do.my_e_commerce_website.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import kimlam_do.my_e_commerce_website.model.dto.FavoriteProductDTO;
import kimlam_do.my_e_commerce_website.model.entity.FavoriteProduct;
import kimlam_do.my_e_commerce_website.service.favorite_product.FavoriteProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/favorite-products")
@RequiredArgsConstructor
public class FavoriteProductController {
    private final FavoriteProductService favoriteProductService;

    @GetMapping
    public ResponseEntity<?> getAllFavoriteProducts() {
        try {
            List<FavoriteProductDTO> favoriteProductDTOs = favoriteProductService.getAllFavoriteProducts();
            return ResponseEntity.ok(favoriteProductDTOs);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Người dùng không tồn tại hoặc yêu cầu không hợp lệ");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã xảy ra lỗi trong quá trình xử lý yêu cầu lấy danh sách sản phẩm yêu thích");
        }
    }

    @GetMapping("/find-by-user")
    public ResponseEntity<?> getAllFavoriteProductByUserId(@RequestParam(value = "userId") int userId) {
        try {
            List<FavoriteProductDTO> favoriteProductTOs = favoriteProductService.getFavoriteProductsByUser(userId);
            return ResponseEntity.ok(favoriteProductTOs);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Người dùng không tồn tại hoặc yêu cầu không hợp lệ");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã xảy ra lỗi trong quá trình xử lý yêu cầu lấy danh sách sản phẩm yêu thích");
        }
    }

    @PostMapping("/add-favorite-product")
    public ResponseEntity<ObjectNode> addFavoriteProduct(@RequestBody JsonNode jsonNode) {
        try {
            ObjectNode response = favoriteProductService.addFavoriteProduct(jsonNode);
            String status = response.get("status").asText();
            HttpStatus httpStatus = "error".equals(status) ? HttpStatus.BAD_REQUEST : HttpStatus.OK;
            return new ResponseEntity<>(response, httpStatus);
        } catch (Exception e) {
            e.printStackTrace();
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode errorResponse = mapper.createObjectNode();
            errorResponse.put("message", "Đã xảy ra lỗi trong quá trình yêu thích sản phẩm");
            errorResponse.put("status", "error");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete-favorite-product")
    public ResponseEntity<ObjectNode> deleteFavoriteProduct(@RequestBody JsonNode jsonNode) {
        try {
            ObjectNode response = favoriteProductService.deleteFavoriteProduct(jsonNode);
            String status = response.get("status").asText();
            HttpStatus httpStatus = "error".equals(status) ? HttpStatus.BAD_REQUEST : HttpStatus.OK;
            return new ResponseEntity<>(response, httpStatus);
        } catch (Exception e) {
            e.printStackTrace();
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode errorResponse = mapper.createObjectNode();
            errorResponse.put("message", "Đã xảy ra lỗi trong quá trình bỏ yêu thích sản phẩm");
            errorResponse.put("status", "error");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}