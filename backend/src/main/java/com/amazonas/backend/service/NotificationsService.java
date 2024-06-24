package com.amazonas.backend.service;

import com.amazonas.backend.business.notifications.NotificationController;
import com.amazonas.backend.exceptions.NotificationException;
import com.amazonas.common.dtos.Notification;
import com.amazonas.common.requests.Request;
import com.amazonas.common.requests.notifications.NotificationRequest;
import com.amazonas.common.utils.Response;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("notificationsService")
public class NotificationsService {

    private final NotificationController notificationController;

    public NotificationsService(NotificationController notificationController) {
        this.notificationController = notificationController;
    }
    public String sendNotification(String json){
        Request request = Request.from(json);
        try {
            NotificationRequest toSend = NotificationRequest.from(request.payload());
            notificationController.sendNotification(toSend.title(), toSend.message(), toSend.senderId(), toSend.receiverId());
            return Response.getOk();
        } catch (NotificationException e) {
            return Response.getError(e);
        }
    }

    public String setReadValue(String json){
        Request request = Request.from(json);
        try {
            NotificationRequest toSet = NotificationRequest.from(request.payload());
            notificationController.setReadValue(toSet.notificationId(), toSet.read());
            return Response.getOk();
        } catch (NotificationException e) {
            return Response.getError(e);
        }
    }

    public String getUnreadNotifications(String json) {
        Request request = Request.from(json);
        try {
            NotificationRequest toGet = NotificationRequest.from(request.payload());
            List<Notification> notifications = notificationController.getUnreadNotifications(toGet.receiverId());
            return Response.getOk(notifications);
        } catch (NotificationException e) {
            return Response.getError(e);
        }
    }

    public String getNotifications(String json) {
        Request request = Request.from(json);
        try {
            NotificationRequest toGet = NotificationRequest.from(request.payload());
            List<Notification> notifications = notificationController.getNotifications(toGet.receiverId(), toGet.limit());
            return Response.getOk(notifications);
        } catch (NotificationException e) {
            return Response.getError(e);
        }
    }

    public String deleteNotification(String json) {
        Request request = Request.from(json);
        try {
            NotificationRequest toDelete = NotificationRequest.from(request.payload());
            notificationController.deleteNotification(toDelete.notificationId());
            return Response.getOk();
        } catch (NotificationException e) {
            return Response.getError(e);
        }
    }
}
