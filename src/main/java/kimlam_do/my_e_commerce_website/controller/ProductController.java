package kimlam_do.my_e_commerce_website.controller;

import kimlam_do.my_e_commerce_website.model.dto.PaginatedResponse;
import kimlam_do.my_e_commerce_website.model.dto.ProductDTO;
import kimlam_do.my_e_commerce_website.model.entity.Product;
import kimlam_do.my_e_commerce_website.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public ResponseEntity<?> getAllProducts(@RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "size", defaultValue = "20") int size, @RequestParam(value = "sortBy", defaultValue = "id") String sortBy, @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir) {
        try {
            Page<Product> productPage = productService.getAllProducts(page, size, sortBy, sortDir);
            List<ProductDTO> productDTOs = productPage.getContent().stream().map(ProductDTO::toDTO).collect(Collectors.toList());
            PaginatedResponse<ProductDTO> response = new PaginatedResponse<>(productDTOs, productPage.getTotalPages(), productPage.getTotalElements());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã xảy ra lỗi khi lấy dữ liệu sản phẩm.");
        }
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getProductById(@PathVariable("id") int id) {
        try {
            Optional<Product> optionalProduct = productService.getProductById(id);
            if (!optionalProduct.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sản phẩm không tồn tại.");
            }
            ProductDTO productDTO = ProductDTO.toDTO(optionalProduct.get());
            return ResponseEntity.ok(productDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã xảy ra lỗi khi lấy dữ liệu sản phẩm theo id.");
        }
    }

    @GetMapping("/findByCurrentPriceBetween")
    public ResponseEntity<?> findByCurrentPriceBetween(@RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "size", defaultValue = "20") int size, @RequestParam(value = "sortBy", defaultValue = "id") String sortBy, @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir, @RequestParam(value = "minPrice", required = false) BigDecimal minPrice, @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice) {
        try {
            Page<Product> productPage = productService.findByCurrentPriceBetween(page, size, sortBy, sortDir, minPrice, maxPrice);
            List<ProductDTO> productDTOs = productPage.getContent().stream().map(ProductDTO::toDTO).collect(Collectors.toList());
            PaginatedResponse<ProductDTO> response = new PaginatedResponse<>(productDTOs, productPage.getTotalPages(), productPage.getTotalElements());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã xảy ra lỗi khi lấy dữ liệu sản phẩm theo khoảng giá.");
        }
    }

    @GetMapping("/findByCategoryId")
    public ResponseEntity<?> findByCategoryId(@RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "size", defaultValue = "20") int size, @RequestParam(value = "sortBy", defaultValue = "id") String sortBy, @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir, @RequestParam(value = "categoryId") int categoryId) {
        try {
            Page<Product> productPage = productService.getProductsByCategoryId(page, size, sortBy, sortDir, categoryId);
            List<ProductDTO> productDTOs = productPage.stream().map(ProductDTO::toDTO).collect(Collectors.toList());
            PaginatedResponse<ProductDTO> response = new PaginatedResponse<>(productDTOs, productPage.getTotalPages(), productPage.getTotalElements());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Đã xảy ra lỗi khi lấy dữ liệu sản phẩm bằng mã danh mục.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/findProductsByPriceDifferencePrice")
    public ResponseEntity<?> findProductsByPriceDifferencePrice(@RequestParam(value = "size", defaultValue = "20") int size) {
        try {
            Page<Product> productPage = productService.findProductsByPriceDifferencePrice(size);
            List<ProductDTO> productDTOs = productPage.stream().map(ProductDTO::toDTO).collect(Collectors.toList());
            PaginatedResponse<ProductDTO> response = new PaginatedResponse<>(productDTOs, productPage.getTotalPages(), productPage.getTotalElements());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Đã xảy ra lỗi khi lấy dữ liệu sản phẩm theo khoảng giá.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/findByNameContainingAndCurrentPriceBetween")
    public ResponseEntity<?> findByNameContainingAndCurrentPriceBetween(@RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "size", defaultValue = "20") int size, @RequestParam(value = "sortBy", defaultValue = "id") String sortBy, @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir, @RequestParam(value = "productName") String productName, @RequestParam(value = "minPrice") BigDecimal minPrice, @RequestParam(value = "maxPrice") BigDecimal maxPrice) {
        try {
            productName = productName.trim();
            Page<Product> productPage = productService.findByNameContainingAndCurrentPriceBetween(page, size, sortBy, sortDir, productName, minPrice, maxPrice);
            List<ProductDTO> productDTOs = productPage.stream().map(ProductDTO::toDTO).collect(Collectors.toList());
            PaginatedResponse<ProductDTO> response = new PaginatedResponse<>(productDTOs, productPage.getTotalPages(), productPage.getTotalElements());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Đã xảy ra lỗi khi lấy dữ liệu sản phẩm theo tên và khoảng giá.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/findByCategory_AliasAndCurrentPriceBetween")
    public ResponseEntity<?> findByCategory_AliasAndCurrentPriceBetween(@RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "size", defaultValue = "20") int size, @RequestParam(value = "sortBy", defaultValue = "id") String sortBy, @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir, @RequestParam(value = "categoryAlias") String categoryAlias, @RequestParam(value = "minPrice") BigDecimal minPrice, @RequestParam(value = "maxPrice") BigDecimal maxPrice) {
        try {
            Page<Product> productPage = productService.findByCategory_AliasAndCurrentPriceBetween(page, size, sortBy, sortDir, categoryAlias, minPrice, maxPrice);
            List<ProductDTO> productDTOs = productPage.stream().map(ProductDTO::toDTO).collect(Collectors.toList());
            PaginatedResponse<ProductDTO> response = new PaginatedResponse<>(productDTOs, productPage.getTotalPages(), productPage.getTotalElements());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Đã xảy ra lỗi khi lấy dữ liệu sản phẩm theo tên danh mục và khoảng giá.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/findByNameContainingAndCategory_AliasAndCurrentPriceBetween")
    public ResponseEntity<?> findByNameContainingAndCategory_AliasAndCurrentPriceBetween(@RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "size", defaultValue = "20") int size, @RequestParam(value = "sortBy", defaultValue = "id") String sortBy, @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir, @RequestParam(value = "productName") String productName, @RequestParam(value = "categoryAlias") String categoryAlias, @RequestParam(value = "minPrice") BigDecimal minPrice, @RequestParam(value = "maxPrice") BigDecimal maxPrice) {
        try {
            productName = productName.trim();
            Page<Product> productPage = productService.findByNameContainingAndCategory_AliasAndCurrentPriceBetween(page, size, sortBy, sortDir, productName, categoryAlias, minPrice, maxPrice);
            List<ProductDTO> productDTOs = productPage.stream().map(ProductDTO::toDTO).collect(Collectors.toList());
            PaginatedResponse<ProductDTO> response = new PaginatedResponse<>(productDTOs, productPage.getTotalPages(), productPage.getTotalElements());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Đã xảy ra lỗi khi lấy dữ liệu sản phẩm theo tên, tên danh mục và khoảng giá.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}