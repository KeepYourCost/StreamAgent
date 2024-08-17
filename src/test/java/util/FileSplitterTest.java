package util;

import core.util.FileBuffer;
import core.util.FileSplitter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FileSplitterTest {
    final FileSplitter fileSplitter = FileSplitter.getInstance();

    @Test
    @DisplayName("파일분할_텍스트")
    void splitFileIntoChunks_text() throws URISyntaxException, IOException {
        // Given
        ClassLoader classLoader = getClass().getClassLoader();
        Path path = Paths.get(classLoader.getResource("sample/sampleText.txt").toURI());

        byte[] fileContent = Files.readAllBytes(path);
        int expectedChunkCount = (fileContent.length + FileSplitter.CHUNK_SIZE - 1) / FileSplitter.CHUNK_SIZE;

        // When
        FileBuffer fileBuffer = fileSplitter.splitFile(path.toString());

        // Then
        // 분할된 Chunk 개수로 비교
        assertNotNull(fileBuffer.getChunks());
        assertEquals(expectedChunkCount, fileBuffer.getChunkCount());
    }

    @Test
    @DisplayName("파일분할_이미지")
    void splitFileIntoChunks_image() throws URISyntaxException, IOException {
        // Given
        ClassLoader classLoader = getClass().getClassLoader();
        Path path = Paths.get(classLoader.getResource("sample/sampleImg.png").toURI());

        byte[] fileContent = Files.readAllBytes(path);
        int expectedChunkCount = (fileContent.length + FileSplitter.CHUNK_SIZE - 1) / FileSplitter.CHUNK_SIZE;

        // When
        FileBuffer fileBuffer = fileSplitter.splitFile(path.toString());

        // Then
        assertNotNull(fileBuffer.getChunks());
        assertEquals(expectedChunkCount, fileBuffer.getChunkCount());
    }

    @Test
    @DisplayName("파일분할_실핼파일")
    void splitFileIntoChunks_jar() throws URISyntaxException, IOException {
        // Given
        ClassLoader classLoader = getClass().getClassLoader();
        Path path = Paths.get(classLoader.getResource("sample/mysql-connector-j-8.4.0.jar").toURI());

        byte[] fileContent = Files.readAllBytes(path);
        int expectedChunkCount = (fileContent.length + FileSplitter.CHUNK_SIZE - 1) / FileSplitter.CHUNK_SIZE;

        // When
        FileBuffer fileBuffer = fileSplitter.splitFile(path.toString());

        // Then
        assertNotNull(fileBuffer.getChunks());
        assertEquals(expectedChunkCount, fileBuffer.getChunkCount());
    }
}
