package edu.sv.ues.mv12013.splashparcial2.domain.model;

public class UserDomain {
    public final String email;
    public final String fullName;

    public UserDomain(String email, String fullName) {
        this.email = email;
        this.fullName = fullName;
    }

    public boolean hasValidName() {
        return fullName != null && fullName.trim().length() >= 3;
    }
}