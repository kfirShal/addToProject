package com.amazonas.common.requests.suspends;

import com.amazonas.common.requests.notifications.NotificationRequest;
import com.amazonas.common.utils.JsonUtils;

public final class SuspendedRequest {
    private final String suspendId;

    public SuspendedRequest(String suspendId) {
        this.suspendId = suspendId;
    }


    public String getSuspendId() {return suspendId;}

    public static SuspendedRequest from(String json) {
        return JsonUtils.deserialize(json, SuspendedRequest.class);
    }
}
