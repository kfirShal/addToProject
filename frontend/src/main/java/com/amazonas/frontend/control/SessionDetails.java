package com.amazonas.frontend.control;

import org.apache.catalina.session.StandardSession;

import java.util.Objects;

public class SessionDetails {

    private String sessionId;
    private String userId;
    private String token;
    private boolean isGuest;
    private StandardSession session;

    public SessionDetails(StandardSession session) {
        this.sessionId = session.getId();
        this.session = session;
        isGuest = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SessionDetails that = (SessionDetails) o;
        return Objects.equals(sessionId, that.sessionId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(sessionId);
    }

    public String userId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String token() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public StandardSession session() {
        return session;
    }

    public boolean isGuest() {
        return isGuest;
    }

    public void setGuest(boolean b) {
        isGuest = b;
    }
}
