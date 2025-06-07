package me.zuif.doordeluxe.validator;

import me.zuif.doordeluxe.config.WebSecurityConfig;

import java.util.regex.Pattern;

public class InputValidator {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9-]+(\\.[A-Za-z]{2,})+$"
    );


    private WebSecurityConfig securityConfig;

    public static boolean validateEmail(String email) {
        if (email == null) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean validatePassword(String password) {
        if (password == null) {
            return false;
        }
        int length = password.length();
        return length >= 8 && length <= 256;
    }
}