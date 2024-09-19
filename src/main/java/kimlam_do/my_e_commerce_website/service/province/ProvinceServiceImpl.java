package kimlam_do.my_e_commerce_website.service.province;

import kimlam_do.my_e_commerce_website.model.dto.ProvinceDTO;
import kimlam_do.my_e_commerce_website.model.entity.Province;
import kimlam_do.my_e_commerce_website.repository.ProvinceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProvinceServiceImpl implements ProvinceService {
    private final ProvinceRepository provinceRepository;

    @Override
    public List<ProvinceDTO> getAllProvinces() {
        List<Province> provinces = provinceRepository.findAll();
        return provinces.stream().sorted((p1, p2) -> p1.getName().compareToIgnoreCase(p2.getName())).map(ProvinceDTO::toDTO).collect(Collectors.toList());
    }
}