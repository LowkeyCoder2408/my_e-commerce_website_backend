package kimlam_do.my_e_commerce_website.repository;

import jakarta.transaction.Transactional;
import kimlam_do.my_e_commerce_website.model.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(exported = false)
public interface AddressRepository extends JpaRepository<Address, Integer> {

    List<Address> findByUser_Id(int userId);

    List<Address> findByUser(User user);

    Address findByAddressLineAndProvinceAndDistrictAndWardAndUser(String addressLine, Province province, District district, Ward ward, User user);

    @Modifying
    @Transactional
    @Query("UPDATE Address a SET a.isDefaultAddress = false WHERE a.user.id = :userId")
    public void updateIsDefaultAddressByUserId(@Param("userId") Integer userId);
}