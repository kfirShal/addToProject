package com.amazonas.common.requests.suspends;

import com.amazonas.common.requests.notifications.NotificationRequest;
import com.amazonas.common.utils.JsonUtils;

public final class SuspendedRequest {
    private final String suspendId;
    private final String beginDate;
    private final String finishDate;

    public SuspendedRequest(String suspendId, String beginDate, String finishDate) {
        this.suspendId = suspendId;
        this.beginDate = beginDate;
        this.finishDate = finishDate;
    }

    //only suspend id
    public SuspendedRequest(String suspendId){
        this( suspendId, "", "");
    }

    public SuspendedRequest(){
        this("", "", "");
    }

    public String getSuspendId() {return suspendId;}

    public String getBeginDate() {return beginDate;}

    public String getFinishDate() {return finishDate;}

    public static SuspendedRequest from(String json) {
        return JsonUtils.deserialize(json, SuspendedRequest.class);
    }
}
