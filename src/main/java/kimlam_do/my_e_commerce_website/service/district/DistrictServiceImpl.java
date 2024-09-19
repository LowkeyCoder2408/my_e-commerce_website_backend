package kimlam_do.my_e_commerce_website.service.district;

import kimlam_do.my_e_commerce_website.model.dto.DistrictDTO;
import kimlam_do.my_e_commerce_website.model.entity.District;
import kimlam_do.my_e_commerce_website.repository.DistrictRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DistrictServiceImpl implements DistrictService {
    private final DistrictRepository districtRepository;

    @Override
    public List<DistrictDTO> getAllDistricts() {
        List<District> districts = districtRepository.findAll();
        return districts.stream().sorted((d1, d2) -> d1.getName().compareToIgnoreCase(d2.getName())).map(DistrictDTO::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<DistrictDTO> getAllDistrictsByProvinceName(String provinceName) {
        List<District> districts = districtRepository.findByProvince_Name(provinceName);
        return districts.stream().sorted((d1, d2) -> d1.getName().compareToIgnoreCase(d2.getName())).map(DistrictDTO::toDTO).collect(Collectors.toList());
    }
}