package kimlam_do.my_e_commerce_website.repository;

import kimlam_do.my_e_commerce_website.model.entity.BlogComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface BlogCommentRepository extends JpaRepository<BlogComment, Integer> {
}