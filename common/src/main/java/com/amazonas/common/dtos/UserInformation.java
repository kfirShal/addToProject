package com.amazonas.common.dtos;

import java.time.LocalDate;

public record UserInformation(String userId, String email, LocalDate birthDate) {
}
