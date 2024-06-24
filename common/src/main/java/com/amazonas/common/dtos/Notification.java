package com.amazonas.common.dtos;

import java.time.LocalDateTime;

public class Notification {
    private final String notificationId;
    private final String title;
    private final String message;
    private final String senderId;
    private final String receiverId;
    private final LocalDateTime timestamp;
    private boolean read;

    public Notification(String notificationId, String title, String message, LocalDateTime timestamp, String senderId, String receiverId) {
        this.notificationId = notificationId;
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
        this.senderId = senderId;
        this.receiverId = receiverId;
    }

    public String notificationId() {
        return notificationId;
    }

    public String message() {
        return message;
    }

    public String senderId() {
        return senderId;
    }

    public String receiverId() {
        return receiverId;
    }

    public boolean read() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public LocalDateTime timestamp() {
        return timestamp;
    }

    public String title() {
        return title;
    }
}
