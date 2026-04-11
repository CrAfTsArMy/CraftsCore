package de.craftsblock.craftscore.cache;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A simple Least Recently Used (LRU) cache implementation based on {@link LinkedHashMap}.
 * <p>
 * This cache automatically removes the least recently accessed entry once the
 * configured capacity is exceeded. Access order is enabled to ensure that entries
 * are reordered based on usage.
 *
 * @param <K> The type of keys maintained by this cache
 * @param <V> The type of mapped values
 * @author Philipp Maywald
 * @author CraftsBlock
 * @see LinkedHashMap
 * @since 3.8.13
 */
public class LruCache<K, V> extends LinkedHashMap<K, V> {

    private final int capacity;

    /**
     * Creates a new {@link LruCache} with the specified maximum capacity.
     *
     * @param capacity The maximum number of entries this cache can hold before
     *                 evicting the least recently used entry.
     */
    public LruCache(int capacity) {
        super(capacity, 0.75f, true);
        this.capacity = capacity;
    }

    /**
     * {@inheritDoc}
     *
     * @param eldest {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > capacity;
    }

    /**
     * Returns the maximum number of entries this cache can hold.
     *
     * @return The cache capacity
     */
    public int getCapacity() {
        return capacity;
    }

}
