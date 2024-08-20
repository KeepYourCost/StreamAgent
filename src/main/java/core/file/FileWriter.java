package core.file;

import infrastructure.singleton.Injection;
import infrastructure.singleton.Singleton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Singleton
public class FileWriter {
    @Injection
    public FileWriter(
    ) {
    }

    public void write(String destPath, byte[] bytes) throws IOException {
        destPath = FilePathConverter.convertSafetyPath(destPath);

        makeParentDir(destPath);

        try (FileOutputStream fos = new FileOutputStream(destPath)) {
            fos.write(bytes);
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
