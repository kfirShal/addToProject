package com.amazonas.backend.service;

import com.amazonas.backend.business.permissions.proxies.SuspendedProxy;
import com.amazonas.backend.exceptions.AuthenticationFailedException;
import com.amazonas.backend.exceptions.NoPermissionException;
import com.amazonas.common.requests.Request;
import com.amazonas.common.requests.notifications.NotificationRequest;
import com.amazonas.common.requests.suspends.SuspendedRequest;
import com.amazonas.common.utils.Response;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("suspendedService")
public class SuspendedService {
    private final SuspendedProxy proxy;


    public SuspendedService(SuspendedProxy proxy) {
       this.proxy = proxy;

    }

    public String getSuspendList(String json) {
        Request request = Request.from(json);
        try {
            List<String> suspendList = proxy.getSuspendList(request.userId(), request.token());
            return Response.getOk(suspendList);
        } catch (NoPermissionException | AuthenticationFailedException e) {
            return Response.getError(e);
        }
    }

    public String addSuspend(String json){
        Request request = Request.from(json);
        try {
            SuspendedRequest suspendedRequest = SuspendedRequest.from(request.payload());
            proxy.addSuspend(suspendedRequest.getSuspendId(), request.userId(), request.token());
            return Response.getOk();
        } catch (NoPermissionException | AuthenticationFailedException e) {
            return Response.getError(e);
        }
    }

    public String removeSuspend(String json){
        Request request = Request.from(json);
        try {
            SuspendedRequest suspendedRequest = SuspendedRequest.from(request.payload());
            boolean isRemove  = proxy.removeSuspend(suspendedRequest.getSuspendId(), request.userId(), request.token());
            return Response.getOk(isRemove);
        } catch (NoPermissionException | AuthenticationFailedException e) {
            return Response.getError(e);
        }

    }

    public String isIDInList(String json){
        Request request = Request.from(json);
        try {
            SuspendedRequest suspendedRequest = SuspendedRequest.from(request.payload());
            boolean isSuspend = proxy.isIDInList(suspendedRequest.getSuspendId(), request.userId(), request.token());
            return Response.getOk(isSuspend);
        } catch (NoPermissionException | AuthenticationFailedException e) {
            return Response.getError(e);
        }
    }
}
