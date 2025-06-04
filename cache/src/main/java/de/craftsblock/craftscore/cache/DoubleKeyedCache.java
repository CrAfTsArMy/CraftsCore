package de.craftsblock.craftscore.cache;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

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
 * @see Cache
 * @since 3.7.39
 */
public class DoubleKeyedCache<K1, K2, V> {

    private final int capacity;
    private final Cache<KeyPair<K1, K2>, V> internalCache;

    /**
     * Constructs a new DoubleKeyedCache with the specified capacity.
     *
     * @param capacity The maximum number of entries this cache can hold.
     */
    public DoubleKeyedCache(final int capacity) {
        this.capacity = capacity;
        this.internalCache = new Cache<>(capacity);
    }

    /**
     * Retrieves the value from the cache associated with the specified keys.
     *
     * @param key1 The first key.
     * @param key2 The second key.
     * @return The value associated with the specified keys in the cache, or null if no value is found.
     */
    public V get(final K1 key1, final K2 key2) {
        KeyPair<K1, K2> pair = new KeyPair<>(key1, key2);
        return internalCache.get(pair);
    }

    /**
     * Checks if an entry with the specified keys exists in the cache.
     *
     * @param key1 The first key.
     * @param key2 The second key.
     * @return true if an entry with the specified keys exists in the cache, otherwise false.
     */
    public boolean containsKeyPair(final K1 key1, final K2 key2) {
        KeyPair<K1, K2> pair = new KeyPair<>(key1, key2);
        return internalCache.containsKey(pair);
    }

    /**
     * Inserts a value into the cache with the specified keys.
     *
     * @param key1  The first key.
     * @param key2  The second key.
     * @param value The value to be stored in the cache.
     */
    public void put(final K1 key1, final K2 key2, final V value) {
        KeyPair<K1, K2> pair = new KeyPair<>(key1, key2);
        internalCache.put(pair, value);
    }

    /**
     * Removes the entry from the cache associated with the specified keys.
     *
     * @param key1 The first key.
     * @param key2 The second key.
     */
    public void remove(final K1 key1, final K2 key2) {
        KeyPair<K1, K2> pair = new KeyPair<>(key1, key2);
        internalCache.remove(pair);
    }

    /**
     * Clears all entries from the cache.
     */
    public void clear() {
        internalCache.clear();
    }

    /**
     * Returns the set of entries (key-value pairs) in the cache.
     *
     * @return The set of entries in the cache.
     */
    public Set<Map.Entry<KeyPair<K1, K2>, V>> entrySet() {
        return internalCache.entrySet();
    }

    /**
     * Returns the set of keys in the cache.
     *
     * @return The set of keys in the cache.
     */
    public Set<KeyPair<K1, K2>> keySet() {
        return internalCache.keySet();
    }

    /**
     * Returns the collection of values in the cache.
     *
     * @return The collection of values in the cache.
     */
    public Collection<V> values() {
        return internalCache.values();
    }

    /**
     * Returns the number of entries in the cache.
     *
     * @return The number of entries in the cache.
     */
    public int size() {
        return internalCache.size();
    }

    /**
     * Returns a string representation of the cache.
     *
     * @return A string representation of the cache.
     */
    @Override
    public String toString() {
        return internalCache.toString();
    }

    /**
     * A key pair consisting of two keys.
     *
     * @param <K1> The type of the first key.
     * @param <K2> The type of the second key.
     * @author CraftsBlock
     * @version 1.0
     * @see DoubleKeyedCache
     * @since 3.7.39
     */
    public record KeyPair<K1, K2>(K1 key1, K2 key2) {

        /**
         * Checks whether another object is equal to this key pair.
         *
         * @param obj The object to compare.
         * @return true if the object is equal to this key pair, otherwise false.
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            KeyPair<?, ?> keyPair = (KeyPair<?, ?>) obj;
            return key1.equals(keyPair.key1) && key2.equals(keyPair.key2);
        }

        /**
         * Returns a string representation of the key pair.
         *
         * @return A string representation of the key pair.
         */
        @Override
        public String toString() {
            return "(" + key1.toString() + ", " + key2.toString() + ")";
        }

    }
}
