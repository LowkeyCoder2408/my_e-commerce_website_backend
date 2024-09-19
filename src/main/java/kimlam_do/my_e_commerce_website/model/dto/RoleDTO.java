package kimlam_do.my_e_commerce_website.model.dto;

import kimlam_do.my_e_commerce_website.model.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleDTO {
    private Integer id;
    private String name;
    private String description;
    private List<Integer> userIds;

    public static RoleDTO toDTO(Role role) {
        return role == null ? null : RoleDTO.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .userIds(role.getUsers() != null ? role.getUsers().stream()
                        .map(user -> user.getId())
                        .collect(Collectors.toList()) : null)
                .build();
    }
}