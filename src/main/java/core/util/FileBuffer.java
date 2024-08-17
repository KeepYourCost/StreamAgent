package core.util;

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

    public FileBuffer(byte[] chunks) {
        this.chunks = new ArrayList<>(Collections.singleton(chunks));
    }

    /**
     * Chunks에 byte배열 값을 추가
     * @param chunk 추가할 byte 배열
     */
    public void addChunk(byte[] chunk) {
        chunks.add(chunk);
    }

    /**
     * chunks 값을 조회한다.
     * 이때 수정불가능한 상태로 조회한다.
     * @return 수정할 수 없는 상태의 chunks 리스트
     */
    public List<byte[]> getChunks() {
        return Collections.unmodifiableList(chunks);
    }

    /**
     * chunks 데이터를 초기화한다.
     */
    public void clearChunks() {
        this.chunks.clear();
    }

    /**
     * chunks 리스트의 index 개수를 조회한다.
     * @return chunks 리스트의 크기
     */
    public int getChunkCount() {
        return chunks.size();
    }

    /**
     * chunk buffer 전체의 byte 크기를 조회한다.
     * @return chunks 리스트에 포함된 모든 byte 배열의 총 크기
     */
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
