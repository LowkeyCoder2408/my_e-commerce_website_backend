package kimlam_do.my_e_commerce_website.repository;

import kimlam_do.my_e_commerce_website.model.entity.LikedBlogComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(exported = false)
public interface LikedBlogCommentRepository extends JpaRepository<LikedBlogComment, Integer> {
    List<LikedBlogComment> findByUser_Id(int userId);

    boolean existsByBlogComment_IdAndUser_Id(int blogCommentId, Integer id);

    LikedBlogComment findByBlogComment_IdAndUser_Id(int blogId, Integer id);
}