    package util;

    import java.io.IOException;
    import java.nio.file.Files;
    import java.nio.file.Paths;
    import java.util.ArrayList;
    import java.util.List;

    public class FileChunker {
        public static final int CHUNK_SIZE = 1024;
        private static volatile FileChunker instance;

        private FileChunker() {
        }

        public static FileChunker getInstance() {
            if (instance == null) {
                synchronized (FileChunker.class) {
                    if (instance == null) {
                        instance = new FileChunker();
                    }
                }
            }
            return instance;
        }

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
