package kimlam_do.my_e_commerce_website.service.role;

import kimlam_do.my_e_commerce_website.model.entity.Role;

import java.util.List;

public interface RoleService {
    Role saveRole(Role role);

    List<Role> getAllRoles();
}