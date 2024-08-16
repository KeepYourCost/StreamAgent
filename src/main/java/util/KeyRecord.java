package util;

public record KeyRecord(
        String path,
        int index
) {
    public static KeyRecord of(String path, int index) {
        return new KeyRecord(path, index);
    }
}
