package com.amazonas.service.requests;

import com.amazonas.common.utils.JsonUtils;

public record Request(String userId, String token, String payload) {

    public static Request from(String json){
        return JsonUtils.deserialize(json, Request.class);
    }

    public String toJson(){
        return JsonUtils.serialize(this);
    }
}
