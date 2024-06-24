package com.amazonas.backend.repository;

import com.amazonas.common.dtos.Notification;
import com.amazonas.backend.repository.abstracts.AbstractCachingRepository;
import com.amazonas.backend.repository.abstracts.MongoCollection;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class NotificationRepository extends AbstractCachingRepository<Notification> {

    public NotificationRepository(MongoCollection<Notification> repo) {
        super(repo);
    }

    public void insert(Notification notification) {

    }

    public Notification findById(String notificationId) {
        return null;
    }

    public void update(Notification notification) {
    }

    public List<Notification> findUnreadByReceiverId(String receiverId) {
        return null;
    }

    public List<Notification> findByReceiverId(String receiverId, Integer limit) {
        return null;
    }

    public void delete(String notificationId) {

    }

    public boolean existsById(String receiverId) {
        return false;
    }
}
