package com.nanobit;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class BinarySearchUtils {

    /**
     * Function to find all matching indexes in HEX Finds all positions in a
     * binary file where a pattern occurs. Bytes inside parentheses are treated
     * as wildcards.
     *
     * @param filePath path to the binary file
     * @param pattern string pattern, bytes in parentheses are wildcards
     * Example: "12 0A (FF) 34" -> matches 12 0A any_byte 34
     * @return list of HEX strings representing indexes where the pattern
     * matches
     * @throws Exception if file reading fails or invalid pattern Example usage:
     * List<String> positions = BinarySearchUtils.findPattern("ecu.bin", "12 0A
     * (FF) 34"); for(String pos : positions){ System.out.println("Pattern found
     * at HEX index: " + pos); }
     */
    public static List<String> findPattern(String filePath, String pattern) throws Exception {

        // Read the entire file into a byte array
        byte[] data = Files.readAllBytes(Paths.get(filePath));

        // mask list stores the pattern bytes
        // - actual numbers for fixed bytes
        // - null for wildcard bytes (inside parentheses)
        List<Integer> mask = new ArrayList<>();

        // Variables to parse the pattern string
        StringBuilder current = new StringBuilder(); // build each token (byte) character by character
        boolean inParens = false; // true if we are inside parentheses (wildcards)

        // Loop over each character in the pattern string
        for (char c : pattern.toCharArray()) {

            if (c == '(') { // Start of parentheses
                inParens = true;
                continue;
            }
            if (c == ')') { // End of parentheses
                inParens = false;
                continue;
            }

            if (c == ' ' || c == '\t') { // Space or tab means end of current byte token
                if (current.length() > 0) { // if we have collected some characters
                    String token = current.toString();
                    current.setLength(0); // reset for the next token

                    if (inParens) {
                        mask.add(null); // inside parentheses → wildcard
                    } else {
                        mask.add(Integer.parseInt(token, 16)); // convert hex string to integer
                    }
                }
                continue; // skip the space
            }

            // Append character to the current token
            current.append(c);
        }

        // Add the last token if exists (after the loop ends)
        if (current.length() > 0) {
            mask.add(inParens ? null : Integer.parseInt(current.toString(), 16));
        }

        // List to store all matching positions in HEX
        List<String> matches = new ArrayList<>();

        // Search through the binary file for the pattern
        for (int i = 0; i <= data.length - mask.size(); i++) {
            boolean match = true; // assume pattern matches at this position

            for (int j = 0; j < mask.size(); j++) {
                Integer m = mask.get(j);

                // If this byte is fixed and does not match, mark as no match
                if (m != null && (data[i + j] & 0xFF) != m) {
                    match = false;
                    break;
                }
            }

            // If the pattern matches at this position, add the index in HEX
            if (match) {
                matches.add(Integer.toHexString(i).toUpperCase());
            }
        }

        // Return all found positions as HEX strings
        return matches;
    }
}
