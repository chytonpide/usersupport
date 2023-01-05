package com.nconnect.usersupport.infrastructure.persistence;

import io.vlingo.xoom.lattice.query.StateStoreQueryActor;
import io.vlingo.xoom.symbio.Metadata;
import io.vlingo.xoom.symbio.State;

import java.util.function.Consumer;

public class QueryResultHandler<T> {
    final Consumer<T> consumer;
    final T notFoundState;


    static QueryResultHandler from(Object handler) {
        return (QueryResultHandler)handler;
    }

    QueryResultHandler(Consumer<T> consumer, T notFoundState) {
        this.consumer = consumer;
        this.notFoundState = notFoundState;
    }

    void completeNotFound() {
        this.consumer.accept(this.notFoundState);
    }

    void completeFoundWith(T state) {
        this.consumer.accept(state);
    }

}