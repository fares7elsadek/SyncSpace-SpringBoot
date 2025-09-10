package com.fares7elsadek.syncspace.shared.cqrs;

public interface QueryHandler <Q extends Query<R>,R>{
    R handle(Q query);
}
