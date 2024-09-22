package kimlam_do.my_e_commerce_website.service.address;

import kimlam_do.my_e_commerce_website.model.dto.AddressDTO;
import kimlam_do.my_e_commerce_website.model.entity.*;
import kimlam_do.my_e_commerce_website.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final ProvinceRepository provinceRepository;
    private final DistrictRepository districtRepository;
    private final WardRepository wardRepository;

//    @Override
//    public Address createAddress(String addressLine, String wardName, String districtName, String provinceName, User user, boolean isDefaultAddress) {
//        // Tìm kiếm Province
//        Province province = provinceRepository.findByName(provinceName);
//        if (province == null) {
//            throw new IllegalArgumentException("Tỉnh '" + provinceName + "' không tồn tại.");
//        }
//
//        // Tìm kiếm District theo Province
//        District district = districtRepository.findByNameAndProvince(districtName, province);
//        if (district == null) {
//            throw new IllegalArgumentException("Huyện '" + districtName + "' không tồn tại trong tỉnh '" + provinceName + "'.");
//        }
//
//        // Tìm kiếm Ward theo District
//        Ward ward = wardRepository.findByNameAndDistrict(wardName, district);
//        if (ward == null) {
//            throw new IllegalArgumentException("Xã '" + wardName + "' không tồn tại trong huyện '" + districtName + "'.");
//        }
//
//        // Kiểm tra xem địa chỉ đã tồn tại hay chưa
//        List<Address> existingAddresses = addressRepository.findByUser(user);
//        for (Address existingAddress : existingAddresses) {
//            if (existingAddress.getWard().equals(ward) &&
//                    existingAddress.getDistrict().equals(district) &&
//                    existingAddress.getProvince().equals(province) &&
//                    existingAddress.getAddressLine().equals(addressLine)) {
//                return existingAddress;
//            }
//        }
//
//        // Nếu địa chỉ mới là địa chỉ mặc định, cập nhật tất cả các địa chỉ hiện tại của người dùng về isDefaultAddress = false
//        if (isDefaultAddress) {
//            for (Address userAddress : existingAddresses) {
//                if (userAddress.isDefaultAddress()) {
//                    userAddress.setDefaultAddress(false);
//                    addressRepository.save(userAddress);
//                }
//            }
//        }
//
//        // Tạo Address nếu tất cả thông tin đều hợp lệ
//        Address address = new Address();
//        address.setWard(ward);
//        address.setDistrict(district);
//        address.setProvince(province);
//        address.setUser(user);
//        address.setDefaultAddress(isDefaultAddress);
//        address.setAddressLine(addressLine);
//
//        // Lưu vào cơ sở dữ liệu
//        return addressRepository.save(address);
//    }

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