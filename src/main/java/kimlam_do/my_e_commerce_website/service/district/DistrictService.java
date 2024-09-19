package kimlam_do.my_e_commerce_website.service.district;

import kimlam_do.my_e_commerce_website.model.dto.DistrictDTO;

import java.util.List;

public interface DistrictService {
    List<DistrictDTO> getAllDistricts();

    List<DistrictDTO> getAllDistrictsByProvinceName(String provinceName);
}