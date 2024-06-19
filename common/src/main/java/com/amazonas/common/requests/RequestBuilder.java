package com.amazonas.common.requests;

import com.amazonas.common.utils.JsonUtils;

public class RequestBuilder {
    String userId;
    String token;
    String payload;

    private RequestBuilder() {
    }

    public RequestBuilder withUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public RequestBuilder withToken(String token) {
        this.token = token;
        return this;
    }

    public <T> RequestBuilder withPayload(T payload) {
        this.payload = JsonUtils.serialize(payload);
        return this;
    }

    public Request build() {
        return new Request(userId, token, payload);
    }

    public static RequestBuilder create() {
        return new RequestBuilder();
    }
}
