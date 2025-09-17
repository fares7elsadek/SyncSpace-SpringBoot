package com.fares7elsadek.syncspace.shared.events;

public interface EventPublisher {
    void publish(DomainEvent event);
    void publishAsync(DomainEvent event);
}
