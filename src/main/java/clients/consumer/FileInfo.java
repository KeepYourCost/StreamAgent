package clients.consumer;

import util.FileBuffer;

public record FileInfo(
        String filePath,
        FileBuffer fileBuffer
) {
    public static FileInfo of(String filePath, FileBuffer fileBuffer) {
        return new FileInfo(filePath, fileBuffer);
    }
}
