package core.file;

import infrastructure.singleton.Injection;
import infrastructure.singleton.Singleton;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Singleton
public class FileReader {
    private final FileSplitter fileSplitter;
    @Injection
    public FileReader(
            FileSplitter fileSplitter
    ) {
        this.fileSplitter = fileSplitter;
    }

    public ByteBuffer read(String srcPath) throws IOException {
        srcPath = FilePathConverter.convertSafetyPath(srcPath);
        byte[] fileStream = Files.readAllBytes(Paths.get(srcPath));

        ByteBuffer byteBuffer = fileSplitter.split(fileStream);
        return byteBuffer;
    }
}
