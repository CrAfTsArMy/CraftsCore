package de.craftsblock.craftscore.utils;

import java.util.Objects;

/**
 * A simple immutable key-value container representing a pair of two related objects.
 *
 * @param <K> The type of the key element
 * @param <V> The type of the value element
 * @author Philipp Maywald
 * @author CraftsBlock
 * @since 3.8.13
 */
public class Pair<K, V> {

    private final K key;
    private final V value;

    /**
     * Creates a new {@link Pair} with the given key and value.
     *
     * @param key   The key element of the pair
     * @param value The value element of the pair
     */
    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Returns the key element of this pair.
     *
     * @return The key
     */
    public K getKey() {
        return key;
    }

    /**
     * Returns the value element of this pair.
     *
     * @return The value
     */
    public V getValue() {
        return value;
    }

    /**
     * Compares this pair to another object for equality.
     * <p>
     * Two pairs are considered equal if both their key and value are equal.
     *
     * @param obj The object to compare with this pair
     * @return {@code true} if the given object is equal to this pair, otherwise {@code false}
     */
    @Override
    @SuppressWarnings("rawtypes")
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        Pair that = (Pair) obj;
        return Objects.equals(this.key, that.key) &&
                Objects.equals(this.value, that.value);
    }

    /**
     * Returns a hash code value for this pair based on its key and value.
     *
     * @return The hash code of this pair
     */
    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }


}
