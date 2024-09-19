package kimlam_do.my_e_commerce_website.service.product;

import kimlam_do.my_e_commerce_website.model.entity.Product;
import kimlam_do.my_e_commerce_website.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    @Override
    public Optional<Product> getProductById(int id) {
        return productRepository.findById(id);
    }

    @Override
    public Page<Product> getAllProducts(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return productRepository.findAll(pageable);
    }

    @Override
    public Page<Product> findByCurrentPriceBetween(int page, int size, String sortBy, String sortDir, BigDecimal minPrice, BigDecimal maxPrice) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return productRepository.findByCurrentPriceBetween(minPrice, maxPrice, pageable);
    }

    @Override
    public Page<Product> getProductsByCategoryId(int page, int size, String sortBy, String sortDir, int categoryId) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return productRepository.findByCategory_Id(categoryId, pageable);
    }

    @Override
    public Page<Product> findProductsByPriceDifferencePrice(int size) {
        Pageable pageable = PageRequest.of(0, size);
        return productRepository.findProductsByPriceDifferencePrice(pageable);
    }

    @Override
    public Page<Product> findByNameContainingAndCurrentPriceBetween(int page, int size, String sortBy, String sortDir, String productName, BigDecimal minPrice, BigDecimal maxPrice) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return productRepository.findByNameContainingAndCurrentPriceBetween(productName, minPrice, maxPrice, pageable);
    }

    @Override
    public Page<Product> findByCategory_AliasAndCurrentPriceBetween(int page, int size, String sortBy, String sortDir, String categoryAlias, BigDecimal minPrice, BigDecimal maxPrice) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return productRepository.findByCategory_AliasAndCurrentPriceBetween(categoryAlias, minPrice, maxPrice, pageable);
    }

    @Override
    public Page<Product> findByNameContainingAndCategory_AliasAndCurrentPriceBetween(int page, int size, String sortBy, String sortDir, String productName, String categoryAlias, BigDecimal minPrice, BigDecimal maxPrice) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return productRepository.findByNameContainingAndCategory_AliasAndCurrentPriceBetween(productName, categoryAlias, minPrice, maxPrice, pageable);
    }
}