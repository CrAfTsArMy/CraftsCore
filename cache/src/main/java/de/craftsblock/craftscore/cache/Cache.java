package de.craftsblock.craftscore.cache;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * A simple cache implementation that uses ConcurrentHashMap and ConcurrentLinkedDeque
 * to store key-value pairs with a fixed capacity. When the capacity is exceeded,
 * the least recently used items are automatically removed to make space for new entries.
 *
 * @param <K> The type of the keys in the cache.
 * @param <V> The type of the values in the cache.
 * @author Philipp Maywald
 * @author CraftsBlock
 * @version 2.0.1
 * @since 3.6#4
 */
public class Cache<K, V> {

    private final int capacity;
    private final ConcurrentHashMap<K, V> hashmap;
    private final ConcurrentLinkedDeque<K> internalQueue;

    /**
     * Constructs a new Cache object with the specified capacity.
     *
     * @param capacity The maximum number of key-value pairs the cache can hold.
     */
    public Cache(final int capacity) {
        this.capacity = capacity;
        this.hashmap = new ConcurrentHashMap<>(capacity);
        this.internalQueue = new ConcurrentLinkedDeque<>();
    }

    /**
     * Retrieves the value associated with the given key from the cache and updates its access order.
     *
     * @param key The key whose associated value is to be retrieved from the cache.
     * @return The value associated with the given key, or null if the key is not in the cache.
     */
    public V get(final K key) {
        moveToFront(key);
        return hashmap.get(key);
    }

    /**
     * Checks if the cache contains the specified key and updates its access order accordingly.
     *
     * @param key The key whose presence in the cache is to be checked.
     * @return true if the cache contains the key, otherwise false.
     */
    public boolean containsKey(final K key) {
        moveToFront(key);
        return hashmap.containsKey(key);
    }

    /**
     * Adds a key-value pair to the cache and manages its capacity. If the key is not already present
     * in the cache and the cache size exceeds its capacity, the least recently used item is removed
     * to make space for the new entry.
     *
     * @param key   The key to be added to the cache.
     * @param value The value associated with the key to be added.
     */
    public void put(final K key, final V value) {
        hashmap.put(key, value);
        // Move the key to the front of the queue to represent its recent use.
        // If the key was not already present in the queue and the cache size exceeds capacity,
        // remove the least recently used key-value pair from the cache.
        if (!moveToFront(key) && hashmap.size() > capacity) {
            K last = internalQueue.removeLast();
            remove(last);
        }
    }

    /**
     * Moves the given key to the front of the internal queue, representing its recent use.
     * If the key is already present in the queue, it is removed and added to the front.
     *
     * @param key The key to be moved to the front of the queue.
     * @return true if the key was already in the queue and moved, otherwise false.
     */
    protected boolean moveToFront(final K key) {
        boolean removed = internalQueue.remove(key);
        internalQueue.addFirst(key);
        return removed;
    }

    /**
     * Removes the key-value pair with the specified key from the cache, if present.
     *
     * @param key The key whose associated key-value pair is to be removed from the cache.
     */
    public void remove(final K key) {
        hashmap.remove(key);
        internalQueue.remove(key);
    }

    /**
     * Clears the cache, removing all key-value pairs from it.
     */
    public void clear() {
        internalQueue.forEach(this::remove);
    }

    public Set<Map.Entry<K, V>> entrySet() {
        return hashmap.entrySet();
    }

    public Set<K> keySet() {
        return hashmap.keySet();
    }

    public Collection<V> values() {
        return hashmap.values();
    }

    public int size() {
        return hashmap.size();
    }

    /**
     * Returns a String representation of the cache, based on the String representation of the HashMap.
     *
     * @return A String representation of the cache.
     */
    @Override
    public String toString() {
        return hashmap.toString();
    }
}
