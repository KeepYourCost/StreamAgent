package util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FileChunkerTest {
    final FileChunker fileChunker = FileChunker.getInstance();

    @Test
    @DisplayName("파일분할_텍스트")
    void splitFileIntoChunks_text() throws URISyntaxException, IOException {
        // Given
        ClassLoader classLoader = getClass().getClassLoader();
        Path path = Paths.get(classLoader.getResource("sample/sampleText.txt").toURI());

        byte[] fileContent = Files.readAllBytes(path);
        int expectedChunkCount = (fileContent.length + FileChunker.CHUNK_SIZE - 1) / FileChunker.CHUNK_SIZE;

        // When
        List<byte[]> chunks = fileChunker.splitFileIntoChunks(path.toString());

        // Then
        // 분할된 Chunk 개수로 비교
        assertNotNull(chunks);
        assertEquals(expectedChunkCount, chunks.size());
    }

    @Test
    @DisplayName("파일분할_이미지")
    void splitFileIntoChunks_image() throws URISyntaxException, IOException {
        // Given
        ClassLoader classLoader = getClass().getClassLoader();
        Path path = Paths.get(classLoader.getResource("sample/sampleImg.png").toURI());

        byte[] fileContent = Files.readAllBytes(path);
        int expectedChunkCount = (fileContent.length + FileChunker.CHUNK_SIZE - 1) / FileChunker.CHUNK_SIZE;

        // When
        List<byte[]> chunks = fileChunker.splitFileIntoChunks(path.toString());

        // Then
        assertNotNull(chunks);
        assertEquals(expectedChunkCount, chunks.size());
    }

    @Test
    @DisplayName("파일분할_실핼파일")
    void splitFileIntoChunks_jar() throws URISyntaxException, IOException {
        // Given
        ClassLoader classLoader = getClass().getClassLoader();
        Path path = Paths.get(classLoader.getResource("sample/mysql-connector-j-8.4.0.jar").toURI());

        byte[] fileContent = Files.readAllBytes(path);
        int expectedChunkCount = (fileContent.length + FileChunker.CHUNK_SIZE - 1) / FileChunker.CHUNK_SIZE;

        // When
        List<byte[]> chunks = fileChunker.splitFileIntoChunks(path.toString());

        // Then
        assertNotNull(chunks);
        assertEquals(expectedChunkCount, chunks.size());
    }
}
