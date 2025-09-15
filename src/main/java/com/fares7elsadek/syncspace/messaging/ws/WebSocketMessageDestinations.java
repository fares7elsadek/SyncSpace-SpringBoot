package com.fares7elsadek.syncspace.messaging.ws;

public class WebSocketMessageDestinations {
    public static final String CHANNEL_MESSAGES = "/topic/channel/{channelId}/messages";
    public static final String CHANNEL_DELETIONS = "/topic/channel/{channelId}/deletions";
    public static final String CHANNEL_TYPING = "/topic/channel/{channelId}/typing";
    public static final String CHANNEL_MESSAGES_PRIVATE = "/queue/private/messages";
    public static final String CHANNEL_DELETIONS_PRIVATE = "/queue/private/deletions";
}
