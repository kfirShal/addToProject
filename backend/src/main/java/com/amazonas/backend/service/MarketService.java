package com.amazonas.backend.service;

import com.amazonas.backend.business.permissions.proxies.MarketProxy;
import com.amazonas.backend.exceptions.AuthenticationFailedException;
import com.amazonas.backend.exceptions.NoPermissionException;
import com.amazonas.common.requests.Request;
import com.amazonas.common.utils.Response;
import org.springframework.stereotype.Component;

@Component("marketService")
public class MarketService {

    private final MarketProxy proxy;

    public MarketService(MarketProxy marketProxy) {
        proxy = marketProxy;
    }

    public String startMarket(String json) {
        Request request = Request.from(json);
        try {
            proxy.start(request.userId(), request.token());
            return Response.getOk();
        } catch (AuthenticationFailedException | NoPermissionException e) {
            return Response.getError(e);
        }
    }

    public String shutdown(String json) {
        Request request = Request.from(json);
        try {
            proxy.shutdown(request.userId(), request.token());
            return Response.getOk();
        } catch (AuthenticationFailedException | NoPermissionException e) {
            return Response.getError(e);
        }
    }
}
