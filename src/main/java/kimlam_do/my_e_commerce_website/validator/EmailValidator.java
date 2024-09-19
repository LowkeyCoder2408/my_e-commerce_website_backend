package kimlam_do.my_e_commerce_website.validator;

import java.util.regex.Pattern;

public class EmailValidator {
    private static final String EMAIL_REGEX = "^[\\w!#$%&'*+/=?^`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^`{|}~-]+)*@(?:[\\w-]+(?:\\.[\\w-]+)*|\\[(?:\\d{1,3}\\.){3}\\d{1,3}\\])$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    /**
     * Kiểm tra tính hợp lệ của email.
     *
     * @param email địa chỉ email cần kiểm tra
     * @return true nếu email hợp lệ, ngược lại false
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }
}