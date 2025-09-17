package com.fares7elsadek.syncspace.shared.cqrs;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class QueryBus {
    private final ApplicationContext applicationContext;
    public <Q extends Query<R>, R> R send(Q query) {
        String handlerClassName = query.getClass().getSimpleName() + "Handler";
        String handlerName = Character.toLowerCase(handlerClassName.charAt(0)) + handlerClassName.substring(1);
        try {
            QueryHandler<Q,R> handler = (QueryHandler<Q, R>) applicationContext.getBean(handlerName);
            R result = handler.handle(query);
            return result;
        } catch(Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
