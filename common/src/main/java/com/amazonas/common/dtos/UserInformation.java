package com.amazonas.common.dtos;

import java.time.LocalDate;

public class UserInformation {
    private final String userId;
    private final String email;
    private final LocalDate birthDate;

    public UserInformation(String userId, String email, LocalDate birthDate) {
        this.userId = userId;
        this.email = email;
        this.birthDate = birthDate;
    }

    public String getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserInformation that = (UserInformation) o;

        if (!userId.equals(that.userId)) return false;
        if (!email.equals(that.email)) return false;
        return birthDate.equals(that.birthDate);
    }

    @Override
    public int hashCode() {
        int result = userId.hashCode();
        result = 31 * result + email.hashCode();
        result = 31 * result + birthDate.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "UserInformation{" +
                "userId='" + userId + '\'' +
                ", email='" + email + '\'' +
                ", birthDate=" + birthDate +
                '}';
    }
}
