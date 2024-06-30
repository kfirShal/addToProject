package com.amazonas.backend.business.notifications;

import com.amazonas.backend.exceptions.NotificationException;
import com.amazonas.backend.repository.NotificationRepository;
import com.amazonas.common.dtos.Notification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class NotificationControllerTest {


    private NotificationController notificationController;
    private NotificationRepository repository;
    private String senderId;
    private String receiverId;
    private String notificationId;
    private Notification notification;

    @BeforeEach
    void setUp() {
        senderId = "senderId";
        receiverId = "receiverId";
        repository = mock(NotificationRepository.class);
        notification = new Notification(notificationId, "title", "message", null ,receiverId, receiverId);
        notificationController = new NotificationController(repository);
        notificationId = "notificationId";
    }

    @Test
    void sendNotificationGood() {
        when(repository.existsByReceiverId(senderId)).thenReturn(true);
        when(repository.existsByReceiverId(receiverId)).thenReturn(true);
        assertDoesNotThrow(()->notificationController.sendNotification("title", "message", receiverId, receiverId));
    }

    @Test
    void sendNotificationReceiverIdDoesNotExist() {
        when(repository.existsByReceiverId(senderId)).thenReturn(true);
        when(repository.existsByReceiverId(receiverId)).thenReturn(false);
        assertThrows(NotificationException.class, ()->notificationController.sendNotification("title", "message", receiverId, receiverId));
    }

    @Test
    void setReadValueGood() {
        when(repository.findById(notificationId)).thenReturn(notification);
        assertDoesNotThrow(()->notificationController.setReadValue(notificationId, true));
    }

    @Test
    void setReadValueNotificationDoesNotExist() {
        when(repository.findById(notificationId)).thenReturn(null);
        assertThrows(NotificationException.class, ()->notificationController.setReadValue(notificationId, true));
    }

    @Test
    void getUnreadNotificationsGood() {
        when(repository.existsByReceiverId(receiverId)).thenReturn(true);
        List<Notification> notifications = List.of(notification);
        when(repository.findUnreadByReceiverId(receiverId)).thenReturn(notifications);
        List<Notification> received = assertDoesNotThrow(()->notificationController.getUnreadNotifications(receiverId));
        assert(notifications.equals(received));
    }

    @Test
    void getUnreadNotificationsReceiverIdDoesNotExist() {
        when(repository.existsByReceiverId(receiverId)).thenReturn(false);
        assertThrows(NotificationException.class, ()->notificationController.getUnreadNotifications(receiverId));
    }

    @Test
    void getNotificationsGood() {
        when(repository.existsByReceiverId(receiverId)).thenReturn(true);
        List<Notification> notifications = List.of(notification);
        when(repository.findByReceiverId(receiverId, 10)).thenReturn(notifications);
        List<Notification> received = assertDoesNotThrow(()->notificationController.getNotifications(receiverId, 10));
        assert(notifications.equals(received));
    }

    @Test
    void getNotificationsReceiverIdDoesNotExist() {
        when(repository.existsByReceiverId(receiverId)).thenReturn(false);
        assertThrows(NotificationException.class, ()->notificationController.getNotifications(receiverId, 10));
    }

    @Test
    void deleteNotificationGood() {
        when(repository.findById(notificationId)).thenReturn(notification);
        assertDoesNotThrow(()->notificationController.deleteNotification(notificationId));
    }

    @Test
    void deleteNotificationNotificationDoesNotExist() {
        when(repository.findById(notificationId)).thenReturn(null);
        assertThrows(NotificationException.class, ()->notificationController.deleteNotification(notificationId));
    }
}