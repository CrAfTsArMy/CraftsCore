package de.craftsblock.craftscore.cache;

import org.jetbrains.annotations.ApiStatus;

/**
 * A cache implementation that stores key-value pairs, with each entry indexed by a pair of keys.
 * This allows for efficient retrieval and management of cached data based on two distinct keys.
 *
 * @param <K1> The type of the first key.
 * @param <K2> The type of the second key.
 * @param <V>  The type of value stored in the cache.
 * @author Philipp Maywald
 * @author CraftsBlock
 * @version 1.0
 * @since 3.7.39
 * @deprecated Use {@link DoubleKeyedLruCache} instead.
 */
@Deprecated(since = "3.8.13", forRemoval = true)
@ApiStatus.ScheduledForRemoval(inVersion = "4.0.0")
public class DoubleKeyedCache<K1, K2, V> extends DoubleKeyedLruCache<K1, K2, V> {

    /**
     * Creates a new {@link DoubleKeyedCache} with the specified maximum capacity.
     *
     * @param maxSize The maximum number of entries this cache can hold before
     *                evicting the least recently used entry.
     * @deprecated Use {@link DoubleKeyedCache} instead.
     */
    @Deprecated(since = "3.8.13", forRemoval = true)
    @ApiStatus.ScheduledForRemoval(inVersion = "4.0.0")
    public DoubleKeyedCache(int maxSize) {
        super(maxSize);
    }

}
