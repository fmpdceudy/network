package fmp.remoting.transport;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.function.Predicate;

import fmp.common.Addr;
import fmp.remoting.Channel;

public abstract class AbstractChannel<T> implements Channel {

    private static class GenMap<T, U extends AbstractChannel<T> > {
        private final Class<T> clazz;
        private final ConcurrentMap<T, U> map;
        private final Function<T, U> gen;
        private final Predicate<T> canremove;

        private GenMap( Class<T> clazz, Function<T,U> gen, Predicate<T> canremove ) {
            this.clazz = Objects.requireNonNull( clazz );
            this.gen = Objects.requireNonNull( gen );
            this.canremove = Objects.requireNonNull( canremove );
            map = new ConcurrentHashMap<>();
        }

        private U get( T ch ) {
            return map.computeIfAbsent( ch, gen );
        }

        private void remove( T ch ) {
            map.keySet().stream().filter( ch::equals ).filter( canremove ).forEach( map::remove );
        }

        private boolean isthis( Class<?> ch ) {
            if( ch == null )
                return false;
            return clazz.isAssignableFrom( ch );
        }

        private Optional<U> getChannel( Addr addr ) {
            return map.values().stream().filter( u -> u.getAddr().equals( addr ) ).findFirst();
        }

        private Collection<U> getChannels() {
            return map.values();
        }

        private void autoremove() {
            map.keySet().stream()
                .filter( canremove )
                .forEach( map::remove );
        }
    }

    private static final List<GenMap<?,?>>
        genlist = Collections.synchronizedList( new ArrayList<>() );

    protected static<T, U extends AbstractChannel<T>> void update( Class<T> clazz,
            Function<T, U> fun,
            Predicate<T> canremove
            ) {
        genlist.stream()
            .filter( l -> l.isthis( clazz ) )
            .findFirst()
            .ifPresent( g -> {
                throw new IllegalStateException();
            });
        genlist.add( new GenMap<T,U>( clazz, fun, canremove ) );
    }

    @SuppressWarnings("unchecked")
    private static<T, K extends T, U extends AbstractChannel<T>> Optional<GenMap<T,U>> getGen( K ch) {
        if( ch == null )
            return Optional.empty();
        return genlist.stream().filter( l -> l.isthis( ch.getClass() )).findFirst().map( t -> (GenMap<T,U>)t);
    }

    @SuppressWarnings("unchecked")
    private static<T, K extends T, U extends AbstractChannel<T>> Optional<GenMap<T,U>> getGen( Class<K> cl) {
        return genlist.stream().filter( l -> l.isthis( cl )).findFirst().map( t -> (GenMap<T,U>)t);
    }

    public static<T, K extends T, U extends AbstractChannel<T>> Optional<U> getChannel( K ch ) {
        return AbstractChannel.<T,K,U>getGen( ch ).map(t -> t.get( ch ));
    }

    public static<T,K extends T> void remove( K ch ) {
        AbstractChannel.getGen( ch ).ifPresent( t -> t.remove( ch ));
    }

    public static<T,K extends T> void autoremove( Class<K> ch ) {
        AbstractChannel.getGen( ch ).ifPresent( GenMap::autoremove );
    }

    public static<T, K extends T, U extends AbstractChannel<T>> Optional<U> getChannel( Class<K> cl, Addr addr ) {
        return AbstractChannel.<T,K,U>getGen( cl ).flatMap( t -> t.getChannel( addr ) );
    }

    public static<T, K extends T, U extends AbstractChannel<T>> Collection<U> getChannels( Class<K> cl) {
        return AbstractChannel.<T,K,U>getGen( cl ).map( GenMap::getChannels ).orElseGet( LinkedList::new );
    }

    protected final T channel;

    protected AbstractChannel( T channel ) {
        this.channel = Objects.requireNonNull( channel );
    }

    protected abstract Addr getAddr();

}
