package kimlam_do.my_e_commerce_website.repository;

import kimlam_do.my_e_commerce_website.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource(exported = false)
public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByEmail(String email);

    public User findByEmail(String email);

    User findByResetPasswordToken(String token);
}