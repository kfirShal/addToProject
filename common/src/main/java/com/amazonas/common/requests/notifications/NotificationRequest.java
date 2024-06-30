package com.amazonas.common.requests.notifications;

import com.amazonas.common.utils.JsonUtils;

public final class NotificationRequest {
    private final String notificationId;
    private final String title;
    private final String message;
    private final String senderId;
    private final String receiverId;
    private final Boolean read;
    private final Integer limit;
    private final Integer offset;

    private NotificationRequest(
            String notificationId,
            String title,
            String message,
            String senderId,
            String receiverId,
            Boolean read,
            Integer limit,
            Integer offset
    ) {
        this.notificationId = notificationId;
        this.title = title;
        this.message = message;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.read = read;
        this.limit = limit;
        this.offset = offset;
    }

    /**
     * Constructor for sending a new notification
     */
    public NotificationRequest(String title, String message, String senderId, String receiverId) {
        this("", title, message, senderId, receiverId, null, 0,0);
    }

    /**
     * Constructor for getting notifications with a limit and offset
     */
    public NotificationRequest(Integer limit, String receiverId, Integer offset) {
        this("", "", "", "", receiverId, null, limit, offset);
    }

    /**
     * Constructor for getting unread notifications and for deleting a notification
     */
    public NotificationRequest(String receiverId) {
        this("", "", "", "", receiverId, null, 0,0);
    }

    /**
     * Constructor for setting the read value of a notification
     */
    public NotificationRequest(Boolean read, String notificationId) {
        this(notificationId, "", "", "", "", read, 0,0);
    }

    public String notificationId() {
        return notificationId;
    }

    public String title() {
        return title;
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

    public Boolean read() {
        return read;
    }

    public Integer limit() {
        return limit;
    }

    public Integer offset() {
        return offset;
    }

    public static NotificationRequest from(String json) {
        return JsonUtils.deserialize(json, NotificationRequest.class);
    }
}
