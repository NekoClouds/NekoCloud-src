package me.nekocloud.base.util.lists;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.base.Ticker;
import com.google.common.primitives.Longs;
import lombok.AllArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.TimeUnit;

public class ExpireList<V> {

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
        Preconditions.checkNotNull((unit), "expireUnit cannot be NULL");
        Preconditions.checkState((expireDelay > 0L ? 1 : 0) != 0, "expireDelay cannot be equal or less than zero.");

        evictExpired();

        val entry = new ExpireEntry(this.ticker.read() + TimeUnit.NANOSECONDS.convert(expireDelay, unit), value);
        val previous = this.keyLookup.put(value, entry);

        expireQueue.add(entry);
        return previous != null ? previous.expireValue : null;
    }

    public int size() {
        evictExpired();

        return this.keyLookup.size();
    }

    public void clear() {
        keyLookup.clear();
        expireQueue.clear();
    }

    public boolean contains(V value) {
        evictExpired();

        return this.keyLookup.containsKey(value);
    }

    public V remove(V value) {
        evictExpired();

        val entry = this.keyLookup.remove(value);

        return entry != null ? (V)entry.expireValue : null;
    }

    private void evictExpired() {
        val currentTime = this.ticker.read();
        while (expireQueue.size() > 0 && expireQueue.peek().expireTime <= currentTime) {

            val entry = expireQueue.poll();

            if (entry != keyLookup.get(entry.expireValue)) continue;

            keyLookup.remove(entry.expireValue);
        }
    }

    public String toString() {
        return keyLookup.toString();
    }

    @AllArgsConstructor
    private class ExpireEntry implements Comparable<ExpireEntry> {

        final long expireTime;
        final V expireValue;

        @Contract(pure = true)
        @Override
        public int compareTo(@NotNull ExpireEntry o) {
            return Longs.compare(expireTime, o.expireTime);
        }

        public @NotNull String toString() {
            return MoreObjects.toStringHelper(this).add("expireTime", expireTime).add("expireValue", this.expireValue).toString();
        }
    }
}

