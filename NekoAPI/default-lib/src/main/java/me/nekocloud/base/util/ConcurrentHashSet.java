package me.nekocloud.base.util;

import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class ConcurrentHashSet<E> extends AbstractSet<E> implements Serializable {

    @Serial
    private static final long serialVersionUID = -6761513279741915432L;
    private final ConcurrentMap<E, Boolean> map;

    public ConcurrentHashSet() {
        this.map = new ConcurrentHashMap<>();
    }

    public ConcurrentHashSet(int capacity) {
        this.map = new ConcurrentHashMap<>(capacity);
    }

    @Override
    public int size() {
        return this.map.size();
    }

    @Override
    public boolean contains(Object o) {
        return this.map.containsKey(o);
    }

    @Override
    public boolean add(E o) {
        return this.map.putIfAbsent(o, Boolean.TRUE) == null;
    }

    @Override
    public boolean remove(Object o) {
        return this.map.remove(o) != null;
    }

    @Override
    public void clear() {
        this.map.clear();
    }

    @Override
    public @NotNull Iterator<E> iterator() {
        return this.map.keySet().iterator();
    }
}

