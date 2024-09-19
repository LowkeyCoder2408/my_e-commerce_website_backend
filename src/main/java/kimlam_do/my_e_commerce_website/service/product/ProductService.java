package kimlam_do.my_e_commerce_website.service.product;

import kimlam_do.my_e_commerce_website.model.entity.Product;
import org.springframework.data.domain.Page;

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
}