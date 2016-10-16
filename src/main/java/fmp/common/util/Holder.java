package fmp.common.util;

import java.util.function.Supplier;
import java.util.concurrent.locks.ReentrantLock;

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

    private T check(Supplier<T> su ) {
        try {
            lock.lock();
            return su.get();
        } finally {
            lock.unlock();
        }
    }
}
