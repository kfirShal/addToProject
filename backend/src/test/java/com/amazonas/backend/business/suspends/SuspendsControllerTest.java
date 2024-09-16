package com.amazonas.backend.business.suspends;
import com.amazonas.backend.business.notifications.NotificationController;
import com.amazonas.backend.business.suspended.SuspendedController;
import com.amazonas.common.dtos.Suspend;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SuspendsControllerTest {

    private SuspendedController suspendedController;
    private Suspend suspend;

    @BeforeEach
    public void setUp(){
        suspendedController = SuspendedController.getInstance();
        suspend = new Suspend("5", "14/09/24", "26/09/24");
    }

    @Test
    void addSuspendPositive(){
        suspendedController.addSuspend(suspend);
        assertTrue(suspendedController.getSuspendList().contains(suspend));
    }

    @Test
    void addSuspendNegative(){
        suspendedController.addSuspend(new Suspend("5", "26/09/24", "always"));
        assertFalse(suspendedController.isSuspended("5"));
    }

    @Test
    void removeSuspendPositive(){
        suspendedController.addSuspend(suspend);
        suspendedController.removeSuspend("5");
        assertFalse(suspendedController.getSuspendList().contains(suspend));
    }

    @Test
    void isSuspendedPositive(){
        suspendedController.addSuspend(suspend);
        assertTrue(suspendedController.isSuspended(suspend.getSuspendId()));
    }

    @Test
    void isSuspendedNegative(){
        suspendedController.addSuspend(suspend);
        suspendedController.removeSuspend("5");
        assertFalse(suspendedController.isSuspended(suspend.getSuspendId()));
    }


}
