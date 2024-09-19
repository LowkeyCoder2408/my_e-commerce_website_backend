package kimlam_do.my_e_commerce_website.model.dto;

import kimlam_do.my_e_commerce_website.model.entity.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressDTO {
    private Integer id;
    private String addressLine;
    private String province;
    private String district;
    private String ward;
    private Integer userId;
    private boolean isDefaultAddress;

    public static AddressDTO toDTO(Address address) {
        return address == null ? null : AddressDTO.builder()
                .id(address.getId())
                .addressLine(address.getAddressLine())
                .province(address.getProvince().getName())
                .district(address.getDistrict().getName())
                .ward(address.getWard().getName())
                .userId(address.getUser() != null ? address.getUser().getId() : null)
                .isDefaultAddress(address.isDefaultAddress())
                .build();
    }
}