package kimlam_do.my_e_commerce_website.service.ward;

import kimlam_do.my_e_commerce_website.model.dto.WardDTO;

import java.util.List;

public interface WardService {
    List<WardDTO> getAllWards();

    List<WardDTO> getAllWardsByProvinceNameAndDistrictName(String provinceName, String districtName);
}