package com.amazonas.backend.business.notifications;

import com.amazonas.backend.exceptions.NotificationException;
import com.amazonas.backend.repository.NotificationRepository;
import com.amazonas.backend.repository.UserRepository;
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
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        senderId = "senderId";
        receiverId = "receiverId";
        repository = mock(NotificationRepository.class);
        userRepository = mock(UserRepository.class);
        when(userRepository.userIdExists(senderId)).thenReturn(true);
        when(userRepository.userIdExists(receiverId)).thenReturn(true);
        notification = new Notification(notificationId, "title", "message", null ,receiverId, receiverId);
        notificationController = new NotificationController(repository,userRepository);
        notificationId = "notificationId";
    }

    @Test
    void sendNotificationGood() {
        assertDoesNotThrow(()->notificationController.sendNotification("title", "message", receiverId, receiverId));
    }

    @Test
    void sendNotificationReceiverIdDoesNotExist() {
        when(userRepository.userIdExists(receiverId)).thenReturn(false);
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
        List<Notification> notifications = List.of(notification);
        when(repository.findUnreadByReceiverId(receiverId)).thenReturn(notifications);
        List<Notification> received = assertDoesNotThrow(()->notificationController.getUnreadNotifications(receiverId));
        assert(notifications.equals(received));
    }

    @Test
    void getUnreadNotificationsReceiverIdDoesNotExist() {
        when(userRepository.userIdExists(receiverId)).thenReturn(false);
        assertThrows(NotificationException.class, ()->notificationController.getUnreadNotifications(receiverId));
    }

    @Test
    void getNotificationsGood() {
        List<Notification> notifications = List.of(notification);
        when(repository.findByReceiverId(receiverId, 10,0)).thenReturn(notifications);
        List<Notification> received = assertDoesNotThrow(()->notificationController.getNotifications(receiverId, 10));
        assert(notifications.equals(received));
    }

    @Test
    void getNotificationsReceiverIdDoesNotExist() {
        when(userRepository.userIdExists(receiverId)).thenReturn(false);
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