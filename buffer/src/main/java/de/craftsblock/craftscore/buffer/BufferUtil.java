package de.craftsblock.craftscore.buffer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A utility wrapper around {@link ByteBuffer} providing extended functionality.
 * <p>
 * This class is designed to simplify reading from and writing to byte buffers,
 * offering features such as VarInt/VarLong support, UTF-8 encoding, and safe
 * indexed operations that preserve buffer position.
 *
 * @author Philipp Maywald
 * @author CraftsBlock
 * @version 1.1.1
 * @see ByteBuffer
 * @since 3.8.11
 */
public class BufferUtil {

    private ByteBuffer buffer;

    /**
     * Constructs a new {@code BufferUtil} instance wrapping the given {@link ByteBuffer}.
     *
     * @param buffer The {@link ByteBuffer} to wrap.
     */
    public BufferUtil(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    /**
     * Writes a boolean to the buffer.
     *
     * @param value The boolean value to write.
     * @return The {@code BufferUtil} instance for chaining.
     */
    public BufferUtil putBoolean(boolean value) {
        this.buffer.put((byte) (value ? 1 : 0));
        return this;
    }

    /**
     * Writes a boolean at a specific index in the buffer.
     *
     * @param index This position in the buffer to write the value.
     * @param value The boolean value to write.
     * @return The {@code BufferUtil} instance for chaining.
     */
    public BufferUtil putBoolean(int index, boolean value) {
        return with(index, buffer -> this.putBoolean(value));
    }

    /**
     * Writes a variable-length integer to the buffer using VarInt encoding.
     *
     * @param value The integer value to write.
     * @return This {@code BufferUtil} instance for chaining.
     */
    public BufferUtil putVarInt(int value) {
        while ((value & -128) != 0) {
            buffer.put((byte) (value & 127 | 128));
            value >>>= 7;
        }

        buffer.put((byte) value);
        return this;
    }

    /**
     * Writes a variable-length integer at a specific index in the buffer.
     *
     * @param index The position in the buffer to write the value.
     * @param value The integer value to write.
     * @return This {@code BufferUtil} instance for chaining.
     */
    public BufferUtil putVarInt(int index, int value) {
        return with(index, buffer -> this.putVarInt(value));
    }

    /**
     * Writes a variable-length long to the buffer using VarLong encoding.
     *
     * @param value The long value to write.
     * @return This {@code BufferUtil} instance for chaining.
     */
    public BufferUtil putVarLong(long value) {
        while ((value & -128) != 0) {
            buffer.put((byte) (value & 127L | 128));
            value >>>= 7;
        }

        buffer.put((byte) value);
        return this;
    }

    /**
     * Writes a variable-length long at a specific index in the buffer.
     *
     * @param index The position in the buffer to write the value.
     * @param value The long value to write.
     * @return This {@code BufferUtil} instance for chaining.
     */
    public BufferUtil putVarLong(int index, long value) {
        return with(index, buffer -> this.putVarLong(value));
    }

    /**
     * Writes a UTF-8 encoded string to the buffer, prefixed with its length as a VarInt.
     *
     * @param value The string value to write.
     * @return This {@code BufferUtil} instance for chaining.
     */
    public BufferUtil putUtf(String value) {
        this.putVarInt(value.getBytes(StandardCharsets.UTF_8).length);
        this.putCharSequence(value, StandardCharsets.UTF_8);

        return this;
    }

    /**
     * Writes a UTF-8 encoded string at a specific index in the buffer.
     *
     * @param index The buffer index to write at.
     * @param value The string value to write.
     * @return This {@code BufferUtil} instance for chaining.
     */
    public BufferUtil putUtf(int index, String value) {
        return this.with(index, () -> this.putUtf(value));
    }

    /**
     * Writes a character sequence using the specified charset.
     *
     * @param value   The character sequence to write.
     * @param charset The charset to use for encoding.
     * @return This {@code BufferUtil} instance for chaining.
     */
    public BufferUtil putCharSequence(CharSequence value, Charset charset) {
        buffer.put(value.toString().getBytes(charset));
        return this;
    }

    /**
     * Writes a character sequence at a specific index using the specified charset.
     *
     * @param index   The buffer index to write at.
     * @param value   The character sequence to write.
     * @param charset The charset to use for encoding.
     * @return This {@code BufferUtil} instance for chaining.
     */
    public BufferUtil putCharSequence(int index, CharSequence value, Charset charset) {
        return this.with(index, () -> this.putCharSequence(value, charset));
    }

    /**
     * Writes an enum value by its ordinal index.
     *
     * @param t   The enum value to write.
     * @param <T> The enum type.
     * @return This {@code BufferUtil} instance for chaining.
     */
    public <T extends Enum<?>> BufferUtil putEnum(T t) {
        buffer.putInt(t.ordinal());
        return this;
    }

    /**
     * Writes an enum value at a specific index by its ordinal index.
     *
     * @param index The buffer index to write at.
     * @param t     The enum value to write.
     * @param <T>   The enum type.
     * @return This {@code BufferUtil} instance for chaining.
     */
    public <T extends Enum<?>> BufferUtil putEnum(int index, T t) {
        return this.with(index, () -> this.putEnum(t));
    }

    /**
     * Writes the given {@link UUID} into the underlying buffer.
     * <p>
     * The {@link UUID} is encoded as two consecutive {@code long} values.
     * First the most significant bits, then the least significant bits.
     *
     * @param uuid the UUID to write into the buffer
     * @return this {@code BufferUtil} instance for method chaining
     * @since 3.8.13
     */
    public BufferUtil putUuid(UUID uuid) {
        buffer.putLong(uuid.getMostSignificantBits());
        buffer.putLong(uuid.getLeastSignificantBits());
        return this;
    }

    /**
     * Writes the given {@link UUID} into the underlying buffer at the specified index.
     * <p>
     * The {@link UUID} is encoded as two consecutive {@code long} values.
     * First the most significant bits, then the least significant bits.
     *
     * @param index the position at which the UUID should be written
     * @param uuid  the UUID to write into the buffer
     * @return this {@code BufferUtil} instance for method chaining
     * @since 3.8.13
     */
    public BufferUtil putUuid(int index, UUID uuid) {
        return with(index, () -> this.putUuid(uuid));
    }

    /**
     * Reads a specific number of bytes from the buffer.
     *
     * @param n The number of bytes to read.
     * @return A byte array containing the read data.
     */
    public byte[] getNBytes(int n) {
        byte[] dst = new byte[n];
        buffer.get(dst);
        return dst;
    }

    /**
     * Reads a specific number of bytes from the buffer at a given index.
     *
     * @param index The starting index.
     * @param n     The number of bytes to read.
     * @return A byte array containing the read data.
     */
    public byte[] getNBytes(int index, int n) {
        return map(index, () -> getNBytes(n));
    }

    /**
     * Reads a boolean value from the buffer.
     *
     * @return {@code true} if the byte is 1, {@code false} if 0.
     * @throws IllegalStateException If the byte does not represent a valid boolean.
     * @since 3.8.13
     */
    public boolean getBoolean() {
        byte value = buffer.get();
        return switch (value) {
            case 0 -> true;
            case 1 -> false;
            default -> throw new IllegalStateException("%s is not a valid boolean state! (Expected 0 or 1)"
                    .formatted(value));
        };
    }

    /**
     * Reads a boolean value from a specific index in the buffer.
     *
     * @param index The position in the buffer.
     * @return The boolean value.
     * @throws IllegalStateException If the byte does not represent a valid boolean.
     * @since 3.8.13
     */
    public boolean getBoolean(int index) {
        return map(index, () -> getBoolean());
    }

    /**
     * Reads a VarInt (variable-length integer) from the buffer.
     *
     * @return The decoded integer.
     */
    public int getVarInt() {
        return (int) this.getVarNumber(5);
    }

    /**
     * Reads a VarInt at a specific index in the buffer.
     *
     * @param index The position to read from.
     * @return The decoded integer.
     */
    public int getVarInt(int index) {
        return this.map(index, () -> getVarInt());
    }

    /**
     * Reads a VarLong (variable-length long) from the buffer.
     *
     * @return The decoded long value.
     */
    public long getVarLong() {
        return this.getVarNumber(10);
    }

    /**
     * Reads a VarLong at a specific index in the buffer.
     *
     * @param index The position to read from.
     * @return The decoded long value.
     */
    public long getVarLong(int index) {
        return this.map(index, () -> getVarLong());
    }

    /**
     * Internal helper to read a variable-length encoded number.
     *
     * @param maxSize The maximum number of bytes allowed for decoding.
     * @return The decoded number.
     */
    private long getVarNumber(long maxSize) {
        int numRead = 0;
        long result = 0;

        byte read;
        do {
            read = buffer.get();
            long value = (read & 127);
            result |= (value << (7 * numRead));

            if (++numRead > maxSize)
                throw new RuntimeException("VarLong is too large");
        } while ((read & 128) != 0);

        return result;
    }

    /**
     * Reads a UTF-8 encoded string from the buffer.
     *
     * @return The decoded string.
     */
    public String getUtf() {
        return getUtf(getVarInt());
    }

    /**
     * Reads a UTF-8 encoded string starting at a specific index.
     *
     * @param index The buffer index to read from.
     * @return The decoded string.
     */
    public String getUtfAt(int index) {
        return this.map(index, () -> getUtf());
    }

    /**
     * Reads a UTF-8 encoded string of a specified length.
     *
     * @param length The number of bytes to read.
     * @return The decoded string.
     */
    public String getUtf(int length) {
        return getCharSequence(length, StandardCharsets.UTF_8);
    }

    /**
     * Reads a UTF-8 encoded string of a specified length from a given index.
     *
     * @param index  The buffer index.
     * @param length The number of bytes to read.
     * @return The decoded string.
     */
    public String getUtfAt(int index, int length) {
        return this.map(index, () -> getUtf(length));
    }

    /**
     * Reads a character sequence using the given charset.
     *
     * @param length  The number of bytes to read.
     * @param charset The charset used for decoding.
     * @return The decoded string.
     */
    public String getCharSequence(int length, Charset charset) {
        return new String(getNBytes(length), charset);
    }

    /**
     * Reads a character sequence from a given index using the specified charset.
     *
     * @param index   The buffer index.
     * @param length  The number of bytes to read.
     * @param charset The charset to use.
     * @return The decoded string.
     */
    public String getCharSequence(int index, int length, Charset charset) {
        return map(index, () -> this.getCharSequence(length, charset));
    }

    /**
     * Reads an enum constant from the buffer by ordinal index.
     *
     * @param type The enum class.
     * @param <T>  The enum type.
     * @return The corresponding enum constant.
     */
    public <T extends Enum<?>> T getEnum(Class<T> type) {
        return type.getEnumConstants()[buffer.getInt()];
    }

    /**
     * Reads an enum constant from a specific buffer index by ordinal index.
     *
     * @param index The buffer index.
     * @param type  The enum class.
     * @param <T>   The enum type.
     * @return The corresponding enum constant.
     */
    public <T extends Enum<?>> T getEnum(int index, Class<T> type) {
        return this.map(index, () -> getEnum(type));
    }

    /**
     * Reads a UUID consisting of two longs.
     *
     * @return The decoded {@link UUID}.
     */
    public UUID getUuid() {
        return new UUID(buffer.getLong(), buffer.getLong());
    }

    /**
     * Reads a UUID from a specific index.
     *
     * @param index The buffer index.
     * @return The decoded {@link UUID}.
     */
    public UUID getUuid(int index) {
        return this.map(index, () -> getUuid());
    }

    /**
     * Reads the remaining bytes in the buffer.
     *
     * @return An array containing the remaining bytes.
     * @since 3.8.13
     */
    public byte[] getRemainingBytes() {
        int remaining = buffer.remaining();

        byte[] data = new byte[remaining];
        buffer.get(data);

        return data;
    }

    /**
     * Reads the remaining bytes in the buffer starting at a specific index.
     *
     * @param index The index to start reading from.
     * @return An array containing the remaining bytes.
     * @since 3.8.13
     */
    public byte[] getRemainingBytes(int index) {
        return this.map(index, () -> getRemainingBytes());
    }

    /**
     * Checks whether the underlying {@link ByteBuffer} has at least the specified
     * number of bytes remaining between its current position and its limit.
     *
     * @param bytes The minimum number of bytes required to be available for reading or writing
     * @return {@code true} if {@code buffer.remaining() >= bytes}, otherwise {@code false}
     * @since 3.8.13
     */
    public boolean hasRemainingBytes(int bytes) {
        return buffer.remaining() >= bytes;
    }

    /**
     * Checks whether the underlying {@link ByteBuffer} has any bytes remaining
     * between its current position and its limit.
     *
     * @return {@code true} if at least one byte is available, otherwise {@code false}
     * @since 3.8.13
     */
    public boolean hasRemainingBytes() {
        return buffer.hasRemaining();
    }

    /**
     * Ensures that the underlying {@link ByteBuffer} has at least the specified
     * number of remaining bytes available. If the current buffer does not have
     * enough space, it will be expanded automatically.
     *
     * <p>This version uses a default step size of 4096 bytes for rounding
     * the new buffer capacity.</p>
     *
     * @param needed The minimum number of bytes that should be available.
     * @return This {@code BufferUtil} instance for chaining.
     * @throws OutOfMemoryError if the buffer would need to exceed {@link Integer#MAX_VALUE}.
     */
    public BufferUtil ensure(@Range(from = 0, to = Integer.MAX_VALUE) int needed) {
        return ensure(needed, 4096);
    }

    /**
     * Ensures that the underlying {@link ByteBuffer} has at least the specified
     * number of remaining bytes available. If the current buffer does not have
     * enough space, it will be expanded automatically. The new buffer capacity
     * is rounded up to a multiple of {@code steps} for predictable allocation sizes.
     *
     * <p>The buffer's byte order is preserved when expanding.</p>
     *
     * @param needed The minimum number of bytes that should be available.
     * @param steps  The step size used to round up the new buffer capacity; must be >= 1.
     * @return This {@code BufferUtil} instance for chaining.
     * @throws OutOfMemoryError if the buffer would need to exceed {@link Integer#MAX_VALUE}.
     * @since 3.8.13
     */
    public BufferUtil ensure(@Range(from = 0, to = Integer.MAX_VALUE) int needed,
                             @Range(from = 1, to = Integer.MAX_VALUE) int steps) {
        if (buffer.remaining() >= needed)
            return this;

        int position = buffer.position();
        int required = position + needed;

        int capacity = buffer.capacity();
        while (capacity < required) {
            capacity <<= 1;

            if (capacity < 0)
                throw new OutOfMemoryError("Buffer too large");
        }

        int remainder = capacity % steps;
        if (capacity > steps && remainder != 0)
            capacity += steps - remainder;

        ByteBuffer expanded = ByteBuffer.allocate(capacity).order(buffer.order());
        buffer.flip();
        expanded.put(buffer);

        this.buffer = expanded;
        return this;
    }

    /**
     * Trims the buffer to its current position, creating a new buffer containing only
     * the written data.
     *
     * @return This {@code BufferUtil} instance for chaining.
     */
    public BufferUtil trim() {
        buffer.flip();

        byte[] trimmed = new byte[buffer.remaining()];
        buffer.get(trimmed);

        this.buffer = ByteBuffer.wrap(trimmed);
        return this;
    }

    /**
     * Skips a number of bytes by advancing the buffer position.
     *
     * @param bytes The number of bytes to skip.
     * @return This {@code BufferUtil} instance for chaining.
     */
    public BufferUtil skip(int bytes) {
        buffer.position(buffer.position() + bytes);
        return this;
    }

    /**
     * Creates a duplicate copy of this {@code BufferUtil} with a duplicated buffer.
     *
     * @return A new {@code BufferUtil} instance wrapping the duplicate buffer.
     */
    public BufferUtil copy() {
        return of(buffer.duplicate());
    }

    /**
     * Executes a {@link Consumer} operation on the wrapped buffer.
     *
     * @param consumer The consumer to execute.
     * @return This {@code BufferUtil} instance for chaining.
     */
    public BufferUtil with(Consumer<ByteBuffer> consumer) {
        consumer.accept(buffer);
        return this;
    }

    /**
     * Executes a {@link Consumer} operation at a specific buffer index,
     * restoring the position afterward.
     *
     * @param index    The index to temporarily set.
     * @param consumer The consumer to execute.
     * @return This {@code BufferUtil} instance for chaining.
     */
    public BufferUtil with(int index, Consumer<ByteBuffer> consumer) {
        buffer.mark().position(index);
        consumer.accept(buffer);
        buffer.reset();

        return this;
    }

    /**
     * Executes a {@link Runnable} at a specific buffer index, restoring position afterward.
     *
     * @param index    The index to temporarily set.
     * @param runnable The runnable to execute.
     * @return This {@code BufferUtil} instance for chaining.
     */
    private BufferUtil with(int index, Runnable runnable) {
        return this.with(index, buffer -> runnable.run());
    }

    /**
     * Maps the buffer content using a provided mapping function.
     *
     * @param mapper The mapping function.
     * @param <R>    The result type.
     * @return The mapping result.
     */
    public <R> R map(Function<ByteBuffer, R> mapper) {
        return mapper.apply(buffer);
    }

    /**
     * Maps a portion of the buffer content at a specific index using a mapping function.
     * The buffer position is restored after mapping.
     *
     * @param index  The buffer index.
     * @param mapper The mapping function.
     * @param <R>    The result type.
     * @return The mapping result.
     */
    public <R> R map(int index, Function<ByteBuffer, R> mapper) {
        buffer.mark().position(index);
        R mapped = mapper.apply(buffer);
        buffer.reset();

        return mapped;
    }

    /**
     * Maps a portion of the buffer content at a specific index using a supplier.
     *
     * @param index  The buffer index.
     * @param mapper The supplier function.
     * @param <R>    The result type.
     * @return The mapping result.
     */
    private <R> R map(int index, Supplier<R> mapper) {
        return this.map(index, buffer -> mapper.get());
    }

    /**
     * Sets the buffer byte order to little-endian.
     *
     * @return This {@code BufferUtil} instance for chaining.
     */
    public BufferUtil toLittleEndian() {
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        return this;
    }

    /**
     * Sets the buffer byte order to big-endian.
     *
     * @return This {@code BufferUtil} instance for chaining.
     */
    public BufferUtil toBigEndian() {
        buffer.order(ByteOrder.BIG_ENDIAN);
        return this;
    }

    /**
     * Returns a string representation of this buffer utility.
     *
     * @return A string describing the buffer.
     */
    @Override
    public String toString() {
        return "BufferUtil{buffer=" + buffer + '}';
    }

    /**
     * Converts the buffer content into a hexadecimal string representation.
     *
     * @return A string containing the buffer’s bytes in hexadecimal format.
     */
    public String toHexString() {
        ByteBuffer dup = buffer.duplicate();
        dup.rewind();

        StringBuilder sb = new StringBuilder();
        while (dup.hasRemaining())
            sb.append(String.format("%02X ", dup.get()));

        return sb.toString().trim();
    }

    /**
     * Converts the buffer content into a byte array.
     *
     * @return A byte array containing the current buffer content.
     */
    public byte[] toByteArray() {
        ByteBuffer dup = buffer.duplicate();
        dup.flip();

        byte[] data = new byte[dup.remaining()];
        dup.get(data);
        return data;
    }

    /**
     * Returns the raw wrapped {@link ByteBuffer}.
     *
     * @return The wrapped buffer.
     */
    public ByteBuffer getRaw() {
        return buffer;
    }

    /**
     * Creates a new {@code BufferUtil} instance from an existing {@link ByteBuffer}.
     *
     * @param buffer The buffer to wrap.
     * @return A new {@code BufferUtil} instance.
     */
    public static BufferUtil of(ByteBuffer buffer) {
        return new BufferUtil(buffer);
    }

    /**
     * Creates a new {@code BufferUtil} instance from a {@link Supplier} of {@link ByteBuffer}.
     *
     * @param bufSupplier The supplier providing the buffer.
     * @return A new {@code BufferUtil} instance.
     */
    public static BufferUtil of(Supplier<ByteBuffer> bufSupplier) {
        return of(bufSupplier.get());
    }

    /**
     * Creates a new {@code BufferUtil} instance using a function that generates a {@link ByteBuffer}
     * based on an input parameter.
     *
     * @param bufFunction The function generating the buffer.
     * @param t           The input value.
     * @param <T>         The input type.
     * @return A new {@code BufferUtil} instance.
     */
    public static <T> BufferUtil of(Function<T, ByteBuffer> bufFunction, T t) {
        return of(bufFunction.apply(t));
    }

    /**
     * Creates a new {@code BufferUtil} instance using a bi-function that generates a {@link ByteBuffer}
     * based on two input parameters.
     *
     * @param bufFunction The bi-function generating the buffer.
     * @param t           The first input value.
     * @param u           The second input value.
     * @param <T>         The first input type.
     * @param <U>         The second input type.
     * @return A new {@code BufferUtil} instance.
     */
    public static <T, U> BufferUtil of(BiFunction<T, U, ByteBuffer> bufFunction, T t, U u) {
        return of(bufFunction.apply(t, u));
    }

}
