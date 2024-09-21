package com.amazonas.acceptanceTests;
import com.amazonas.backend.business.permissions.proxies.SuspendedProxy;
import com.amazonas.backend.exceptions.*;
import com.amazonas.backend.service.SuspendedService;
import com.amazonas.common.dtos.Suspend;
import com.amazonas.common.requests.Request;
import com.amazonas.common.requests.suspends.SuspendedRequest;
import com.amazonas.common.utils.JsonUtils;
import com.amazonas.common.utils.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class SuspendsAcceptanceTests {

    @Mock
    private SuspendedProxy suspendedProxy;

    @InjectMocks
    private SuspendedService suspendedService;
    @BeforeEach
    void setUp() {
        // Manually create a mock for UsersController
        suspendedProxy = mock(SuspendedProxy.class);

        suspendedService = new SuspendedService(suspendedProxy);
    }


    @Test
    void addSuspendSuccess() throws AuthenticationFailedException, NoPermissionException {

        String suspendId = "suspendId";
        String beginDate = "19/09/24";
        String finishDate = "27/09/24";

        SuspendedRequest suspendedRequest = new SuspendedRequest(suspendId, beginDate, finishDate);
        Request request = new Request("userId","token",JsonUtils.serialize(suspendedRequest));
        Suspend suspend = new Suspend(suspendId, beginDate, finishDate);
        doNothing().when(suspendedProxy).addSuspend(suspend, request.userId(), request.token());
        String result = suspendedService.addSuspend(request.toJson());
        assertEquals(Response.getOk(), result);
    }

    @Test
    void addSuspendFailure() throws AuthenticationFailedException, NoPermissionException{

        String suspendId = "suspendId";
        String beginDate = "19/09/24";
        String finishDate = "27/09/24";

        SuspendedRequest suspendedRequest = new SuspendedRequest(suspendId, beginDate, finishDate);
        Request request = new Request("userId","token",JsonUtils.serialize(suspendedRequest));
        Suspend suspend = new Suspend(suspendId, beginDate, finishDate);
        doThrow(new NoPermissionException("No Permission")).when(suspendedProxy).addSuspend(suspend, request.userId(), request.token());
        String result = suspendedService.addSuspend(request.toJson());
        assertEquals(Response.getError(new NoPermissionException("No Permission")), result);
    }


    @Test
    void removeSuspendSuccess() throws AuthenticationFailedException, NoPermissionException {

        String suspendId = "suspendId";
        String beginDate = "19/09/24";
        String finishDate = "27/09/24";

        SuspendedRequest suspendedRequest = new SuspendedRequest(suspendId, null, null);
        Request request = new Request("userId","token",JsonUtils.serialize(suspendedRequest));
        Suspend suspend = new Suspend(suspendId, beginDate, finishDate);

        when(suspendedProxy.removeSuspend(suspendId, request.userId(), request.token())).thenReturn(suspend);
        String result = suspendedService.removeSuspend(request.toJson());
        assertEquals(Response.getOk(suspend), result);
    }

    @Test
    void removeSuspendFailure() throws AuthenticationFailedException, NoPermissionException{

        String suspendId = "suspendId";
        String beginDate = "19/09/24";
        String finishDate = "27/09/24";

        SuspendedRequest suspendedRequest = new SuspendedRequest(suspendId, null, null);
        Request request = new Request("userId","token",JsonUtils.serialize(suspendedRequest));


        doThrow(new NoPermissionException("No Permission")).when(suspendedProxy).removeSuspend(suspendId, request.userId(), request.token());
        String result = suspendedService.removeSuspend(request.toJson());
        assertEquals(Response.getError(new NoPermissionException("No Permission")), result);

    }

    @Test
    void getSuspendListSuccess() throws AuthenticationFailedException, NoPermissionException {

        Request request = new Request("userId","token", null);
        Suspend suspend1 = new Suspend("suspendId1", null, null);
        Suspend suspend2 = new Suspend("suspendId2", null, null);
        List<Suspend> suspendList = Arrays.asList(suspend1, suspend2);

        when(suspendedProxy.getSuspendList(request.userId(), request.token())).thenReturn(suspendList);
        String result = suspendedService.getSuspendList(request.toJson());
        assertEquals(Response.getOk(suspendList), result);
    }

    @Test
    void getSuspendListFailure() throws AuthenticationFailedException, NoPermissionException{

        Request request = new Request("userId","token", null);
        doThrow(new NoPermissionException("No Permission")).when(suspendedProxy).getSuspendList(request.userId(), request.token());
        String result = suspendedService.getSuspendList(request.toJson());
        assertEquals(Response.getError(new NoPermissionException("No Permission")), result);
    }
}
