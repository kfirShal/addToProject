package com.amazonas.backend.business.permissions.proxies;

import com.amazonas.backend.business.authentication.AuthenticationController;
import com.amazonas.backend.business.notifications.NotificationController;
import com.amazonas.backend.business.permissions.PermissionsController;
import com.amazonas.common.permissions.actions.UserActions;
import com.amazonas.backend.exceptions.AuthenticationFailedException;
import com.amazonas.backend.exceptions.NoPermissionException;
import com.amazonas.backend.exceptions.NotificationException;
import com.amazonas.common.dtos.Notification;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("notificationProxy")
public class NotificationProxy extends ControllerProxy{

    private final NotificationController real;

    protected NotificationProxy(PermissionsController perm, AuthenticationController auth, NotificationController notificationController) {
        super(perm, auth);
        this.real = notificationController;
    }


    public void sendNotification(String title, String message, String senderId, String receiverId, String userId, String token) throws NotificationException, NoPermissionException, AuthenticationFailedException {
        authenticateToken(userId, token);
        checkPermission(userId, UserActions.SEND_NOTIFICATION);
        real.sendNotification(title, message, senderId, receiverId);
    }


    public void setReadValue(String notificationId, Boolean read, String userId, String token) throws NotificationException, NoPermissionException, AuthenticationFailedException {
        authenticateToken(userId, token);
        checkPermission(userId, UserActions.SET_NOTIFICATION_READ);
        real.setReadValue(notificationId, read);
    }

    public List<Notification> getUnreadNotifications(String receiverId, String userId, String token) throws NotificationException, NoPermissionException, AuthenticationFailedException {
        authenticateToken(userId, token);
        checkPermission(userId, UserActions.READ_NOTIFICATIONS);
        return real.getUnreadNotifications(receiverId);
    }

    public List<Notification> getNotifications(String receiverId, Integer limit, Integer offset, String userId, String token) throws NotificationException, NoPermissionException, AuthenticationFailedException {
        authenticateToken(userId, token);
        checkPermission(userId, UserActions.READ_NOTIFICATIONS);
        return real.getNotifications(receiverId, limit, offset);
    }

    public void deleteNotification(String notificationId, String userId, String token) throws NotificationException, NoPermissionException, AuthenticationFailedException {
        authenticateToken(userId, token);
        checkPermission(userId, UserActions.DELETE_NOTIFICATION);
        real.deleteNotification(notificationId);
    }
}
