package kimlam_do.my_e_commerce_website.model.dto;

import kimlam_do.my_e_commerce_website.model.entity.Ward;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WardDTO {
    private Integer id;
    private String name;
    private Integer districtId;

    public static WardDTO toDTO(Ward ward) {
        return ward == null ? null : WardDTO.builder()
                .id(ward.getId())
                .name(ward.getName())
                .districtId(ward.getDistrict() != null ? ward.getDistrict().getId() : null)
                .build();
    }
}