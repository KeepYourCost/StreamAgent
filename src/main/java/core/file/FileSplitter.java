package core.file;

import infrastructure.singleton.Injection;
import infrastructure.singleton.Singleton;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Singleton
public class FileSplitter {
    public static final int CHUNK_SIZE = 1024;

    @Injection
    public FileSplitter() {
    }

    public ByteBuffer split(byte[] fileStream) throws IOException {
        ByteBuffer byteBuffer = new ByteBuffer();
        for (int offset = 0; offset < fileStream.length; offset += CHUNK_SIZE) {
            int chunkLength = Math.min(CHUNK_SIZE, fileStream.length - offset);
            byte[] chunk = new byte[chunkLength];

            System.arraycopy(fileStream, offset, chunk, 0, chunkLength);

            byteBuffer.addChunk(chunk);
        }

        return byteBuffer;
    }

}
