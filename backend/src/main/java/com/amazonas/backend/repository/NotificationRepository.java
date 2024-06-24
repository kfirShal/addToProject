package com.amazonas.backend.repository;

import com.amazonas.backend.repository.abstracts.AbstractCachingRepository;
import com.amazonas.backend.repository.abstracts.MongoCollection;
import com.amazonas.common.dtos.Notification;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Repository
public class NotificationRepository extends AbstractCachingRepository<Notification> {

    private Map<String,Notification> notifications; //TODO: remove this when we have a real database

    public NotificationRepository(MongoCollection<Notification> repo) {
        super(repo);
        notifications = new HashMap<>();
    }

    //TODO: replace these methods with the real database calls

    public void insert(Notification notification) {
        notifications.put(notification.notificationId(),notification);
    }

    public Notification findById(String notificationId) {
        return notifications.get(notificationId);
    }

    public void update(Notification notification) {
        notifications.put(notification.notificationId(),notification);
    }

    public List<Notification> findUnreadByReceiverId(String receiverId) {
        List<Notification> unreadNotifications = new LinkedList<>();
        for(Notification notification : notifications.values()){
            if(notification.receiverId().equals(receiverId) && !notification.read()){
                unreadNotifications.add(notification);
            }
        }
        return unreadNotifications;
    }

    public List<Notification> findByReceiverId(String receiverId, Integer limit) {

        List<Notification> notifications = new LinkedList<>(findUnreadByReceiverId(receiverId));
        for(Notification notification : this.notifications.values()){
            if(notification.receiverId().equals(receiverId)){
                notifications.add(notification);
            }
        }
        return notifications.stream().limit(limit).toList();
    }

    public void delete(String notificationId) {
        notifications.remove(notificationId);
    }

    public boolean existsById(String receiverId) {
        return notifications.values().stream()
                .anyMatch(notification -> notification.receiverId().equals(receiverId));
    }
}
