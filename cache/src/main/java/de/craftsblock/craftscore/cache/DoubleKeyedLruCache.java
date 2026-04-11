package de.craftsblock.craftscore.cache;

import de.craftsblock.craftscore.utils.Pair;

/**
 * A Least Recently Used (LRU) cache implementation that supports
 * composite keys consisting of two separate key parts.
 * <p>
 * Internally, both keys are wrapped into a {@link Pair} which is then
 * used as the actual cache key. This allows efficient storage and
 * retrieval of values based on two-dimensional key structures without
 * requiring a custom key class.
 *
 * @param <K1> The type of the first key component
 * @param <K2> The type of the second key component
 * @param <V>  The type of cached values
 * @author Philipp Maywald
 * @author CraftsBlock
 * @see LruCache
 * @see Pair
 * @since 3.8.13
 */
public class DoubleKeyedLruCache<K1, K2, V> extends LruCache<Pair<K1, K2>, V> {

    /**
     * Creates a new {@link DoubleKeyedLruCache} with the specified maximum size.
     *
     * @param maxSize The maximum number of entries the cache can hold
     */
    public DoubleKeyedLruCache(int maxSize) {
        super(maxSize);
    }

    /**
     * Retrieves a cached value based on a pair of keys.
     *
     * @param key1 The first key component
     * @param key2 The second key component
     * @return The cached value associated with the given key pair,
     * or {@code null} if no mapping exists
     */
    public V get(final K1 key1, final K2 key2) {
        Pair<K1, K2> pair = new Pair<>(key1, key2);
        return super.get(pair);
    }

    /**
     * Checks whether a value exists for the given pair of keys.
     *
     * @param key1 The first key component
     * @param key2 The second key component
     * @return {@code true} if a mapping exists for the given keys,
     * otherwise {@code false}
     */
    public boolean containsPair(final K1 key1, final K2 key2) {
        Pair<K1, K2> pair = new Pair<>(key1, key2);
        return super.containsKey(pair);
    }

    /**
     * Stores a value in the cache associated with the given pair of keys.
     *
     * @param key1  The first key component
     * @param key2  The second key component
     * @param value The value to cache
     * @return The previous value associated with the key pair, or {@code null}
     * if none existed
     */
    public V put(final K1 key1, final K2 key2, final V value) {
        Pair<K1, K2> pair = new Pair<>(key1, key2);
        return super.put(pair, value);
    }

    /**
     * Removes the cached value associated with the given pair of keys.
     *
     * @param key1 The first key component
     * @param key2 The second key component
     * @return The removed value, or {@code null} if no mapping existed
     */
    public V removePair(final K1 key1, final K2 key2) {
        Pair<K1, K2> pair = new Pair<>(key1, key2);
        return super.remove(pair);
    }

    /**
     * Removes the entry associated with the given key pair only if it is
     * currently mapped to the specified value.
     * <p>
     * This method ensures that removal happens conditionally, preventing
     * accidental deletion if the stored value does not match the expected one.
     *
     * @param key1  The first key component
     * @param key2  The second key component
     * @param value The value that must match the currently stored value for removal
     * @return {@code true} if the entry was successfully removed, otherwise {@code false}
     */
    public boolean removePair(final K1 key1, final K2 key2, V value) {
        Pair<K1, K2> pair = new Pair<>(key1, key2);
        return super.remove(pair, value);
    }

}
