package de.craftsblock.craftscore.utils;

/**
 * A simple immutable key-value container representing a pair of two related objects.
 *
 * @param <K> The type of the key element
 * @param <V> The type of the value element
 * @author Philipp Maywald
 * @author CraftsBlock
 * @since 3.8.13
 */
public record Pair<K, V>(K key, V value) {

    /**
     * Creates a new {@link Pair} with the given key and value.
     *
     * @param key   The key element of the pair
     * @param value The value element of the pair
     */
    public Pair {
    }

    /**
     * Returns the key element of this pair.
     *
     * @return The key
     */
    @Override
    public K key() {
        return key;
    }

    /**
     * Returns the value element of this pair.
     *
     * @return The value
     */
    @Override
    public V value() {
        return value;
    }


}
