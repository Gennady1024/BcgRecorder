package com.github.dreamrec.filters;

/**
 *
 */
public interface   Filter<T> {
    public int size();
    public T get(int index);
    public int divider();
}
