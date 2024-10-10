package kimlam_do.my_e_commerce_website.repository;

import kimlam_do.my_e_commerce_website.model.entity.BlogCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource(exported = false)
public interface BlogCategoryRepository extends JpaRepository<BlogCategory, Integer> {
    Optional<BlogCategory> findByName(String blogCategoryName);
}