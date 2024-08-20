package core.clients.consumer;

import core.file.ByteBuffer;

public record FileInfo(
        String filePath,
        ByteBuffer byteBuffer
) {
    public static FileInfo of(String filePath, ByteBuffer byteBuffer) {
        return new FileInfo(filePath, byteBuffer);
    }
}
