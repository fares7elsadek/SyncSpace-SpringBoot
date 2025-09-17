package com.fares7elsadek.syncspace.user.shared;

public interface PresenceService {
    public void setOnline(String userId,String sessionId);
    public void setOffline(String userId,String sessionId);
}
