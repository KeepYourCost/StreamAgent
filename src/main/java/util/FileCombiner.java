package util;

import java.io.FileOutputStream;
import java.io.IOException;

public class FileCombiner {
    public static final int CHUNK_SIZE = 1024;
    private static volatile FileCombiner instance;

    private FileCombiner() {
    }

    public static FileCombiner getInstance() {
        if (instance == null) {
            synchronized (FileCombiner.class) {
                if (instance == null) {
                    instance = new FileCombiner();
                }
            }
        }
        return instance;
    }

    public void writeFile(String filePath, FileBuffer fileBuffer) throws IOException {
        byte[] combined = this.combineBuffer(fileBuffer);
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(combined);
        }
    }

    private byte[] combineBuffer(FileBuffer fileBuffer) {
        long totalSize = fileBuffer.getBufferSize();
        byte[] combined = new byte[(int) totalSize];

        int offset = 0;
        for (byte[] chunk : fileBuffer.getChunks()) {
            System.arraycopy(chunk, 0, combined, offset, chunk.length);
            offset += chunk.length;
        }

        return combined;
    }


}
