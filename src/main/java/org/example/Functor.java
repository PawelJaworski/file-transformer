package org.example;

import java.util.function.Function;

public class Functor<T> {
    private final T value;

    public static <T>Functor<T> get(T value) {
        return new Functor<>(value);
    }

    private Functor(T value) {
        this.value = value;
    }
    public <R> Functor<R> then(Function<T, R> mapper) {
        return new Functor<>(mapper.apply(value));
    }

    public T value() {
        return value;
    }
}
