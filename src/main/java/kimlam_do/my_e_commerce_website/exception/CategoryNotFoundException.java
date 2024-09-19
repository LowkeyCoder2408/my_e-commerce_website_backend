package kimlam_do.my_e_commerce_website.exception;

public class CategoryNotFoundException extends RuntimeException {
    public CategoryNotFoundException(String alias) {
        super("Không tìm thấy danh mục có alias là: " + alias);
    }
}