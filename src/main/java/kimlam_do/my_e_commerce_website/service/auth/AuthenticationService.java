package kimlam_do.my_e_commerce_website.service.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import kimlam_do.my_e_commerce_website.auth.AuthenticationRequest;

public interface AuthenticationService {
    ObjectNode authenticate(AuthenticationRequest authenticationRequest);

    ObjectNode registerUser(JsonNode jsonData);

    ObjectNode enableUserAccount(String email, String verificationCode);
}