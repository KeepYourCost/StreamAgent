package core.util;

import infrastructure.singleton.Injection;
import infrastructure.singleton.Singleton;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Singleton
public class FileSplitter {
    public static final int CHUNK_SIZE = 1024;
    private static volatile FileSplitter instance;

    @Injection
    private FileSplitter() {
    }

    public static FileSplitter getInstance() {
        if (instance == null) {
            synchronized (FileSplitter.class) {
                if (instance == null) {
                    instance = new FileSplitter();
                }
            }
        }
        return instance;
    }

    public FileBuffer splitFile(String filePath) throws IOException {
        FileBuffer fileBuffer = new FileBuffer();
        byte[] fileStream = Files.readAllBytes(Paths.get(filePath));

        for (int offset = 0; offset < fileStream.length; offset += CHUNK_SIZE) {
            int chunkLength = Math.min(CHUNK_SIZE, fileStream.length - offset);
            byte[] chunk = new byte[chunkLength];

            System.arraycopy(fileStream, offset, chunk, 0, chunkLength);

            fileBuffer.addChunk(chunk);
        }

        return fileBuffer;
    }

}
