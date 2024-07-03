package com.amazonas.backend.business.notifications;

import com.amazonas.backend.exceptions.NotificationException;
import com.amazonas.backend.repository.NotificationRepository;
import com.amazonas.backend.repository.UserRepository;
import com.amazonas.common.dtos.Notification;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component("notificationController")
public class NotificationController {

    private final NotificationRepository repo;
    private final UserRepository userRepository;

    public NotificationController(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.repo = notificationRepository;
        this.userRepository = userRepository;
    }

    public void sendNotification(String title,String message, String senderId, String receiverId) throws NotificationException {
        validateUserExists(receiverId);
        if(! senderId.equals("Amazonas")){
            validateUserExists(senderId);
        }
        Notification notification = new Notification(UUID.randomUUID().toString(),
                title,
                message,
                LocalDateTime.now(),
                senderId,
                receiverId);
        repo.insert(notification);
    }

    public void setReadValue(String notificationId, boolean read) throws NotificationException {
        Notification notification = repo.findById(notificationId);
        if(notification == null){
            throw new NotificationException("Notification does not exist.");
        }
        notification.setRead(read);
    }

    public List<Notification> getUnreadNotifications(String receiverId) throws NotificationException {
        validateUserExists(receiverId);
        return repo.findUnreadByReceiverId(receiverId);
    }

    public List<Notification> getNotifications(String receiverId, Integer limit) throws NotificationException {
        validateUserExists(receiverId);
        return getNotifications(receiverId, limit, 0);
    }

    public List<Notification> getNotifications(String receiverId, Integer limit, Integer offset) throws NotificationException {
        validateUserExists(receiverId);
        if(limit == null || limit < 1){
            limit = 10;
        }
        return repo.findByReceiverId(receiverId, limit, offset);
    }

    public void deleteNotification(String notificationId) throws NotificationException {
        Notification notification = repo.findById(notificationId);
        if(notification == null){
            throw new NotificationException("Notification does not exist.");
        }

        repo.delete(notificationId);
    }

    private void validateUserExists(String receiverId) throws NotificationException {
        if(!userRepository.userIdExists(receiverId)){
            throw new NotificationException("User does not exist.");
        }
    }
}
