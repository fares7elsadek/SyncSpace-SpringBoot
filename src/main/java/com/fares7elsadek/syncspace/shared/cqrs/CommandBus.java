package com.fares7elsadek.syncspace.shared.cqrs;


import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommandBus {
    private final ApplicationContext applicationContext;

    public <C extends Command, R> R send(C command) {
        String handlerName = command.getClass().getSimpleName() + "Handler";
        try{
            CommandHandler<C,R> handler = (CommandHandler<C, R>) applicationContext.getBean(handlerName);
            return handler.handle(command);
        }catch(Exception e){
            throw new IllegalArgumentException("No handler found for command: " + command.getClass().getSimpleName());
        }
    }

}
