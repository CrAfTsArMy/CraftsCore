package de.craftsblock.craftscore.buffer;

import org.jetbrains.annotations.Contract;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Utility class responsible for serializing and deserializing objects
 * into and from a compact byte-array representation.
 * <p>
 * Primitive wrapper types, {@link String} and {@link UUID} are serialized
 * in a custom binary format for performance and size efficiency.
 * All other objects are serialized using java's built-in object serialization
 * mechanism ({@link ObjectOutputStream}/{@link ObjectInputStream}).
 * </p>
 * <p>
 * Each serialized value starts with a single byte type identifier which
 * determines how the remaining bytes should be interpreted during deserialization.
 * </p>
 *
 * @author Philipp Maywald
 * @author CraftsBlock
 * @version 1.0.0
 * @see BufferUtil
 * @see ObjectOutputStream
 * @see ObjectInputStream
 * @since 3.8.13
 */
public final class ObjectSerializer {

    /**
     * Type identifier for {@link Byte} values
     */
    public static final byte BYTE_ID = 0;

    /**
     * Type identifier for {@link Short} values
     */
    public static final byte SHORT_ID = 1;

    /**
     * Type identifier for {@link Integer} values
     */
    public static final byte INTEGER_ID = 2;

    /**
     * Type identifier for {@link Long} values
     */
    public static final byte LONG_ID = 3;

    /**
     * Type identifier for {@link Float} values
     */
    public static final byte FLOAT_ID = 4;

    /**
     * Type identifier for {@link Double} values
     */
    public static final byte DOUBLE_ID = 5;

    /**
     * Type identifier for UTF-8 encoded {@link String} values
     */
    public static final byte UTF8_ID = 6;

    /**
     * Type identifier for {@link Character} values
     */
    public static final byte CHARACTER_ID = 7;

    /**
     * Type identifier for {@link Boolean} values
     */
    public static final byte BOOLEAN_ID = 8;

    /**
     * Type identifier for {@link UUID} values
     */
    public static final byte UUID_ID = 9;

    /**
     * Type identifier for {@link Class} values
     */
    public static final byte CLASS_ID = 10;

    /**
     * Type identifier for {@link Enum} values
     */
    public static final byte ENUM_ID = 11;

    /**
     * Type identifier for objects serialized using java object serialization.
     * <p>
     * This value is chosen to be {@link Byte#MAX_VALUE} to avoid collisions
     * with primitive identifiers.
     * </p>
     */
    public static final byte OBJECT_ID = Byte.MAX_VALUE;

    /**
     * Private constructor to prevent instantiation.
     */
    private ObjectSerializer() {
    }

    /**
     * Deserializes an object from the given byte array.
     * <p>
     * The first byte of the array is interpreted as a type identifier
     * which determines how the remaining bytes are read.
     * </p>
     *
     * @param data The serialized byte array
     * @return The deserialized object
     * @throws IllegalStateException If the type identifier is unknown
     * @throws UncheckedIOException  If an I/O error occurs during deserialization
     * @throws RuntimeException      If the class of a serialized object cannot be found
     */
    public static Object deserialize(byte[] data) {
        BufferUtil buffer = BufferUtil.wrap(data);
        byte id = buffer.getRaw().get();
        return switch (id) {
            case BYTE_ID -> buffer.getRaw().get();
            case SHORT_ID -> buffer.getRaw().getShort();
            case INTEGER_ID -> buffer.getRaw().getInt();
            case LONG_ID -> buffer.getRaw().getLong();
            case FLOAT_ID -> buffer.getRaw().getFloat();
            case DOUBLE_ID -> buffer.getRaw().getDouble();
            case UTF8_ID -> buffer.getUtf();
            case CHARACTER_ID -> buffer.getRaw().getChar();
            case BOOLEAN_ID -> buffer.getBoolean();
            case UUID_ID -> buffer.getUuid();
            case CLASS_ID -> {
                try {
                    yield Class.forName(buffer.getUtf());
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException("Deserialization failed: %s".formatted(e.getMessage()), e);
                }
            }
            case ENUM_ID -> {
                try {
                    Class<?> type = Class.forName(buffer.getUtf());
                    yield buffer.getEnum(type.asSubclass(Enum.class));
                } catch (ClassCastException | ClassNotFoundException e) {
                    throw new RuntimeException("Deserialization failed: %s".formatted(e.getMessage()), e);
                }
            }

            case OBJECT_ID -> {
                try (ByteArrayInputStream in = new ByteArrayInputStream(data, 1, data.length);
                     ObjectInputStream reader = new ObjectInputStream(in)) {
                    yield reader.readObject();
                } catch (IOException e) {
                    throw new UncheckedIOException("Deserialization failed: %s".formatted(e.getMessage()), e);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(("Deserialization failed: %s").formatted(e.getMessage()), e);
                }
            }

            default -> throw new IllegalStateException("Unexpected value: " + id);
        };
    }

    /**
     * Serializes the given object into a byte array.
     * <p>
     * Known primitive wrapper types, {@link String} and {@link UUID} are serialized
     * using a compact binary format.
     * All other objects are serialized using java's standard object serialization.
     * </p>
     *
     * @param value The object to serialize (must not be {@code null})
     * @return A byte array representing the serialized object
     * @throws NullPointerException If {@code value} is {@code null}
     * @throws UncheckedIOException If an I/O error occurs during serialization
     */
    @Contract("null -> fail; !null -> !null")
    public static byte[] serialize(Object value) {
        if (value == null) {
            throw new NullPointerException("Unexpected value for serialization: null");
        }

        if (value instanceof Byte b) {
            return serializeUsingByteBuffer(BYTE_ID, 1, buffer -> buffer.getRaw().put(b));
        } else if (value instanceof Short s) {
            return serializeUsingByteBuffer(SHORT_ID, 2, buffer -> buffer.getRaw().putShort(s));
        } else if (value instanceof Integer i) {
            return serializeUsingByteBuffer(INTEGER_ID, 4, buffer -> buffer.getRaw().putInt(i));
        } else if (value instanceof Long l) {
            return serializeUsingByteBuffer(LONG_ID, 8, buffer -> buffer.getRaw().putLong(l));
        } else if (value instanceof Float f) {
            return serializeUsingByteBuffer(FLOAT_ID, 4, buffer -> buffer.getRaw().putFloat(f));
        } else if (value instanceof Double d) {
            return serializeUsingByteBuffer(DOUBLE_ID, 8, buffer -> buffer.getRaw().putDouble(d));
        } else if (value instanceof Character c) {
            return serializeUsingByteBuffer(CHARACTER_ID, 2, buffer -> buffer.getRaw().putChar(c));
        } else if (value instanceof Boolean b) {
            return serializeUsingByteBuffer(BOOLEAN_ID, 1, buffer -> buffer.putBoolean(b));
        } else if (value instanceof UUID uuid) {
            return serializeUsingByteBuffer(UUID_ID, 16, buffer -> buffer.putUuid(uuid));
        } else if (value instanceof Class<?> c) {
            String name = c.getName();
            return serializeUsingByteBuffer(
                    CLASS_ID,
                    4 + name.getBytes(StandardCharsets.UTF_8).length,
                    buffer -> buffer.putUtf(name),
                    true
            );
        } else if (value.getClass().isEnum()) {
            String name = value.getClass().getName();
            return serializeUsingByteBuffer(
                    ENUM_ID,
                    4 + name.getBytes(StandardCharsets.UTF_8).length + 1,
                    buffer -> buffer.putUtf(name).putEnum((Enum<?>) value),
                    true
            );
        }

        if (value instanceof String str) {
            return serializeUsingByteBuffer(
                    UTF8_ID,
                    4 + str.getBytes(StandardCharsets.UTF_8).length,
                    buffer -> buffer.putUtf(str),
                    true
            );
        }

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             ObjectOutputStream writer = new ObjectOutputStream(out)) {
            writer.writeObject(value);

            byte[] serialized = out.toByteArray();
            return serializeUsingByteBuffer(OBJECT_ID, serialized.length, buffer -> buffer.getRaw().put(serialized));
        } catch (IOException e) {
            throw new UncheckedIOException("Serialization failed: %s".formatted(e.getMessage()), e);
        }
    }

    /**
     * Serializes a value using a {@link BufferUtil} with a fixed capacity.
     *
     * @param id       The type identifier
     * @param capacity The number of bytes required for the value
     * @param consumer A consumer that writes the value into the buffer
     * @return The serialized byte array
     */
    private static byte[] serializeUsingByteBuffer(byte id, int capacity, Consumer<BufferUtil> consumer) {
        return serializeUsingByteBuffer(id, capacity, consumer, false);
    }

    /**
     * Serializes a value using a {@link BufferUtil} with optional buffer trimming.
     *
     * @param id       The type identifier
     * @param capacity The number of bytes required for the value
     * @param consumer A consumer that writes the value into the buffer
     * @param trim     Whether the buffer should be trimmed to the actual size
     * @return The serialized byte array
     */
    private static byte[] serializeUsingByteBuffer(byte id, int capacity, Consumer<BufferUtil> consumer, boolean trim) {
        BufferUtil buffer = BufferUtil.allocate(capacity + 1).with(raw -> raw.put(id));
        consumer.accept(buffer);

        if (trim) {
            buffer.trim();
        }

        return buffer.toByteArray();
    }

}
