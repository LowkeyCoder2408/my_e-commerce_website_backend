package kimlam_do.my_e_commerce_website.service.address;

import kimlam_do.my_e_commerce_website.model.dto.AddressDTO;
import kimlam_do.my_e_commerce_website.model.entity.Address;
import kimlam_do.my_e_commerce_website.model.entity.User;
import kimlam_do.my_e_commerce_website.repository.AddressRepository;
import kimlam_do.my_e_commerce_website.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @Override
    public List<AddressDTO> getAllAddresses() {
        List<Address> addresses = addressRepository.findAll();
        return addresses.stream().map(AddressDTO::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<AddressDTO> getAllAddressesByUserId(int userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();
        User currentUser = userRepository.findByEmail(currentUserEmail);
        if (currentUser == null) {
            throw new RuntimeException("Không tồn tại người dùng với email: " + currentUserEmail);
        }
        Integer currentUserId = currentUser.getId();
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("Quản trị hệ thống") || role.getAuthority().equals("Nhân viên bán hàng") || role.getAuthority().equals("Quản lý nội dung"));
        if (!isAdmin && userId != currentUserId) {
            throw new AccessDeniedException("Bạn không có quyền truy cập dữ liệu này.");
        }
        List<Address> addresses = addressRepository.findByUser_Id(userId);
        return addresses.stream().map(AddressDTO::toDTO).collect(Collectors.toList());
    }

    @Override
    public Address getDefaultAddressByUserId(int userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();
        User currentUser = userRepository.findByEmail(currentUserEmail);
        if (currentUser == null) {
            throw new RuntimeException("Không tồn tại người dùng với email: " + currentUserEmail);
        }
        Integer currentUserId = currentUser.getId();
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("Quản trị hệ thống") || role.getAuthority().equals("Nhân viên bán hàng") || role.getAuthority().equals("Quản lý nội dung"));
        if (!isAdmin && userId != currentUserId) {
            throw new AccessDeniedException("Bạn không có quyền truy cập dữ liệu này.");
        }
        List<Address> addresses = addressRepository.findByUser_Id(userId);
        return addresses.stream().filter(Address::isDefaultAddress).findFirst().orElse(null);
    }
}