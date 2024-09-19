package kimlam_do.my_e_commerce_website.service.delivery_method;

import kimlam_do.my_e_commerce_website.model.entity.DeliveryMethod;
import kimlam_do.my_e_commerce_website.repository.DeliveryMethodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeliveryMethodServiceImpl implements DeliveryMethodService {
    private final DeliveryMethodRepository deliveryMethodRepository;

    @Override
    public List<DeliveryMethod> getAllDeliveryMethods() {
        return deliveryMethodRepository.findAll();
    }
}