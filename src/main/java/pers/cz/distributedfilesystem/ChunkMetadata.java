package pers.cz.distributedfilesystem;

import java.util.*;

public class ChunkMetadata {
    private UUID chunkId;
    private long chunkSize;
    private UUID fileId;
    private List<String> workerNodeIds;

    public ChunkMetadata(UUID chunkId, int chunkSize, UUID fileID, List<String> workerNodeIds) {
        this.chunkId = chunkId;
        this.chunkSize = chunkSize;
        this.fileId = fileID;
        this.workerNodeIds = workerNodeIds;
    }

    public UUID getChunkId() {
        return chunkId;
    }

    public void setChunkId(UUID chunkId) {
        this.chunkId = chunkId;
    }

    public long getChunkSize() {
        return chunkSize;
    }

    public void setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
    }

    public List<String> getWorkerNodeIds() {
        return workerNodeIds;
    }

    public void setWorkerNodeIds(List<String> workerNodeIds) {
        this.workerNodeIds = workerNodeIds;
    }

    public UUID getFileId() {
        return fileId;
    }

    public void setFileId(UUID fileId) {
        this.fileId = fileId;
    }

}
