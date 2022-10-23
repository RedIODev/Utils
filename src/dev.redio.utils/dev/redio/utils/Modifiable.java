package dev.redio.utils;

public interface Modifiable<E> {

    long length();

    void set(long index, E value);

    default void clear() {
        this.fill(null);
    }

    default void fill(E value) {
        final long length = this.length();
        for (long i = 0; i < length; i++) 
            this.set(i, value);
    }
}
