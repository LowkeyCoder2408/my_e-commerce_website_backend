package kimlam_do.my_e_commerce_website.service.order;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import kimlam_do.my_e_commerce_website.model.dto.OrderDTO;
import kimlam_do.my_e_commerce_website.model.entity.*;
import kimlam_do.my_e_commerce_website.repository.*;
import kimlam_do.my_e_commerce_website.validator.EmailValidator;
import kimlam_do.my_e_commerce_website.validator.PhoneNumberValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;
    private final DeliveryMethodRepository deliveryMethodRepository;
    private final ProductRepository productRepository;
    private final ProvinceRepository provinceRepository;
    private final DistrictRepository districtRepository;
    private final WardRepository wardRepository;
    private final AddressRepository addressRepository;

    @Override
    public List<OrderDTO> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream().map(OrderDTO::toDTO).collect(Collectors.toList());
    }

    @Override
    public OrderDTO findById(int id) {
        Optional<Order> orderOptional = orderRepository.findById(id);
        if (!orderOptional.isPresent()) {
            throw new EntityNotFoundException("Không tìm thấy đơn hàng với ID: " + id);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();
        User currentUser = userRepository.findByEmail(currentUserEmail);
        if (currentUser == null) {
            throw new RuntimeException("Không tồn tại người dùng với email: " + currentUserEmail);
        }

        Integer currentUserId = currentUser.getId();
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("Quản trị hệ thống") || role.getAuthority().equals("Nhân viên bán hàng") || role.getAuthority().equals("Quản lý nội dung"));

        Order order = orderOptional.get();
        if (!isAdmin && !order.getUser().getId().equals(currentUserId)) {
            throw new AccessDeniedException("Bạn không có quyền truy cập dữ liệu này.");
        }

        return OrderDTO.toDTO(order);
    }

    @Override
    public List<OrderDTO> getAllOrdersByUserId(int userId) {
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

        List<Order> orders = orderRepository.findByUser_Id(userId);
        return orders.stream().map(OrderDTO::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ObjectNode addAnOrder(JsonNode jsonData) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();

        // Kiểm tra tính hợp lý của các tham số
        if (!jsonData.has("userId") || jsonData.get("userId").isNull()) {
            response.put("message", "Mã người dùng thiếu hoặc không hợp lệ");
            response.put("status", "error");
            return response;
        }
        Integer userId = Integer.parseInt(formatStringByJson(jsonData.get("userId").asText()));
        if (!userRepository.findById(userId).isPresent()) {
            response.put("message", "Không tồn tại người dùng với id: " + userId);
            response.put("status", "error");
            return response;
        }

        String fullName = jsonData.has("fullName") && !jsonData.get("fullName").isNull() ? formatStringByJson(jsonData.get("fullName").asText()) : null;
        if (fullName == null || fullName.isEmpty()) {
            response.put("message", "Tên người dùng thiếu hoặc không hợp lệ");
            response.put("status", "error");
            return response;
        }

        String phoneNumber = jsonData.has("phoneNumber") && !jsonData.get("phoneNumber").isNull() ? formatStringByJson(jsonData.get("phoneNumber").asText()) : null;
        if (phoneNumber == null || phoneNumber.isEmpty() || !PhoneNumberValidator.isValidPhoneNumber(phoneNumber)) {
            response.put("message", "Số điện thoại thiếu hoặc không hợp lệ");
            response.put("status", "error");
            return response;
        }

        String email = jsonData.has("email") && !jsonData.get("email").isNull() ? formatStringByJson(jsonData.get("email").asText()) : null;
        if (email == null || email.isEmpty() || !EmailValidator.isValidEmail(email)) {
            response.put("message", "Email thiếu hoặc không hợp lệ");
            response.put("status", "error");
            return response;
        }

        String addressLine = jsonData.has("addressLine") && !jsonData.get("addressLine").isNull() ? formatStringByJson(jsonData.get("addressLine").asText()) : null;
        if (addressLine == null || addressLine.isEmpty()) {
            response.put("message", "Đường dẫn địa chỉ (cụ thể) thiếu hoặc không hợp lệ");
            response.put("status", "error");
            return response;
        }

        String provinceName = jsonData.has("provinceName") && !jsonData.get("provinceName").isNull() ? formatStringByJson(jsonData.get("provinceName").asText()) : null;
        if (provinceName == null || provinceName.isEmpty()) {
            response.put("message", "Tên tỉnh/thành phố thiếu hoặc không hợp lệ");
            response.put("status", "error");
            return response;
        }
        Province province = provinceRepository.findByName(provinceName);
        if (province == null) {
            response.put("message", "Tỉnh '" + provinceName + "' không tồn tại.");
            response.put("status", "error");
            return response;
        }

        String districtName = jsonData.has("districtName") && !jsonData.get("districtName").isNull() ? formatStringByJson(jsonData.get("districtName").asText()) : null;
        if (districtName == null || districtName.isEmpty()) {
            response.put("message", "Tên huyện/quận thiếu hoặc không hợp lệ");
            response.put("status", "error");
            return response;
        }
        District district = districtRepository.findByNameAndProvince(districtName, province);
        if (district == null) {
            response.put("message", "Huyện '" + districtName + "' không tồn tại trong tỉnh '" + provinceName + "'.");
            response.put("status", "error");
            return response;
        }

        String wardName = jsonData.has("wardName") && !jsonData.get("wardName").isNull() ? formatStringByJson(jsonData.get("wardName").asText()) : null;
        if (wardName == null || wardName.isEmpty()) {
            response.put("message", "Tên xã/phường thiếu hoặc không hợp lệ");
            response.put("status", "error");
            return response;
        }
        Ward ward = wardRepository.findByNameAndDistrict(wardName, district);
        if (ward == null) {
            response.put("message", "Xã '" + wardName + "' không tồn tại trong huyện '" + districtName + "'.");
            response.put("status", "error");
            return response;
        }

        boolean isSetDefaultAddress = jsonData.has("isSetDefaultAddress") ? jsonData.get("isSetDefaultAddress").asBoolean() : false;

        boolean isUseDefaultAddress = jsonData.has("isUseDefaultAddress") ? jsonData.get("isUseDefaultAddress").asBoolean() : false;

        String deliveryMethod = jsonData.has("deliveryMethod") && !jsonData.get("deliveryMethod").isNull() ? formatStringByJson(jsonData.get("deliveryMethod").asText()) : null;
        if (deliveryMethod == null || deliveryMethod.isEmpty()) {
            response.put("message", "Hình thức giao hàng thiếu hoặc không hợp lệ");
            response.put("status", "error");
            return response;
        }
        DeliveryMethod deliveryMethodEntity = deliveryMethodRepository.findByName(deliveryMethod);
        if (deliveryMethodEntity == null) {
            response.put("message", "Hình thức giao hàng không tồn tại");
            response.put("status", "error");
            return response;
        }

        Integer deliveryFee = jsonData.has("deliveryFee") && !jsonData.get("deliveryFee").isNull() ? Integer.parseInt(formatStringByJson(jsonData.get("deliveryFee").asText())) : null;
        if (deliveryFee == null || deliveryFee < 0) {
            response.put("message", "Phí giao hàng thiếu hoặc không hợp lệ");
            response.put("status", "error");
            return response;
        }

        String paymentMethod = jsonData.has("paymentMethod") && !jsonData.get("paymentMethod").isNull() ? formatStringByJson(jsonData.get("paymentMethod").asText()) : null;
        if (paymentMethod == null || paymentMethod.isEmpty()) {
            response.put("message", "Phương thức thanh toán thiếu hoặc không hợp lệ");
            response.put("status", "error");
            return response;
        }
        if (!isValidPaymentMethod(paymentMethod)) {
            response.put("message", "Phương thức thanh toán không tồn tại");
            response.put("status", "error");
            return response;
        }

        String note = jsonData.has("note") && !jsonData.get("note").isNull() ? formatStringByJson(jsonData.get("note").asText()) : null;
        if (note != null && note.length() > 300) {
            response.put("message", "Ghi chú không được vượt quá 300 ký tự");
            response.put("status", "error");
            return response;
        }

        Integer buyNowProductId = jsonData.has("buyNowProductId") && !jsonData.get("buyNowProductId").isNull() ? jsonData.get("buyNowProductId").asInt() : null;
        Integer buyNowProductQuantity = jsonData.has("buyNowProductQuantity") && !jsonData.get("buyNowProductQuantity").isNull() ? jsonData.get("buyNowProductQuantity").asInt() : null;

        List<Integer> cartItemIds = new ArrayList<>();
        if (jsonData.has("cartItemIds") && jsonData.get("cartItemIds").isArray()) {
            for (JsonNode idNode : jsonData.get("cartItemIds")) {
                cartItemIds.add(idNode.asInt());
            }
        }

        // Kiểm tra buyNowProductId và cartItemIds
        boolean hasBuyNowProductId = buyNowProductId != null;
        boolean hasCartItemIds = !cartItemIds.isEmpty();

        if (hasBuyNowProductId && hasCartItemIds) {
            response.put("message", "Không thể kết hợp sản phẩm mua ngay và giỏ hàng");
            response.put("status", "error");
            return response;
        }

        if (!hasBuyNowProductId && !hasCartItemIds) {
            response.put("message", "Danh sách giỏ hàng không được để trống hoặc cần có sản phẩm mua ngay");
            response.put("status", "error");
            return response;
        }

        // Kiểm tra quyền truy cập
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();
        User currentUser = userRepository.findByEmail(currentUserEmail);
        if (currentUser == null) {
            response.put("message", "Không tồn tại người dùng (đang gọi API) với email: " + currentUserEmail);
            response.put("status", "error");
            return response;
        }
        Integer currentUserId = currentUser.getId();
        boolean isOrdersAdmin = authentication.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("Quản trị hệ thống") || role.getAuthority().equals("Nhân viên bán hàng"));
        if (!isOrdersAdmin && !userId.equals(currentUserId)) {
            response.put("message", "Bạn không được cấp quyền thêm đơn hàng cho người dùng có id: " + userId);
            response.put("status", "error");
            return response;
        }

        // Kiểm tra đã xong, bây giờ tiến hành tạo và lưu vào CSDL
        // Tạo đơn hàng
        Order order = new Order();
        order.setAddressLine(addressLine);
        order.setProvince(provinceName);
        order.setDistrict(districtName);
        order.setWard(wardName);
        order.setFullName(fullName);
        order.setPhoneNumber(phoneNumber);
        order.setEmail(email);
        order.setDeliveryFee(deliveryFee);
        order.setNote(note);
        order.setDeliveryMethod(deliveryMethodEntity);
        order.setPaymentMethod(PaymentMethod.valueOf(paymentMethod));
        order.setUser(userRepository.findById(userId).get());
        if (!paymentMethod.equals("COD")) {
            order.setStatus(OrderStatus.PAID);
            order.setPaidTime(LocalDateTime.now());
        } else {
            order.setStatus(OrderStatus.NEW);
        }

        // Xử lý sản phẩm mua ngay hoặc giỏ hàng
        if (hasBuyNowProductId) {
            Optional<Product> productOptional = productRepository.findById(buyNowProductId);
            if (productOptional.isPresent()) {
                Product product = productOptional.get();

                if (buyNowProductQuantity <= 0 || buyNowProductQuantity > product.getQuantity()) {
                    response.put("message", "Số lượng sản phẩm mua ngay không hợp lệ");
                    response.put("status", "error");
                    return response;
                }

                double productPrice = product.getCurrentPrice() * buyNowProductQuantity;
                order.setTotalPriceProduct(productPrice);
                order.updateTotalPrice();

                // Tạo OrderDetail cho sản phẩm mua ngay
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setQuantity(buyNowProductQuantity);
                orderDetail.setProductPriceAtOrderTime(product.getCurrentPrice());
                orderDetail.setSubtotal(productPrice * buyNowProductQuantity);
                orderDetail.setProduct(product);
                orderDetail.setOrder(order);

                // Thêm OrderDetail vào danh sách orderDetails
                List<OrderDetail> orderDetails = order.getOrderDetails();
                if (orderDetails == null) {
                    orderDetails = new ArrayList<>();
                    order.setOrderDetails(orderDetails);
                }
                orderDetails.add(orderDetail);

                // Giảm số lượng tồn kho và tăng số lượng đã bán của sản phẩm
                product.setQuantity(product.getQuantity() - buyNowProductQuantity);
                product.setSoldQuantity(product.getSoldQuantity() + buyNowProductQuantity);
                productRepository.save(product);
            } else {
                response.put("message", "Sản phẩm cần mua ngay không tồn tại");
                response.put("status", "error");
                return response;
            }
        } else {
            // Xử lý giỏ hàng
            // Tính tổng giá trị sản phẩm từ giỏ hàng
            double totalCartPrice = 0.0;
            List<OrderDetail> orderDetails = new ArrayList<>();

            for (Integer cartItemId : cartItemIds) {
                Optional<CartItem> cartItemOptional = cartItemRepository.findById(cartItemId);
                if (cartItemOptional.isPresent()) {
                    CartItem cartItem = cartItemOptional.get();
                    Product product = cartItem.getProduct();

                    // Kiểm tra số lượng
                    if (cartItem.getQuantity() <= 0 || cartItem.getQuantity() > product.getQuantity()) {
                        response.put("message", "Số lượng sản phẩm trong giỏ hàng không hợp lệ cho sản phẩm: " + product.getName());
                        response.put("status", "error");
                        return response;
                    }
                    // Tính toán giá trị sản phẩm trong giỏ hàng
                    double productPrice = product.getCurrentPrice() * cartItem.getQuantity();
                    totalCartPrice += productPrice;

                    // Cập nhật số lượng tồn kho và số lượng đã bán
                    product.setQuantity(product.getQuantity() - cartItem.getQuantity());
                    product.setSoldQuantity(product.getSoldQuantity() + cartItem.getQuantity());
                    productRepository.save(product);

                    // Tạo OrderDetail cho sản phẩm trong giỏ hàng
                    OrderDetail orderDetail = new OrderDetail();
                    orderDetail.setOrder(order);
                    orderDetail.setSubtotal(productPrice);
                    orderDetail.setQuantity(cartItem.getQuantity());
                    orderDetail.setProduct(product);
                    orderDetail.setProductPriceAtOrderTime(product.getCurrentPrice());

                    orderDetails.add(orderDetail);

                    // Xóa CartItem sau khi đã thêm vào Order
                    cartItemRepository.delete(cartItem);
                } else {
                    response.put("message", "Sản phẩm trong giỏ hàng không tồn tại");
                    response.put("status", "error");
                    return response;
                }
            }

            // Cập nhật tổng giá trị sản phẩm cho đơn hàng
            order.setTotalPriceProduct(totalCartPrice);
            order.updateTotalPrice();

            // Lưu OrderDetails
            order.setOrderDetails(orderDetails);
        }

        if (!isUseDefaultAddress) {
            Address existingAddress = addressRepository.findByAddressLineAndProvinceAndDistrictAndWardAndUser(addressLine, province, district, ward, userRepository.findById(userId).get());

            if (existingAddress != null) {
                // Đã tồn tại một địa chỉ giống nhau trong cơ sở dữ liệu
                if (isSetDefaultAddress && !existingAddress.isDefaultAddress()) {
                    // Chỉ cập nhật địa chỉ mặc định nếu địa chỉ hiện tại chưa phải là mặc định
                    addressRepository.updateIsDefaultAddressByUserId(userId);
                    existingAddress.setDefaultAddress(true);
                }
                // Cập nhật thông tin khác nếu cần
                addressRepository.save(existingAddress);
            } else {
                Address newAddress = new Address();
                newAddress.setAddressLine(addressLine);
                newAddress.setProvince(province);
                newAddress.setDistrict(district);
                newAddress.setWard(ward);
                newAddress.setUser(userRepository.findById(userId).get());

                if (isSetDefaultAddress) {
                    addressRepository.updateIsDefaultAddressByUserId(userId);
                    newAddress.setDefaultAddress(true);
                }
                addressRepository.save(newAddress);
            }
        }

        // Tạo OrderTrack
        OrderTrack orderTrack = new OrderTrack();
        orderTrack.setNote(order.getNote());
        orderTrack.setStatus(order.getStatus());
        orderTrack.setOrder(order);

        // Thêm OrderTrack vào danh sách orderTracks
        List<OrderTrack> orderTracks = order.getOrderTracks();
        if (orderTracks == null) {
            orderTracks = new ArrayList<>();
            order.setOrderTracks(orderTracks);
        }
        orderTracks.add(orderTrack);
        orderRepository.save(order);

        response.put("message", "Khởi tạo đơn hàng thành công");
        response.put("status", "success");
        return response;
    }

    @Override
    public ObjectNode cancelOrder(Integer orderId) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();

        // Lấy thông tin người dùng hiện tại
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();
        User currentUser = userRepository.findByEmail(currentUserEmail);
        if (currentUser == null) {
            response.put("message", "Không tồn tại người dùng với email: " + currentUserEmail);
            response.put("status", "error");
            return response;
        }

        // Kiểm tra xem đơn hàng có tồn tại không
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (!orderOptional.isPresent()) {
            response.put("message", "Không tồn tại đơn hàng với id: " + orderId);
            response.put("status", "error");
            return response;
        }
        Order order = orderOptional.get();

        // Kiểm tra quyền của người dùng
        if (!currentUser.getId().equals(order.getUser().getId())) {
            response.put("message", "Bạn không có quyền hủy đơn hàng này");
            response.put("status", "error");
            return response;
        }

        // Kiểm tra thời gian tạo đơn hàng
        if (order.getCreatedTime().isBefore(LocalDateTime.now().minusHours(6))) {
            response.put("message", "Không thể hủy đơn hàng sau 6 giờ kể từ thời điểm đặt hàng");
            response.put("status", "error");
            return response;
        }

        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELED || order.getStatus() == OrderStatus.PAID || order.getStatus() == OrderStatus.RETURN_REQUESTED || order.getStatus() == OrderStatus.RETURNED || order.getStatus() == OrderStatus.REFUNDED) {
            response.put("message", "Không thể hủy đơn hàng vì đang ở trạng thái \"" + order.getStatus().defaultDescription() + '"');
            response.put("status", "error");
            return response;
        }

        // LOGIC hủy đơn hàng ở đây
        order.setStatus(OrderStatus.CANCELED);

        // Hoàn trả sản phẩm về kho
        for (OrderDetail orderDetail : order.getOrderDetails()) {
            Product product = orderDetail.getProduct();
            int quantityToReturn = orderDetail.getQuantity();

            // Kiểm tra trước khi cập nhật
            if (product.getSoldQuantity() >= quantityToReturn) {
                product.setQuantity(product.getQuantity() + quantityToReturn);
                product.setSoldQuantity(product.getSoldQuantity() - quantityToReturn);
                productRepository.save(product);
            } else {
                response.put("message", "Không thể hoàn trả số lượng lớn hơn số lượng đã bán cho sản phẩm " + product.getId());
                response.put("status", "error");
                return response;
            }
        }

        // Tạo OrderTrack
        OrderTrack orderTrack = new OrderTrack();
        orderTrack.setNote(order.getNote());
        orderTrack.setStatus(OrderStatus.CANCELED);
        orderTrack.setOrder(order);

        List<OrderTrack> orderTracks = order.getOrderTracks();
        if (orderTracks == null) {
            orderTracks = new ArrayList<>();
            order.setOrderTracks(orderTracks);
        }
        orderTracks.add(orderTrack);
        orderRepository.save(order);

        response.put("message", "Hủy đơn hàng ORD-" + orderId + " thành công");
        response.put("status", "success");
        return response;
    }

    @Override
    public ObjectNode requestReturnOrderItems(Integer orderId) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();

        // Lấy thông tin người dùng hiện tại
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();
        User currentUser = userRepository.findByEmail(currentUserEmail);
        if (currentUser == null) {
            response.put("message", "Không tồn tại người dùng với email: " + currentUserEmail);
            response.put("status", "error");
            return response;
        }

        // Kiểm tra xem đơn hàng có tồn tại không
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (!orderOptional.isPresent()) {
            response.put("message", "Không tồn tại đơn hàng với id: " + orderId);
            response.put("status", "error");
            return response;
        }
        Order order = orderOptional.get();

        // Kiểm tra quyền của người dùng
        if (!currentUser.getId().equals(order.getUser().getId())) {
            response.put("message", "Bạn không có quyền yêu cầu hoàn trả sản phẩm trong đơn hàng này");
            response.put("status", "error");
            return response;
        }

        // Kiểm tra trạng thái đơn hàng
        if (order.getStatus() == OrderStatus.NEW || order.getStatus() == OrderStatus.PROCESSING || order.getStatus() == OrderStatus.PACKAGED || order.getStatus() == OrderStatus.PICKED || order.getStatus() == OrderStatus.SHIPPING || order.getStatus() == OrderStatus.CANCELED) {
            response.put("message", "Chỉ có thể yêu cầu hoàn trả cho đơn hàng đã giao hoặc đã thanh toán");
            response.put("status", "error");
            return response;
        }

        // Kiểm tra nếu đơn hàng đã được yêu cầu hoàn trả, đã hoàn trả, hoặc đã hoàn tiền
        if (order.getStatus() == OrderStatus.RETURN_REQUESTED || order.getStatus() == OrderStatus.RETURNED || order.getStatus() == OrderStatus.REFUNDED) {
            response.put("message", "Đơn hàng này đã được yêu cầu hoàn trả hoặc đã hoàn trả/hoàn tiền");
            response.put("status", "error");
            return response;
        }

        // Kiểm tra thời gian trả hàng (ví dụ: trong vòng 30 ngày từ ngày giao hàng)
        if (order.getDeliveredTime() == null) {
            response.put("message", "Đơn hàng chưa được giao, không thể yêu cầu hoàn trả");
            response.put("status", "error");
            return response;
        }

        if (order.getDeliveredTime().isBefore(LocalDateTime.now().minusDays(30))) {
            response.put("message", "Không thể yêu cầu hoàn trả sau 30 ngày kể từ khi giao hàng");
            response.put("status", "error");
            return response;
        }

        // Chuyển trạng thái đơn hàng sang RETURN_REQUESTED
        order.setStatus(OrderStatus.RETURN_REQUESTED);

        // Tạo OrderTrack cho yêu cầu hoàn trả
        OrderTrack returnRequestTrack = new OrderTrack();
        returnRequestTrack.setOrder(order);
        returnRequestTrack.setStatus(OrderStatus.RETURN_REQUESTED); // Trạng thái yêu cầu hoàn trả
        returnRequestTrack.setNote("Yêu cầu hoàn trả cho đơn hàng ORD-" + orderId);

        // Thêm OrderTrack vào danh sách orderTracks của đơn hàng
        List<OrderTrack> orderTracks = order.getOrderTracks();
        if (orderTracks == null) {
            orderTracks = new ArrayList<>();
            order.setOrderTracks(orderTracks);
        }
        orderTracks.add(returnRequestTrack);

        // Lưu trạng thái đơn hàng và các bản ghi tracking
        orderRepository.save(order);

        response.put("message", "Yêu cầu hoàn trả sản phẩm thành công");
        response.put("status", "success");
        return response;
    }

    public static String formatStringByJson(String json) {
        return json.replaceAll("\"", "");
    }

    public static boolean isValidPaymentMethod(String method) {
        for (PaymentMethod paymentMethod : PaymentMethod.values()) {
            if (paymentMethod.name().equalsIgnoreCase(method)) {
                return true;
            }
        }
        return false;
    }
}