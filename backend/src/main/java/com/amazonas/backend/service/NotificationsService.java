package com.amazonas.backend.service;

import com.amazonas.backend.business.permissions.proxies.NotificationProxy;
import com.amazonas.backend.exceptions.AuthenticationFailedException;
import com.amazonas.backend.exceptions.NoPermissionException;
import com.amazonas.backend.exceptions.NotificationException;
import com.amazonas.common.dtos.Notification;
import com.amazonas.common.requests.Request;
import com.amazonas.common.requests.notifications.NotificationRequest;
import com.amazonas.common.utils.Response;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("notificationsService")
public class NotificationsService {

    private final NotificationProxy proxy;

    public NotificationsService(NotificationProxy proxy) {
        this.proxy = proxy;
    }
    public String sendNotification(String json){
        Request request = Request.from(json);
        try {
            NotificationRequest toSend = NotificationRequest.from(request.payload());
            proxy.sendNotification(toSend.title(), toSend.message(), toSend.senderId(), toSend.receiverId(), request.userId(), request.token());
            return Response.getOk();
        } catch (NotificationException | NoPermissionException | AuthenticationFailedException e) {
            return Response.getError(e);
        }
    }

    public String setReadValue(String json){
        Request request = Request.from(json);
        try {
            NotificationRequest toSet = NotificationRequest.from(request.payload());
            proxy.setReadValue(toSet.notificationId(), toSet.read(), request.userId(), request.token());
            return Response.getOk();
        } catch (NotificationException | NoPermissionException | AuthenticationFailedException e) {
            return Response.getError(e);
        }
    }

    public String getUnreadNotifications(String json) {
        Request request = Request.from(json);
        try {
            NotificationRequest toGet = NotificationRequest.from(request.payload());
            List<Notification> notifications = proxy.getUnreadNotifications(toGet.receiverId(), request.userId(), request.token());
            return Response.getOk(notifications);
        } catch (NotificationException | NoPermissionException | AuthenticationFailedException e) {
            return Response.getError(e);
        }
    }

    public String getNotifications(String json) {
        Request request = Request.from(json);
        try {
            NotificationRequest toGet = NotificationRequest.from(request.payload());
            List<Notification> notifications = proxy.getNotifications(toGet.receiverId(), toGet.limit(), toGet.offset(), request.userId(), request.token());
            return Response.getOk(notifications);
        } catch (NotificationException | NoPermissionException | AuthenticationFailedException e) {
            return Response.getError(e);
        }
    }

    public String deleteNotification(String json) {
        Request request = Request.from(json);
        try {
            NotificationRequest toDelete = NotificationRequest.from(request.payload());
            proxy.deleteNotification(toDelete.notificationId(), request.userId(), request.token());
            return Response.getOk();
        } catch (NotificationException | NoPermissionException | AuthenticationFailedException e) {
            return Response.getError(e);
        }
    }
}
