package kimlam_do.my_e_commerce_website.service.product;

import com.fasterxml.jackson.databind.node.ObjectNode;
import kimlam_do.my_e_commerce_website.model.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;


public interface ProductService {
    Optional<Product> getProductById(int id);

    Page<Product> getAllProducts(int page, int size, String sortBy, String sortDir);

    Page<Product> findByCurrentPriceBetween(int page, int size, String sortBy, String sortDir, BigDecimal minPrice, BigDecimal maxPrice);

    Page<Product> getProductsByCategoryId(int page, int size, String sortBy, String sortDir, int categoryId);

    Page<Product> findProductsByPriceDifferencePrice(int size);

    Page<Product> findByNameContainingAndCurrentPriceBetween(int page, int size, String sortBy, String sortDir, String productName, BigDecimal minPrice, BigDecimal maxPrice);

    Page<Product> findByCategory_AliasAndCurrentPriceBetween(int page, int size, String sortBy, String sortDir, String categoryAlias, BigDecimal minPrice, BigDecimal maxPrice);

    Page<Product> findByNameContainingAndCategory_AliasAndCurrentPriceBetween(int page, int size, String sortBy, String sortDir, String productName, String categoryAlias, BigDecimal minPrice, BigDecimal maxPrice);

    ObjectNode addAProduct(String productName, String categoryName, String brandName, int listedPrice, int currentPrice, int quantity, String operatingSystem, Optional<Float> weight, Optional<Float> length, Optional<Float> width, Optional<Float> height, String shortDescription, String fullDescription, MultipartFile mainImageFile, MultipartFile[] relatedImagesFiles) throws IOException;

    ObjectNode updateAProduct(Integer productId, String productName, String categoryName, String brandName, Optional<Integer> listedPrice, Optional<Integer> currentPrice, Optional<Integer> quantity, String operatingSystem, Optional<Float> weight, Optional<Float> length, Optional<Float> width, Optional<Float> height, String shortDescription, String fullDescription, MultipartFile mainImageFile, MultipartFile[] relatedImagesFiles) throws IOException;

    ObjectNode deleteAProduct(Integer productId) throws IOException;
}