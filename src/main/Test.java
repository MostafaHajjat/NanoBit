package main;

import java.util.List;

import static com.nanobit.BinaryEditUtils.*;
import static com.nanobit.BinarySearchUtils.findPattern;

public class Test {

    public static void main(String[] args) {

        String path = "D:/myBinFile.bin";

        System.out.println("Hello, World!");

        // String pattern = "12 (FF) EE EA 34 BA 44 (C0 32) 0F";
        String pattern = "40 C6 (27) 00 38 C7 (14 30) 1B B8";
        String pattern2 = "40 C6 1F 00 38 C7 61 7B 1B";

        try {
            // Find all matching indexes for each pattern
            List<String> matches1 = findPattern(path, pattern);
            List<String> matches2 = findPattern(path, pattern2);

            System.out.println("Matches for pattern1: " + matches1.get(0));
            System.out.println("Matches for pattern1: " + matches2);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            System.out.println(readBytesAtHexIndex(path, "209890", 10));

            replaceBytesAtHexIndex(path, "1", "FF FF FF FF");
            replaceNibblesAtHexIndex(path, "1", "11 11 11 11");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
