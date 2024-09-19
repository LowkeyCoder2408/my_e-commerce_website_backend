package kimlam_do.my_e_commerce_website.service.favorite_product;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import kimlam_do.my_e_commerce_website.model.dto.FavoriteProductDTO;

import java.util.List;

public interface FavoriteProductService {
    List<FavoriteProductDTO> getAllFavoriteProducts();

    List<FavoriteProductDTO> getFavoriteProductsByUser(Integer userId);

    ObjectNode addFavoriteProduct(JsonNode jsonNode);

    ObjectNode deleteFavoriteProduct(JsonNode jsonNode);
}