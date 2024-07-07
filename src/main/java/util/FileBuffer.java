package util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class FileBuffer {
    private final List<byte[]> chunks;

    public FileBuffer() {
        this.chunks = new ArrayList<>();
    }

    public FileBuffer(List<byte[]> chunks) {
        this.chunks = new ArrayList<>(chunks);
    }

    public void addChunk(byte[] chunk) {
        chunks.add(chunk);
    }

    public List<byte[]> getChunks() {
        return Collections.unmodifiableList(chunks);
    }

    public int getChunkCount() {
        return chunks.size();
    }

    public long getBufferSize() {
        return chunks.stream().mapToLong(chunk -> chunk.length).sum();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileBuffer that = (FileBuffer) o;
        return Objects.equals(chunks, that.chunks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chunks);
    }
}
