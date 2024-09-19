package kimlam_do.my_e_commerce_website.model.dto;

import kimlam_do.my_e_commerce_website.model.entity.District;
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
public class DistrictDTO {
    private Integer id;
    private String name;
    private Integer provinceId;
    private List<WardDTO> wards;

    public static DistrictDTO toDTO(District district) {
        return district == null ? null : DistrictDTO.builder()
                .id(district.getId())
                .name(district.getName())
                .provinceId(district.getProvince() != null ? district.getProvince().getId() : null)
                .wards(district.getWards() != null ? district.getWards().stream().map(WardDTO::toDTO).collect(Collectors.toList()) : null)
                .build();
    }
}