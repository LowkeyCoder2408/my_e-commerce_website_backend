package kimlam_do.my_e_commerce_website.validator;

import java.util.regex.Pattern;

public class PhoneNumberValidator {
    // Biểu thức chính quy cho số điện thoại Việt Nam
    private static final String PHONE_NUMBER_REGEX = "(84|0[3|5|7|8|9])[0-9]{8}\\b";
    private static final Pattern PHONE_NUMBER_PATTERN = Pattern.compile(PHONE_NUMBER_REGEX);

    /**
     * Kiểm tra tính hợp lệ của số điện thoại.
     *
     * @param phoneNumber số điện thoại cần kiểm tra
     * @return true nếu số điện thoại hợp lệ, ngược lại false
     */
    public static boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return false;
        }
        return PHONE_NUMBER_PATTERN.matcher(phoneNumber).matches();
    }
}