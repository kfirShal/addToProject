package com.amazonas.common.requests.users;

import com.amazonas.common.utils.JsonUtils;

public record RegisterRequest(String email, String userid, String password) {
    public static RegisterRequest from(String json) {
        return JsonUtils.deserialize(json, RegisterRequest.class);
    }
}
