package de.craftsblock.craftscore.id;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * The Snowflake class is responsible for generating unique IDs based on Twitter's Snowflake algorithm.
 * It utilizes a combination of timestamp, machine ID, and sequence number to ensure the uniqueness of each generated ID.
 */
public final class Snowflake {

    /**
     * The epoch timestamp (in milliseconds) when the Snowflake algorithm starts counting time.
     * It represents the starting point for the timestamp component of the Snowflake ID.
     */
    private static final long EPOCH = 1420070400000L;

    /**
     * The number of bits dedicated to the machine ID in the Snowflake ID.
     * It determines the maximum number of machines that can generate unique IDs simultaneously.
     */
    private static final long MACHINE_ID_BITS = 10;

    /**
     * The number of bits dedicated to the sequence number in the Snowflake ID.
     * It represents the maximum number of unique IDs that can be generated within the same millisecond.
     */
    private static final long SEQUENCE_BITS = 12;

    /**
     * The machine ID assigned to this instance of the Snowflake class.
     * It is determined based on the hostname of the machine.
     */
    private static final long MACHINE_ID;

    /**
     * The last timestamp (in milliseconds) at which a Snowflake ID was generated.
     * It is used to prevent multiple IDs from being generated within the same millisecond.
     */
    private static long lastTimestamp = -1L;

    /**
     * The current sequence number within the same millisecond.
     * It is incremented for each ID generated within the same millisecond to ensure uniqueness.
     */
    private static long sequence = 0L;

    static {
        // Initialize the MACHINE_ID based on the hostname of the machine.
        long temp;
        try {
            temp = calculateMachineIdFromHostname(InetAddress.getLocalHost().getHostName());
        } catch (UnknownHostException e) {
            // If an error occurs while obtaining the hostname, log the error and set MACHINE_ID to -1.
            e.printStackTrace();
            temp = -1;
        }
        MACHINE_ID = temp;
    }

    /**
     * Generates a unique Snowflake ID based on the current timestamp, machine ID, and sequence number.
     *
     * @return A unique Snowflake ID.
     */
    public static synchronized long generate() {
        // Get the current timestamp in milliseconds.
        long currentTimestamp = System.currentTimeMillis();

        // Check if the clock has moved backwards since the last generated ID.
        if (currentTimestamp < lastTimestamp)
            throw new IllegalStateException("Clock moved backwards. Refusing to generate ID.");

        // If the ID is generated within the same millisecond, increment the sequence number.
        if (currentTimestamp == lastTimestamp) {
            sequence = (sequence + 1) & ((1L << SEQUENCE_BITS) - 1);
            if (sequence == 0) currentTimestamp = waitUntilNextMillisecond(currentTimestamp);
        } else sequence = 0L;

        // Update the last timestamp to the current timestamp.
        lastTimestamp = currentTimestamp;

        // Combine the timestamp, machine ID, and sequence number to form the Snowflake ID.
        return ((currentTimestamp - EPOCH) << (MACHINE_ID_BITS + SEQUENCE_BITS))
                | (MACHINE_ID << SEQUENCE_BITS)
                | sequence;
    }

    /**
     * Waits until the next millisecond if the sequence number overflows within the same timestamp.
     *
     * @param currentTimestamp The current timestamp.
     * @return The updated timestamp.
     */
    private static long waitUntilNextMillisecond(long currentTimestamp) {
        while (currentTimestamp <= lastTimestamp) currentTimestamp = System.currentTimeMillis();
        return currentTimestamp;
    }

    /**
     * Calculates a machine ID based on the SHA-256 hash of the hostname.
     * It ensures a consistent and unique machine ID for the Snowflake algorithm.
     *
     * @param hostname The hostname of the machine.
     * @return The calculated machine ID.
     */
    private static int calculateMachineIdFromHostname(String hostname) {
        try {
            // Use SHA-256 hashing algorithm to generate a hash from the hostname.
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(hostname.getBytes());
            int machineId = 0;

            // Combine the bytes of the hash to form the machine ID.
            for (byte b : hashBytes) machineId = (machineId << 8) | (b & 0xFF);

            // Apply a bit mask to ensure the machine ID fits within the specified number of bits.
            return machineId & ((1 << MACHINE_ID_BITS) - 1);
        } catch (NoSuchAlgorithmException e) {
            // If SHA-256 algorithm is not available, log an error.
            throw new RuntimeException("SHA-256 algorithm not available.");
        }
    }

}

