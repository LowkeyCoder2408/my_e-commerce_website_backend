package kimlam_do.my_e_commerce_website.repository;

import kimlam_do.my_e_commerce_website.model.entity.Role;
import kimlam_do.my_e_commerce_website.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource(exported = false)
public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByEmail(String email);

    public User findByEmail(String email);

    User findByResetPasswordToken(String token);

    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r.name = :roleName")
    long countByRoleName(String roleName);
}