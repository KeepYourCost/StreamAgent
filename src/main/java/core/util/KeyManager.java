package core.util;

public class KeyManager {
    private static final String KEY_DELIMITER = "::";
    private static final String KEY_FORMAT = "%s" + KEY_DELIMITER + "%d";

    public static String generateKey(String filePath, int index) {
        return String.format(KEY_FORMAT, filePath, index);
    }

    public static KeyRecord parseKey(String key) {
        String[] parts = key.split(KEY_DELIMITER);
        String path = parts[0];
        int index = Integer.parseInt(parts[1]);
        return new KeyRecord(path, index);
    }
}