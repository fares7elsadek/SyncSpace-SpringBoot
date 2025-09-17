package com.fares7elsadek.syncspace.shared.cqrs;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommandBus {
    private final ApplicationContext applicationContext;

    public <C extends Command, R> R send(C command) {
        String handlerClassName = command.getClass().getSimpleName() + "Handler";
        String handlerName = Character.toLowerCase(handlerClassName.charAt(0)) + handlerClassName.substring(1);
        try {
            Object bean = applicationContext.getBean(handlerName);
            CommandHandler<C,R> handler = (CommandHandler<C, R>) bean;
            R result = handler.handle(command);
            return result;
        } catch(Exception e) {
            throw new  RuntimeException(e);
        }
    }

}
