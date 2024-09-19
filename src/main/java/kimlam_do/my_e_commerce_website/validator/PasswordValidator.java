package kimlam_do.my_e_commerce_website.validator;

import java.util.regex.Pattern;

public class PasswordValidator {

    private static final Pattern UPPERCASE_PATTERN = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile("[a-z]");
    private static final Pattern DIGIT_PATTERN = Pattern.compile("[0-9]");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[#?!@$%^&*-]");

    /**
     * Kiểm tra mật khẩu có chứa chữ cái viết hoa hay không.
     *
     * @param password mật khẩu cần kiểm tra
     * @return true nếu chứa chữ cái viết hoa, ngược lại false
     */
    public static boolean hasUppercase(String password) {
        return UPPERCASE_PATTERN.matcher(password).find();
    }

    /**
     * Kiểm tra mật khẩu có chứa chữ cái viết thường hay không.
     *
     * @param password mật khẩu cần kiểm tra
     * @return true nếu chứa chữ cái viết thường, ngược lại false
     */
    public static boolean hasLowercase(String password) {
        return LOWERCASE_PATTERN.matcher(password).find();
    }

    /**
     * Kiểm tra mật khẩu có chứa số hay không.
     *
     * @param password mật khẩu cần kiểm tra
     * @return true nếu chứa số, ngược lại false
     */
    public static boolean hasDigit(String password) {
        return DIGIT_PATTERN.matcher(password).find();
    }

    /**
     * Kiểm tra mật khẩu có chứa ký tự đặc biệt hay không.
     *
     * @param password mật khẩu cần kiểm tra
     * @return true nếu chứa ký tự đặc biệt, ngược lại false
     */
    public static boolean hasSpecialChar(String password) {
        return SPECIAL_CHAR_PATTERN.matcher(password).find();
    }

    /**
     * Kiểm tra mật khẩu có độ dài tối thiểu không.
     *
     * @param password  mật khẩu cần kiểm tra
     * @param minLength độ dài tối thiểu
     * @return true nếu mật khẩu có độ dài tối thiểu, ngược lại false
     */
    public static boolean hasMinLength(String password, int minLength) {
        return password.length() >= minLength;
    }
}