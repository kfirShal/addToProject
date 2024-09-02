package com.amazonas.backend.service;

import com.amazonas.backend.business.permissions.proxies.NotificationProxy;
import com.amazonas.backend.business.permissions.proxies.SuspendedProxy;
import com.amazonas.backend.exceptions.AuthenticationFailedException;
import com.amazonas.backend.exceptions.NoPermissionException;
import com.amazonas.backend.exceptions.NotificationException;
import com.amazonas.common.requests.Request;
import com.amazonas.common.requests.notifications.NotificationRequest;
import com.amazonas.common.utils.Response;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component("suspendedService")
public class SuspendedService {
    private final SuspendedProxy proxy;


    public SuspendedService(SuspendedProxy proxy) {
       this.proxy = proxy;

    }

    public static List<String> getSuspendList(String json) {
        Request request = Request.from(json);
        try {
            NotificationRequest toSend = NotificationRequest.from(request.payload());
            proxy.getSuspendList(request.userId(), request.token());
            return Response.getOk();
        } catch (NoPermissionException | AuthenticationFailedException e) {
            return Response.getError(e);
        }
    }

    public void addSuspend(String json){
        Request request = Request.from(json);
        suspendList.add(id);
    }

    public boolean removeSuspend(String json){
        Request request = Request.from(json);
        return suspendList.remove(id);

    }

    public boolean isIDInList(String json){
        Request request = Request.from(json);
        return suspendList.contains(id);
    }
}
