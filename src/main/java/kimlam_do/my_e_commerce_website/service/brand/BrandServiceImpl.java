package kimlam_do.my_e_commerce_website.service.brand;

import kimlam_do.my_e_commerce_website.model.entity.Brand;
import kimlam_do.my_e_commerce_website.repository.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {
    private final BrandRepository brandRepository;

    @Override
    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }
}