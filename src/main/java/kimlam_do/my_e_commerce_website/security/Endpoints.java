package kimlam_do.my_e_commerce_website.security;

public class Endpoints {
    public static final String front_end_host = "http://localhost:3000";

    public static final String[] PUBLIC_GET_ENDPOINTS = {
            "/products/**",
            "/brands/**",
            "/categories/**",
            "/product-images/**",
            "/reviews/**",
            "/blogs/**",
            "/blog-comments/**",
            "/blog-categories/**",
            "/users/customers",
            "/users/existsByEmail/**",
            "/users/check-current-password/**",
            "/roles/**",
            "/auth/enable",
            "/provinces",
            "/districts",
            "/wards",
            "/districts/find-by-province-name/**",
            "/wards/find-by-province-name-and-district-name/**",
            "/delivery-methods",
            "/roles",
            "/vn-pay/**",
            // Authorization is handled directly in functions
            "/cart-items/find-by-user/**",
            "/favorite-products/find-by-user/**",
            "/liked-blogs/find-by-user/**",
            "/reviews/find-by-user-id-and-product-id/**",
            "/users/{userId}",
            "/addresses/find-by-user-id/**",
            "/addresses/default-address/find-by-user-id/**",
            "/orders/find-by-id/{id}",
            "/orders/find-by-user/**",
            "/order-status/descriptions",
    };

    public static final String[] PUBLIC_POST_ENDPOINTS = {
            "/auth/register",
            "/auth/login",
            "/auth/login/**",
            "/vn-pay/create-payment/**",
            "/blogs/add-blog",

            // Authorization is handled directly in functions
            "/users/reset-password",
            "/cart-items/add-item",
            "/favorite-products/add-favorite-product",
            "/reviews/add-review",
            "/orders/add-order",
            "/liked-blogs/like-blog",
            "/liked-blog-comments/like-comment",
            "/blog-comments/add-comment",
            "/users/add-user",
    };

    public static final String[] PUBLIC_PUT_ENDPOINTS = {
            // Authorization is handled directly in functions
            "/users/forgot-password",
            "/users/change-password",
            "/users/change-avatar",
            "/users/change-information",
            "/cart-items/update-item",
            "/reviews/update-review",
            "/orders/cancel-order/{orderId}",
            "/orders/return-request/{orderId}",
            "/blog-comments/update-comment",
            "/users/update-user",
    };

    public static final String[] PUBLIC_DELETE_ENDPOINTS = {
            // Authorization is handled directly in functions
            "/cart-items/delete-item/**",
            "/favorite-products/delete-favorite-product",
            "/reviews/delete-review/**",
            "/liked-blog-comments/unlike-comment",
            "/blog-comments/delete-comment/**"
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
            "/users/administrator",
            "/favorite-products/**",
            "/orders/**",
            "/order-details/**",
            "/products/add-product",
            "/products/update-product",
            "/products/delete-product",
    };
}