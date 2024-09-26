package kimlam_do.my_e_commerce_website.service.address;

import kimlam_do.my_e_commerce_website.model.dto.AddressDTO;
import kimlam_do.my_e_commerce_website.model.entity.Address;
import kimlam_do.my_e_commerce_website.model.entity.User;

import java.util.List;

public interface AddressService {
    List<AddressDTO> getAllAddresses();

    List<AddressDTO> getAllAddressesByUserId(int userId);

    Address getDefaultAddressByUserId(int userId);
}