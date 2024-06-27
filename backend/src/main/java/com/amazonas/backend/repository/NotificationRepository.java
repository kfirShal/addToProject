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

    private final Map<String,Notification> notifications;//TODO: remove this when we have a real database
    private final Map<String, List<Notification>> receiverIdToNotifications;

    public NotificationRepository(MongoCollection<Notification> repo) {
        super(repo);
        notifications = new HashMap<>();
        receiverIdToNotifications = new HashMap<>();
    }

    //TODO: replace these methods with the real database calls

    public void insert(Notification notification) {
        notifications.put(notification.notificationId(),notification);
        receiverIdToNotifications.computeIfAbsent(notification.receiverId(), k -> new LinkedList<>()).add(notification);
    }

    public Notification findById(String notificationId) {
        return notifications.get(notificationId);
    }

    public List<Notification> findUnreadByReceiverId(String receiverId) {
        return receiverIdToNotifications.getOrDefault(receiverId, List.of()).stream()
                .filter(n -> !n.read())
                .toList();
    }

    public List<Notification> findByReceiverId(String receiverId, Integer limit) {
        return findByReceiverId(receiverId, limit, 0);
    }

    public List<Notification> findByReceiverId(String receiverId, Integer limit, Integer offset) {
        return receiverIdToNotifications.getOrDefault(receiverId, List.of()).stream()
                .skip(offset)
                .limit(limit)
                .toList();
    }

    public void delete(String notificationId) {
        Notification n = notifications.remove(notificationId);
        receiverIdToNotifications.get(n.receiverId()).remove(n);
    }

    public boolean existsByReceiverId(String receiverId) {
        return receiverIdToNotifications.containsKey(receiverId);
    }
}
