package pers.cz.distributedfilesystem;

import java.util.*;

public class FileMetadataWithMap {
    private UUID fileId;
    private long chunkSize;
    private Map<UUID, List<String>> chunkWorkerNodeMap;

    public FileMetadataWithMap(UUID fileId, long chunkSize, Map<UUID, List<String>> chunkWorkerNodeMap) {
        this.fileId = fileId;
        this.chunkSize = chunkSize;
        this.chunkWorkerNodeMap = chunkWorkerNodeMap;
    }

    public UUID getFileId() {
        return fileId;
    }

    public long getChunkSize() {
        return chunkSize;
    }

    public Map<UUID, List<String>> getChunkWorkerNodeMap() {
        return chunkWorkerNodeMap;
    }
}
