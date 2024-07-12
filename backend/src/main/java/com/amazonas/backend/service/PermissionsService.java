package com.amazonas.backend.service;

import com.amazonas.common.permissions.profiles.PermissionsProfile;
import com.amazonas.backend.business.permissions.proxies.PermissionsProxy;
import com.amazonas.backend.exceptions.AuthenticationFailedException;
import com.amazonas.common.requests.Request;
import com.amazonas.common.utils.Response;
import org.springframework.stereotype.Component;

@Component("permissionsService")
public class PermissionsService {

    private final PermissionsProxy proxy;

    public PermissionsService(PermissionsProxy permissionsProxy) {
        this.proxy = permissionsProxy;
    }

    public String getUserPermissions(String json){
        Request request = Request.from(json);
        try{
            PermissionsProfile profile = proxy.getUserPermissions(request.userId(), request.token());
            return Response.getOk(profile);
        } catch (IllegalArgumentException | AuthenticationFailedException e){
            return Response.getError(e);
        }
    }

    public String getGuestPermissions(String json){
        try{
            Request request = Request.from(json);
            PermissionsProfile profile = proxy.getGuestPermissions(request.userId(), request.token());
            return Response.getOk(profile);
        } catch (IllegalArgumentException | AuthenticationFailedException e){
            return Response.getError(e);
        }
    }

    public String isAdmin(String json){
        try{
            Request request = Request.from(json);
            boolean isAdmin = proxy.isAdmin(request.userId(), request.token());
            return Response.getOk(isAdmin);
        } catch (IllegalArgumentException | AuthenticationFailedException e){
            return Response.getError(e);
        }
    }
}
