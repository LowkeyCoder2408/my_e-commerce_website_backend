package kimlam_do.my_e_commerce_website.repository;

import kimlam_do.my_e_commerce_website.model.entity.Order;
import kimlam_do.my_e_commerce_website.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(exported = false)
public interface OrderRepository extends JpaRepository<Order, Integer> {

    List<Order> findByUser_Id(int userId);

    @Query("SELECT SUM(o.totalPrice) FROM Order o WHERE o.user.id = ?1")
    Long calculateTotalAmountByUserId(Integer userId);

    @Query("SELECT DISTINCT o.user FROM Order o")
    Page<User> findDistinctUsers(Pageable pageable);

    @Query("SELECT SUM(o.totalPrice) FROM Order o WHERE MONTH(o.createdTime) = :month AND YEAR(o.createdTime) = :year")
    Integer calculateTotalAmountByMonth(@Param("month") int month, @Param("year") int year);

    @Query("SELECT o.status, COUNT(o) FROM Order o GROUP BY o.status")
    List<Object[]> countOrdersByStatus();
}