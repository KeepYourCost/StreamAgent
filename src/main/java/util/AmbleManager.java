package util;

public class AmbleManager {
    private static final byte PREAMBLE_PATTERN = (byte) 0xAA; // 10101010
    private static final byte SFD_PATTERN = (byte) 0xAB; // 10101011 (SFD)
    private static final byte POSTAMBLE_PATTERN = (byte) 0xF0; // 11110000

    public static byte[] generatePreamble() {
        byte[] preamble = new byte[8];
        for (int i = 0; i < 7; i++) {
            preamble[i] = PREAMBLE_PATTERN;
        }
        preamble[7] = SFD_PATTERN;
        return preamble;
    }

    public static byte[] generatePostamble() {
        byte[] postamble = new byte[4];
        for (int i = 0; i < 4; i++) {
            postamble[i] = POSTAMBLE_PATTERN;
        }
        return postamble;
    }

    public static boolean isPreamble(byte[] data) {
        if (data.length != 8) return false;
        for (int i = 0; i < 7; i++) {
            if (data[i] != PREAMBLE_PATTERN) return false;
        }
        return data[7] == SFD_PATTERN;
    }

    public static boolean isPostamble(byte[] data) {
        if (data.length != 4) return false;
        for (byte b : data) {
            if (b != POSTAMBLE_PATTERN) return false;
        }
        return true;
    }
}
