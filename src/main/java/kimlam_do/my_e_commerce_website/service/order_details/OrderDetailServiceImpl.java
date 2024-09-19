package kimlam_do.my_e_commerce_website.service.order_details;

import kimlam_do.my_e_commerce_website.model.dto.OrderDetailDTO;
import kimlam_do.my_e_commerce_website.model.entity.OrderDetail;
import kimlam_do.my_e_commerce_website.repository.OrderDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderDetailServiceImpl implements OrderDetailService {
    private final OrderDetailRepository orderDetailRepository;

    @Override
    public List<OrderDetailDTO> getAllOrderDetails() {
        List<OrderDetail> orderDetails = orderDetailRepository.findAll();
        return orderDetails.stream().map(OrderDetailDTO::toDTO).collect(Collectors.toList());
    }
}