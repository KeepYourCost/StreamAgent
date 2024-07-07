package util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileChunker {
    private static final int CHUNK_SIZE = 1024;

    public List<byte[]> splitFileIntoChunks(String filePath) throws IOException {
        List<byte[]> chunks = new ArrayList<>();
        byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));

        for (int offset = 0; offset < fileBytes.length; offset += CHUNK_SIZE) {
            int chunkLength = Math.min(CHUNK_SIZE, fileBytes.length - offset);
            byte[] chunk = new byte[chunkLength];

            System.arraycopy(fileBytes, offset, chunk, 0, chunkLength);

            chunks.add(chunk);
        }

        return chunks;
    }
}
