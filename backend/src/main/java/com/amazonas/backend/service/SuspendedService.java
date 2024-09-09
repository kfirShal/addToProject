package com.amazonas.backend.service;

import com.amazonas.backend.business.permissions.proxies.SuspendedProxy;
import com.amazonas.backend.exceptions.AuthenticationFailedException;
import com.amazonas.backend.exceptions.NoPermissionException;
import com.amazonas.common.dtos.Suspend;
import com.amazonas.common.requests.Request;
import com.amazonas.common.requests.notifications.NotificationRequest;
import com.amazonas.common.requests.suspends.SuspendedRequest;
import com.amazonas.common.utils.Response;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component("suspendedService")
public class SuspendedService {
    private final SuspendedProxy proxy;


    public SuspendedService(SuspendedProxy proxy) {
        this.proxy = proxy;

    }

    public String getSuspendList(String json) {
        Request request = Request.from(json);
        try {
            List<Suspend> suspendList = proxy.getSuspendList(request.userId(), request.token());
            return Response.getOk(suspendList);
        } catch (NoPermissionException | AuthenticationFailedException e) {
            return Response.getError(e);
        }
    }

    public String addSuspend(String json) {
        Request request = Request.from(json);
        try {
            SuspendedRequest suspendedRequest = SuspendedRequest.from(request.payload());
            Suspend suspend = new Suspend(suspendedRequest.getSuspendId(), suspendedRequest.getBeginDate(), suspendedRequest.getFinishDate());
            proxy.addSuspend(suspend, request.userId(), request.token());
            return Response.getOk();
        } catch (NoPermissionException | AuthenticationFailedException e) {
            return Response.getError(e);
        }
    }

    public String removeSuspend(String json) {
        Request request = Request.from(json);
        try {
            SuspendedRequest suspendedRequest = SuspendedRequest.from(request.payload());
            Suspend removeSuspend = proxy.removeSuspend(suspendedRequest.getSuspendId(), request.userId(), request.token());
            return Response.getOk(removeSuspend);
        } catch (NoPermissionException | AuthenticationFailedException e) {
            return Response.getError(e);
        }

    }

    public String isSuspended(String json) {
        Request request = Request.from(json);
        try {
            SuspendedRequest suspendedRequest = SuspendedRequest.from(request.payload());
            boolean isSuspend = proxy.isSuspended(suspendedRequest.getSuspendId(), request.userId(), request.token());
            return Response.getOk(isSuspend);
        } catch (NoPermissionException | AuthenticationFailedException e) {
            return Response.getError(e);
        }
    }

}
