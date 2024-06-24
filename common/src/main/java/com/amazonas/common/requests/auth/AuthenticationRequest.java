package com.amazonas.common.requests.auth;

import com.amazonas.common.utils.JsonUtils;

public record AuthenticationRequest (String userId, String password) {
    public static AuthenticationRequest from(String json) {
        return JsonUtils.deserialize(json, AuthenticationRequest.class);
    }
}
