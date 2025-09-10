package com.fares7elsadek.syncspace.shared.cqrs;

public interface CommandHandler <C extends Command,R>{
    R handle(C command);
}
