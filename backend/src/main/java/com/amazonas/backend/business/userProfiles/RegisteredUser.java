package com.amazonas.backend.business.userProfiles;
import java.time.LocalDate;

public class RegisteredUser extends User{


    private String email;
    private LocalDate birthDate;


    public RegisteredUser(String userId, String email, LocalDate birthDate){
        super(userId);
        this.email = email;
        this.birthDate = birthDate;
    }

    @Override
    public String getUserId() {
        return super.getUserId();
    }

    public String getEmail() {
        return email;
    }

    public LocalDate getBirthDate() {return birthDate.plusDays(0); }
}

