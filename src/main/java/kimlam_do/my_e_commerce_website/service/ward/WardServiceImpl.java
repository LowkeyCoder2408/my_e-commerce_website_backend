package kimlam_do.my_e_commerce_website.service.ward;

import kimlam_do.my_e_commerce_website.model.dto.WardDTO;
import kimlam_do.my_e_commerce_website.model.entity.District;
import kimlam_do.my_e_commerce_website.model.entity.Ward;
import kimlam_do.my_e_commerce_website.repository.DistrictRepository;
import kimlam_do.my_e_commerce_website.repository.WardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WardServiceImpl implements WardService {
    private final WardRepository wardRepository;
    private final DistrictRepository districtRepository;

    @Override
    public List<WardDTO> getAllWards() {
        List<Ward> wards = wardRepository.findAll();
        return wards.stream().sorted((w1, w2) -> w1.getName().compareToIgnoreCase(w2.getName())).map(WardDTO::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<WardDTO> getAllWardsByProvinceNameAndDistrictName(String provinceName, String districtName) {
        District district = districtRepository.findByNameAndProvince_Name(districtName, provinceName);
        List<Ward> wards = wardRepository.findByDistrict_Id(district.getId());
        return wards.stream().sorted((w1, w2) -> w1.getName().compareToIgnoreCase(w2.getName())).map(WardDTO::toDTO).collect(Collectors.toList());
    }
}