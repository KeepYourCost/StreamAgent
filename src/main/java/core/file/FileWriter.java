package core.file;

import infrastructure.singleton.Injection;
import infrastructure.singleton.Singleton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Singleton
public class FileWriter {
    private final FileCombiner fileCombiner;
    @Injection
    public FileWriter(
        FileCombiner fileCombiner
    ) {
        this.fileCombiner = fileCombiner;
    }

    public void write(String destPath, ByteBuffer byteBuffer) throws IOException {
        destPath = FilePathConverter.convertSafetyPath(destPath);

        makeParentDir(destPath);

        byte[] combined = fileCombiner.combine(byteBuffer);
        try (FileOutputStream fos = new FileOutputStream(destPath)) {
            fos.write(combined);
        }
    }

    private void makeParentDir(String destPath) {
        File file = new File(destPath);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
    }
}
