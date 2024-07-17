package com.amazonas.common.requests.users;

import com.amazonas.common.utils.JsonUtils;

import java.time.LocalDate;

public record RegisterRequest(String email, String userid, String password, LocalDate birthDate) {
    public static RegisterRequest from(String json) {
        return JsonUtils.deserialize(json, RegisterRequest.class);
    }
}
