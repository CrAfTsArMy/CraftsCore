package de.craftsblock.craftscore.cache;

import org.jetbrains.annotations.ApiStatus;

/**
 * A simple cache implementation that uses ConcurrentHashMap and ConcurrentLinkedDeque
 * to store key-value pairs with a fixed capacity. When the capacity is exceeded,
 * the least recently used items are automatically removed to make space for new entries.
 *
 * @param <K> The type of the keys in the cache.
 * @param <V> The type of the values in the cache.
 * @author Philipp Maywald
 * @author CraftsBlock
 * @version 2.0.2
 * @since 3.6#4
 * @deprecated Use {@link LruCache} instead.
 */
@Deprecated(since = "3.8.13", forRemoval = true)
@ApiStatus.ScheduledForRemoval(inVersion = "4.0.0")
public class Cache<K, V> extends LruCache<K, V> {

    /**
     * Creates a new {@link Cache} with the specified maximum capacity.
     *
     * @param capacity The maximum number of entries this cache can hold before
     *                 evicting the least recently used entry.
     * @deprecated Use {@link Cache} instead.
     */
    @Deprecated(since = "3.8.13", forRemoval = true)
    @ApiStatus.ScheduledForRemoval(inVersion = "4.0.0")
    public Cache(int capacity) {
        super(capacity);
    }

}
