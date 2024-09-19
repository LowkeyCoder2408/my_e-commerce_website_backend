package kimlam_do.my_e_commerce_website.service.product_image;

import kimlam_do.my_e_commerce_website.model.entity.ProductImage;
import kimlam_do.my_e_commerce_website.repository.ProductImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductImageServiceImpl implements ProductImageService {
    private final ProductImageRepository productImageRepository;

    @Override
    public List<ProductImage> getAllProductImages() {
        return productImageRepository.findAll();
    }
}