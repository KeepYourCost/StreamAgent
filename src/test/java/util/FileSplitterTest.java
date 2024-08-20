package util;

import core.file.ByteBuffer;
import core.file.FileSplitter;
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
    final FileSplitter fileSplitter = new FileSplitter();

    @Test
    @DisplayName("파일분할_텍스트")
    void splitFileIntoChunks_text() throws URISyntaxException, IOException {
        // Given
        ClassLoader classLoader = getClass().getClassLoader();
        Path path = Paths.get(classLoader.getResource("sample/sampleText.txt").toURI());

        byte[] fileContent = Files.readAllBytes(path);
        int expectedChunkCount = (fileContent.length + FileSplitter.CHUNK_SIZE - 1) / FileSplitter.CHUNK_SIZE;

        // When
        ByteBuffer byteBuffer = fileSplitter.split(path.toString());

        // Then
        // 분할된 Chunk 개수로 비교
        assertNotNull(byteBuffer.getChunks());
        assertEquals(expectedChunkCount, byteBuffer.getChunkCount());
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
        ByteBuffer byteBuffer = fileSplitter.split(path.toString());

        // Then
        assertNotNull(byteBuffer.getChunks());
        assertEquals(expectedChunkCount, byteBuffer.getChunkCount());
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
        ByteBuffer byteBuffer = fileSplitter.split(path.toString());

        // Then
        assertNotNull(byteBuffer.getChunks());
        assertEquals(expectedChunkCount, byteBuffer.getChunkCount());
    }
}
