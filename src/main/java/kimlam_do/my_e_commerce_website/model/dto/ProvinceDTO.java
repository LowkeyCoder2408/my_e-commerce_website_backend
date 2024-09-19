package kimlam_do.my_e_commerce_website.model.dto;

import kimlam_do.my_e_commerce_website.model.entity.Province;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProvinceDTO {
    private Integer id;
    private String name;
    private List<DistrictDTO> districts;

    public static ProvinceDTO toDTO(Province province) {
        return province == null ? null : ProvinceDTO.builder()
                .id(province.getId())
                .name(province.getName())
                .districts(province.getDistricts() != null ? province.getDistricts().stream().map(DistrictDTO::toDTO).collect(Collectors.toList()) : null)
                .build();
    }
}