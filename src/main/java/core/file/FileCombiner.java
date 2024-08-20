package core.file;

import infrastructure.singleton.Injection;
import infrastructure.singleton.Singleton;

@Singleton
public class FileCombiner {
    @Injection
    public FileCombiner() {
    }

    public byte[] combine(ByteBuffer byteBuffer) {
        long totalSize = byteBuffer.getBufferSize();
        byte[] combined = new byte[(int) totalSize];

        int offset = 0;
        for (byte[] chunk : byteBuffer.getChunks()) {
            System.arraycopy(chunk, 0, combined, offset, chunk.length);
            offset += chunk.length;
        }

        return combined;
    }
}
