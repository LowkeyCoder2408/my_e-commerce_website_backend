package kimlam_do.my_e_commerce_website.model.dto;

import kimlam_do.my_e_commerce_website.model.entity.Role;
import kimlam_do.my_e_commerce_website.model.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Integer id;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String photo;
    private LocalDateTime createdTime;
    private boolean enabled;
    private List<String> roles;
    private String authenticationType;

    public static UserDTO toDTO(User user) {
        return user == null ? null : UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .photo(user.getPhoto())
                .createdTime(user.getCreatedTime())
                .enabled(user.isEnabled())
                .roles(user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toList()))
                .authenticationType(user.getAuthenticationType().name())
                .build();
    }
}