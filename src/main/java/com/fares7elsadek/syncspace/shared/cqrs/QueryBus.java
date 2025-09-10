package com.fares7elsadek.syncspace.shared.cqrs;


import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QueryBus {
    private final ApplicationContext applicationContext;
    public <Q extends Query<R>, R> R send(Q query) {
        String handlerName = query.getClass().getSimpleName() + "Handler";
        try{
            QueryHandler<Q,R> handler = (QueryHandler<Q, R>) applicationContext.getBean(handlerName);
            return handler.handle(query);
        }catch(Exception e){
            throw new IllegalArgumentException("No handler found for query: " + query.getClass().getSimpleName());
        }
    }
}
