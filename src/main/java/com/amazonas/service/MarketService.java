package com.amazonas.service;

import com.amazonas.business.permissions.proxies.MarketProxy;
import com.amazonas.exceptions.AuthenticationFailedException;
import com.amazonas.exceptions.NoPermissionException;
import com.amazonas.service.requests.Request;
import com.amazonas.utils.Response;
import org.springframework.stereotype.Component;

@Component
public class MarketService {

    private final MarketProxy proxy;

    public MarketService(MarketProxy marketProxy) {
        proxy = marketProxy;
    }

    public String startMarket(String json) {
        Request request = Request.from(json);
        try {
            proxy.start(request.userId(), request.token());
            return new Response(true).toJson();
        } catch (AuthenticationFailedException | NoPermissionException e) {
            return Response.getErrorResponse(e).toJson();
        }
    }

    public String shutdown(String json) {
        Request request = Request.from(json);
        try {
            proxy.shutdown(request.userId(), request.token());
            return new Response(true).toJson();
        } catch (AuthenticationFailedException | NoPermissionException e) {
            return Response.getErrorResponse(e).toJson();
        }
    }
}
