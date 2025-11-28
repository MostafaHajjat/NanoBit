package com.nanobit;

import java.nio.file.Files;
import java.nio.file.Paths;

public class BinaryEditUtils {

    /**
     * Function to read a sequence of bytes from a file starting at a HEX index
     * @param filePath path to the binary file
     * @param hexIndex starting position in HEX (example: "1A3F")
     * @param count how many bytes to read
     * @return bytes as a HEX string (example: "12 0A FF 30")
     * @throws Exception if file reading fails or index out of range Example
     * usage: String bytes = BinaryEditUtils.readBytesAtHexIndex("ecu.bin",
     * "1A3F", 4); System.out.println("Read bytes: " + bytes);
     */
    public static String readBytesAtHexIndex(String filePath, String hexIndex, int count) throws Exception {

        byte[] data = Files.readAllBytes(Paths.get(filePath));
        int start = Integer.parseInt(hexIndex.trim(), 16);

        if (start < 0 || start + count > data.length) {
            throw new IllegalArgumentException("Index out of file range");
        }

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < count; i++) {
            int b = data[start + i] & 0xFF;
            result.append(String.format("%02X", b));
            if (i < count - 1) {
                result.append(" ");
            }
        }

        return result.toString();
    }

    /**
     * Function to replace bytes in a binary file starting at a HEX index
     *
     * @param filePath path to the binary file
     * @param hexIndex starting position in HEX (example: "1A3F")
     * @param newHexValues string of HEX bytes to write (example: "12 0A FF 30")
     * @throws Exception if file reading/writing fails or replacement out of
     * range
     *
     * Example usage: BinaryEditUtils.replaceBytesAtHexIndex("ecu.bin", "1A3F",
     * "12 0A FF 30"); System.out.println("Bytes replaced successfully!");
     */
    public static void replaceBytesAtHexIndex(String filePath, String hexIndex, String newHexValues) throws Exception {
        byte[] data = Files.readAllBytes(Paths.get(filePath));
        int start = Integer.parseInt(hexIndex.trim(), 16);

        String[] parts = newHexValues.trim().replaceAll("\\s+", " ").split(" ");
        byte[] newBytes = new byte[parts.length];

        for (int i = 0; i < parts.length; i++) {
            newBytes[i] = (byte) Integer.parseInt(parts[i], 16);
        }

        if (start < 0 || start + newBytes.length > data.length) {
            throw new IllegalArgumentException("Replacement would go out of file range");
        }

        for (int i = 0; i < newBytes.length; i++) {
            data[start + i] = newBytes[i];
        }

        Files.write(Paths.get(filePath), data);
    }

    /**
     * Function to write HEX values at nibble-level (half-byte editing)
     *
     * @param filePath path to the binary file
     * @param hexIndex nibble index, not byte index
     * @param hexValues sequence of hex digits (example: "11 22 3F")
     * @throws Exception if file reading/writing fails or index out of range
     *
     * Example usage: BinaryEditUtils.replaceNibblesAtHexIndex("ecu.bin", "4",
     * "1A 2B 3C"); This will start writing at nibble index 4, affecting
     * high/low nibbles accordingly.
     */
    public static void replaceNibblesAtHexIndex(String filePath, String hexIndex, String hexValues) throws Exception {

        byte[] data = Files.readAllBytes(Paths.get(filePath));
        int nibbleStart = Integer.parseInt(hexIndex.trim(), 16);
        String clean = hexValues.replace(" ", "").trim();

        for (int i = 0; i < clean.length(); i++) {
            int nibblePos = nibbleStart + i;
            int bytePos = nibblePos / 2;
            boolean high = (nibblePos % 2 == 0);
            int nibbleValue = Integer.parseInt("" + clean.charAt(i), 16);

            int current = data[bytePos] & 0xFF;
            int newByte = high ? (current & 0x0F) | (nibbleValue << 4) : (current & 0xF0) | nibbleValue;

            data[bytePos] = (byte) newByte;
        }

        Files.write(Paths.get(filePath), data);
    }
}
