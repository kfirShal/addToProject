package com.amazonas.common.requests.users;

import com.amazonas.common.utils.JsonUtils;

public record LoginRequest(String guestInitialId, String userId) {
    public static LoginRequest from(String json) {
        return JsonUtils.deserialize(json, LoginRequest.class);
    }
}
