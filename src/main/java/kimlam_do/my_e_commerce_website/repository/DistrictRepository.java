package kimlam_do.my_e_commerce_website.repository;

import kimlam_do.my_e_commerce_website.model.entity.District;
import kimlam_do.my_e_commerce_website.model.entity.Province;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(exported = false)
public interface DistrictRepository extends JpaRepository<District, Integer> {
    List<District> findByProvince_Name(String provinceName);

    District findByNameAndProvince_Name(String districtName, String provinceName);

    District findByNameAndProvince(String name, Province province);
}