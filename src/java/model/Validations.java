package model;

/**
 *
 * @author sjeew
 */
public class Validations {

    public static boolean isEmail(String email) {
        return email.matches("[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");
    }

    public static boolean isMobile(String mobile) {
        return mobile.matches("^[0]{1}[7]{1}[01245678]{1}[0-9]{7}$");
    }

    public static boolean isInteger(String num) {
        return num.matches("^\\d+$");
    }

    public static boolean isDouble(String num) {
        return num.matches("^\\d+(\\.\\d{2})?$");
    }

    public static boolean isPassword(String text) {
        return text.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$");
    }
}
