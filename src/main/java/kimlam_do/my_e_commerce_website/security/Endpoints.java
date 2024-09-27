package kimlam_do.my_e_commerce_website.security;

public class Endpoints {
    public static final String front_end_host = "http://localhost:3000";

    public static final String[] PUBLIC_GET_ENDPOINTS = {
            "/products/**",
            "/brands/**",
            "/categories/**",
            "/product-images/**",
            "/reviews/**",
            "/users/existsByEmail/**",
            "/roles/**",
            "/auth/enable",
            "/provinces",
            "/districts",
            "/wards",
            "/districts/find-by-province-name/**",
            "/wards/find-by-province-name-and-district-name/**",
            "/delivery-methods",
            "/vn-pay/**",
            // Authorization is handled directly in functions
            "/cart-items/find-by-user/**",
            "/favorite-products/find-by-user/**",
            "/reviews/find-by-user-id-and-product-id/**",
            "/users/{userId}",
            "/addresses/find-by-user-id/**",
            "/addresses/default-address/find-by-user-id/**",
            "/orders/find-by-id/{id}"
    };

    public static final String[] PUBLIC_POST_ENDPOINTS = {
            "/auth/register",
            "/auth/login",
            "/auth/login/**",
            "/vn-pay/create-payment/**",

            // Authorization is handled directly in functions
            "/cart-items/add-item",
            "/favorite-products/add-favorite-product",
            "/reviews/add-review",
            "/orders/add-order"
    };

    public static final String[] PUBLIC_PUT_ENDPOINTS = {
            // Authorization is handled directly in functions
            "/cart-items/update-item",
            "/reviews/update-review",
            "/orders/cancel-order/{orderId}",
            "/orders/return-request/{orderId}"
    };

    public static final String[] PUBLIC_DELETE_ENDPOINTS = {
            // Authorization is handled directly in functions
            "/cart-items/delete-item/**",
            "/favorite-products/delete-favorite-product",
            "/reviews/delete-review/**",
    };

    public static final String[] EMPLOYEE_ENDPOINTS = {
            "/cart-items/**",
    };

    public static final String[] CONTENT_ADMIN_ENDPOINTS = {
            "/addresses",
    };

    public static final String[] SYSTEM_ADMIN_ENDPOINTS = {
            "/users",
    };

    public static final String[] COMMON_ADMIN_ENDPOINTS = {
            "/favorite-products/**",
            "/orders/**",
            "/order-details/**",
    };
}