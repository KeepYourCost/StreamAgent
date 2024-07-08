package util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FileCombinerTest {
//    @Test
//    @DisplayName("파일 쓰기 테스트")
//    void testWriteFile() throws IOException {
//        // Given
//        FileBuffer fileBuffer = new FileBuffer();
//        byte[] chunk1 = new byte[]{65, 66, 67};
//        byte[] chunk2 = new byte[]{68, 69, 70};
//        fileBuffer.addChunk(chunk1);
//        fileBuffer.addChunk(chunk2);
//
//        String filePath = "sample/test_output.txt";
//        FileCombiner fileCombiner = FileCombiner.getInstance();
//
//        // When
//        fileCombiner.writeFile(filePath, fileBuffer);
//
//        // Cleanup
//        Files.delete(Paths.get(filePath));
//    }

    @Test
    @DisplayName("싱글톤 인스턴스 테스트")
    void testSingletonInstance() {
        FileCombiner instance1 = FileCombiner.getInstance();
        FileCombiner instance2 = FileCombiner.getInstance();

        assertSame(instance1, instance2);
    }
}