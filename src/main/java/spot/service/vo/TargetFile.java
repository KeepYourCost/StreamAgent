package spot.service.vo;

public record TargetFile(
        String path,
        FileType type
) {
    public static TargetFile of(String path, FileType type) {
        return new TargetFile(path, type);
    }

    public enum FileType {
        FILE,
        DIR
    }
}
