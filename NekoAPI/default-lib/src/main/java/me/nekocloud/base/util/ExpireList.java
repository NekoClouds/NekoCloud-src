package me.nekocloud.base.util;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.base.Ticker;
import com.google.common.primitives.Longs;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.TimeUnit;

/**
 * Represents a list of values with eviction delay.
 *
 * @param <V> - npc of value
 * @author CatCoder
 */
public class ExpireList<V> {

    private class ExpireEntry implements Comparable<ExpireEntry> {
        final long expireTime;
        final V expireValue;

        ExpireEntry(long expireTime, V expireValue) {
            this.expireTime = expireTime;
            this.expireValue = expireValue;
        }

        @Override
        public int compareTo(ExpireEntry o) {
            return Longs.compare(expireTime, o.expireTime);
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("expireTime", expireTime)
                    .add("expireValue", expireValue)
                    .toString();
        }
    }

    private final Map<V, ExpireEntry> keyLookup = new HashMap<>();
    private final PriorityQueue<ExpireEntry> expireQueue = new PriorityQueue<>();

    private final Ticker ticker;

    public ExpireList() {
        this(Ticker.systemTicker());
    }

    public ExpireList(Ticker ticker) {
        this.ticker = ticker;

    }

    public V add(V value, long expireDelay, TimeUnit unit) {
        Preconditions.checkNotNull(unit, "expireUnit cannot be NULL");
        Preconditions.checkState(expireDelay > 0, "expireDelay cannot be equal or less than zero.");
        evictExpired();

        ExpireEntry entry = new ExpireEntry(ticker.read() + TimeUnit.NANOSECONDS.convert(expireDelay, unit), value);
        ExpireEntry previous = keyLookup.put(value, entry);

        expireQueue.add(entry);
        return previous != null ? previous.expireValue : null;
    }

    public int size() {
        evictExpired();
        return keyLookup.size();
    }

    public void clear() {
        keyLookup.clear();
        expireQueue.clear();
    }

    public boolean contains(V value) {
        evictExpired();

        return keyLookup.containsKey(value);
    }

    public V remove(V value) {
        evictExpired();

        ExpireEntry entry = keyLookup.remove(value);
        return entry != null ? entry.expireValue : null;
    }

    private void evictExpired() {
        long currentTime = ticker.read();

        while (expireQueue.size() > 0 && expireQueue.peek().expireTime <= currentTime) {
            ExpireEntry entry = expireQueue.poll();

            if (entry == keyLookup.get(entry.expireValue)) {
                keyLookup.remove(entry.expireValue);
            }
        }
    }

    @Override
    public String toString() {
        return keyLookup.toString();
    }
}
