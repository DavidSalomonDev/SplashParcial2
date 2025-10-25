package edu.sv.ues.mv12013.splashparcial2.core;

import android.util.Patterns;

public class ValidationUtils {
    public static boolean isValidEmail(String email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    public static boolean isValidPassword(String pass) {
        if (pass == null || pass.length() < 8) return false;
        return pass.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$");
    }
}