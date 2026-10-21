package de.craftsblock.craftscore.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Parses and stores command-line arguments in a key-value format.
 *
 * <p>This class supports arguments using the {@code --key value} and
 * {@code --key=value} syntaxes, depending on the selected {@link Pattern}.
 * Arguments are stored in insertion order, and duplicate keys overwrite
 * their previously stored values.</p>
 *
 * <p>Arguments without an explicitly provided value are interpreted as
 * boolean-style flags and assigned the string value {@code "true"}.
 * For example, {@code --verbose} is stored as {@code verbose=true}.</p>
 *
 * <p>All argument keys are stored without their leading {@code --} prefix.
 * The parser does not perform type conversion; all values are represented
 * as strings.</p>
 *
 * <p>Instances are initialized by parsing the supplied argument array.
 * The resulting arguments can be queried using {@link #get(String)},
 * {@link #get(String, String)}, and {@link #has(String)}. The stored
 * arguments can also be accessed through the unmodifiable view returned
 * by {@link #getMapView()}.</p>
 *
 * @author Philipp Maywald
 * @author CraftsBlock
 * @since 3.8.21
 * @see Pattern
 * @see #Arguments(String[])
 * @see #Arguments(String[], Pattern)
 */
public class Arguments {

    /**
     * Defines the syntax used to parse command-line arguments.
     *
     * <p>Each pattern determines how an argument key and its corresponding
     * value are extracted from the supplied argument array.</p>
     *
     * @see #SPACE_SEPARATED
     * @see #EQUALS_SEPARATED
     */
    public enum Pattern {
        /**
         * Parses arguments in which the key and its value are provided
         * as separate command-line tokens.
         *
         * <p>The expected syntax is:</p>
         *
         * <pre>{@code
         * --key value
         * }</pre>
         *
         * <p>The key is extracted from the argument token by removing its
         * leading {@code --} prefix. If the next token exists and does not
         * start with {@code --}, that token is consumed as the value.</p>
         *
         * <p>If no eligible next token exists, the argument is treated as
         * a boolean-style flag and assigned the value {@code "true"}.</p>
         *
         * <p>For example, the following tokens:</p>
         *
         * <pre>{@code
         * --name Alice --verbose
         * }</pre>
         *
         * <p>are interpreted as:</p>
         *
         * <pre>{@code
         * name    = "Alice"
         * verbose = "true"
         * }</pre>
         *
         * <p>A token beginning with {@code --} is never consumed as the
         * value of the preceding argument. Consequently, values that
         * themselves begin with {@code --} cannot be represented directly
         * using this pattern.</p>
         *
         * <p>An empty key, as in {@code --}, causes an
         * {@link IllegalArgumentException} to be thrown.</p>
         */
        SPACE_SEPARATED {
            @Override
            Pair<String, String> parse(String arg, String[] args, AtomicInteger index) {
                String key = arg.substring(2);

                if (key.isEmpty()) {
                    throw new IllegalArgumentException("Empty argument key");
                }

                String value;
                if (index.get() + 1 < args.length && !args[index.get() + 1].startsWith("--")) {
                    value = args[index.incrementAndGet()];
                } else {
                    value = "true";
                }

                return new Pair<>(key, value);
            }
        },

        /**
         * Parses arguments in which the key and its value are separated
         * by an equals sign within the same command-line token.
         *
         * <p>The expected syntax is:</p>
         *
         * <pre>{@code
         * --key=value
         * }</pre>
         *
         * <p>The key is extracted from the portion between the leading
         * {@code --} prefix and the first equals sign. The value consists
         * of all characters following that equals sign.</p>
         *
         * <p>If the argument does not contain an equals sign, it is treated
         * as a boolean-style flag and assigned the value {@code "true"}.</p>
         *
         * <p>For example, the following tokens:</p>
         *
         * <pre>{@code
         * --name=Alice --verbose
         * }</pre>
         *
         * <p>are interpreted as:</p>
         *
         * <pre>{@code
         * name    = "Alice"
         * verbose = "true"
         * }</pre>
         *
         * <p>Only the first equals sign is treated as the separator.
         * Additional equals signs are preserved in the value. For example,
         * {@code --expression=a=b} produces the value {@code "a=b"}.</p>
         *
         * <p>An empty value is permitted. For example,
         * {@code --name=} produces an empty string value.</p>
         */
        EQUALS_SEPARATED {
            @Override
            Pair<String, String> parse(String arg, String[] ignored, AtomicInteger ignored2) {
                int separator = arg.indexOf('=');
                String key, value;

                if (separator < 0) {
                    key = arg.substring(2);
                    value = "true";
                } else {
                    key = arg.substring(2, separator);
                    value = arg.substring(separator + 1);
                }

                if (key.isEmpty()) {
                    throw new IllegalArgumentException("Empty argument key");
                }

                return new Pair<>(key, value);
            }
        },
        ;

        /**
         * Parses a single argument token according to this pattern.
         *
         * <p>The supplied index identifies the current token in the
         * argument array. Implementations may advance this index when
         * consuming additional tokens, as required by their syntax.</p>
         *
         * @param arg   the current argument token, expected to begin with
         *              {@code --}
         * @param args  the complete argument array, used when the pattern
         *              requires access to subsequent tokens
         * @param index the mutable index of the current token; may be
         *              advanced by the implementation
         * @return a pair containing the parsed key and value, neither of
         * which includes the leading {@code --} argument prefix
         * @throws IllegalArgumentException if the argument contains an
         *                                  empty key
         */
        abstract Pair<String, String> parse(String arg, String[] args, AtomicInteger index);
    }

    private final Map<String, String> arguments = new LinkedHashMap<>();

    /**
     * Creates an {@code Arguments} instance using the default parsing
     * pattern, {@link Pattern#SPACE_SEPARATED}.
     *
     * @param args the command-line argument array to parse; must not be
     *             {@code null}
     * @throws NullPointerException     if {@code args} is {@code null}
     * @throws IllegalArgumentException if an argument does not start
     *                                  with {@code --}, or if an argument
     *                                  has an empty key
     */
    public Arguments(@NotNull String[] args) {
        this(args, Pattern.SPACE_SEPARATED);
    }

    /**
     * Creates an {@code Arguments} instance using the specified parsing
     * pattern.
     *
     * <p>The supplied argument array is parsed immediately. Every argument
     * must begin with {@code --}; otherwise, parsing fails with an
     * {@link IllegalArgumentException}.</p>
     *
     * @param args    the command-line argument array to parse; must not
     *                be {@code null}
     * @param pattern the syntax pattern used to interpret the arguments;
     *                must not be {@code null}
     * @throws NullPointerException     if {@code args} or {@code pattern}
     *                                  is {@code null}
     * @throws IllegalArgumentException if an argument does not start
     *                                  with {@code --}, or if an argument
     *                                  has an empty key
     */
    public Arguments(@NotNull String[] args, @NotNull Pattern pattern) {
        this.parse(
                Objects.requireNonNull(args, "args"),
                Objects.requireNonNull(pattern, "pattern")
        );
    }

    /**
     * Parses the supplied argument array and stores each parsed key-value
     * pair in the internal argument map.
     *
     * <p>The selected pattern is responsible for extracting the key and
     * value from each token. Patterns may advance the mutable index to
     * indicate that additional tokens have been consumed.</p>
     *
     * <p>If a key appears more than once, its most recently parsed value
     * replaces the previous value.</p>
     *
     * @param args    the argument array to parse; must not be {@code null}
     * @param pattern the pattern used to parse each argument
     * @throws IllegalArgumentException if an argument does not begin
     *                                  with {@code --}, or if parsing
     *                                  encounters an empty key
     */
    private void parse(String[] args, Pattern pattern) {
        for (AtomicInteger index = new AtomicInteger();
             index.get() < args.length;
             index.incrementAndGet()) {

            String arg = args[index.get()];
            if (!arg.startsWith("--")) {
                throw new IllegalArgumentException("Unexpected argument: " + arg);
            }

            Pair<String, String> parsed = pattern.parse(arg, args, index);
            arguments.put(parsed.key(), parsed.value());
        }
    }

    /**
     * Retrieves the value associated with the specified argument key.
     *
     * <p>The key must be provided without the leading {@code --} prefix.</p>
     *
     * @param key the argument key to look up; must not be {@code null}
     * @return the associated value, or {@code null} if the key is absent
     * @throws NullPointerException if {@code key} is {@code null}
     */
    public @Nullable String get(@NotNull String key) {
        return this.get(key, null);
    }

    /**
     * Retrieves the value associated with the specified argument key,
     * returning a fallback value if the key is absent.
     *
     * <p>The key must be provided without the leading {@code --} prefix.</p>
     *
     * @param key          the argument key to look up; must not be
     *                     {@code null}
     * @param defaultValue the value to return if the key is absent; may
     *                     be {@code null}
     * @return the associated value if the key exists; otherwise,
     * {@code defaultValue}
     * @throws NullPointerException if {@code key} is {@code null}
     */
    public @Nullable String get(@NotNull String key, @Nullable String defaultValue) {
        return arguments.getOrDefault(Objects.requireNonNull(key, "key"), defaultValue);
    }

    /**
     * Checks whether the specified argument key exists.
     *
     * <p>The key must be provided without the leading {@code --} prefix.</p>
     *
     * @param key the argument key to check; must not be {@code null}
     * @return {@code true} if the key exists; {@code false} otherwise
     * @throws NullPointerException if {@code key} is {@code null}
     */
    public boolean has(@NotNull String key) {
        return arguments.containsKey(Objects.requireNonNull(key, "key"));
    }

    /**
     * Returns an unmodifiable view of all parsed arguments.
     *
     * @return an unmodifiable view of the parsed arguments, preserving
     * their insertion order
     */
    public @UnmodifiableView @NotNull Map<String, String> getMapView() {
        return Collections.unmodifiableMap(arguments);
    }

}
