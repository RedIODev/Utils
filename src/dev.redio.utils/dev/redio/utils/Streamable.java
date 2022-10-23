package dev.redio.utils;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface Streamable<E> extends Iterable<E> {

    public static <E> Streamable<E> of(Iterable<E> iterable) {
        class StreamableImpl implements Streamable<E> {

            @Override
            public Iterator<E> iterator() {
                return iterable.iterator();
            }

            @Override
            public void forEach(Consumer<? super E> action) {
                iterable.forEach(action);
            }

            @Override
            public Spliterator<E> spliterator() {
                return iterable.spliterator();
            }
        }

        return new StreamableImpl();
    }

    default Stream<E> stream() {
        return StreamSupport.stream(this.spliterator(), false);
    }

    default Stream<E> parallelStream() {
        return StreamSupport.stream(this.spliterator(), true);
    }

}
