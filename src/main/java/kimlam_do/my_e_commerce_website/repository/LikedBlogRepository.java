package kimlam_do.my_e_commerce_website.repository;

import kimlam_do.my_e_commerce_website.model.entity.LikedBlog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(exported = false)
public interface LikedBlogRepository extends JpaRepository<LikedBlog, Integer> {
    List<LikedBlog> findByUser_Id(int userId);

    LikedBlog findByBlog_IdAndUser_Id(int blogId, Integer id);

    boolean existsByBlog_IdAndUser_Id(int blogId, Integer id);
}