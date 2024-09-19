package kimlam_do.my_e_commerce_website.repository;

import kimlam_do.my_e_commerce_website.model.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.Optional;

@RepositoryRestResource(exported = false)
public interface ProductRepository extends JpaRepository<Product, Integer> {
    public Optional<Product> findById(Integer productId);

    // Tìm kiếm theo 1 yếu tố
    Page<Product> findByCategory_Id(@RequestParam("categoryId") int categoryId, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.currentPrice >= :minPrice AND p.currentPrice <= :maxPrice")
    Page<Product> findByCurrentPriceBetween(@RequestParam("minPrice") BigDecimal minPrice, @RequestParam("maxPrice") BigDecimal maxPrice, Pageable pageable);

    // Tìm kiếm theo 2 yếu tố
    Page<Product> findByNameContainingAndCurrentPriceBetween(@RequestParam("productName") String productName, @RequestParam("minPrice") BigDecimal minPrice, @RequestParam("maxPrice") BigDecimal maxPrice, Pageable pageable);

    Page<Product> findByCategory_AliasAndCurrentPriceBetween(@RequestParam("categoryAlias") String categoryAlias, @RequestParam("minPrice") BigDecimal minPrice, @RequestParam("maxPrice") BigDecimal maxPrice, Pageable pageable);

    // Tìm kiếm theo 3 yếu tố
    Page<Product> findByNameContainingAndCategory_AliasAndCurrentPriceBetween(@RequestParam("productName") String productName, @RequestParam("categoryAlias") String categoryAlias, @RequestParam("minPrice") BigDecimal minPrice, @RequestParam("maxPrice") BigDecimal maxPrice, Pageable pageable);

    @Query("SELECT p FROM Product p ORDER BY (p.listedPrice - p.currentPrice) DESC")
    Page<Product> findProductsByPriceDifferencePrice(Pageable pageable);
}