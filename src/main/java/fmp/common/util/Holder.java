package fmp.common.util;

import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Holder<T> {

    final ReentrantLock lock = new ReentrantLock();

    private volatile T value = null;

    public void set(T value) {
        check( () -> {
            return this.value = value;
        });
    }

    public T get() {
        return check( ()->value );
    }

    public Optional<T> getO() {
        return check( () -> Optional.ofNullable( value ) );
    }

    public void ifPresent( Consumer<? super T> consumer ) {
        try {
            lock.lock();
            if( value != null )
                consumer.accept( value );
        } finally {
            lock.unlock();
        }
    }

    private<M> M check(Supplier<M> su ) {
        try {
            lock.lock();
            return su.get();
        } finally {
            lock.unlock();
        }
    }
}
