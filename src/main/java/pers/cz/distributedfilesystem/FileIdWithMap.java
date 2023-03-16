package pers.cz.distributedfilesystem;

import java.util.*;

public class FileIdWithMap {
    private UUID fileId;
    private Map<UUID, List<String>> chunkWorkerNodeMap;

    public FileIdWithMap(UUID fileId, Map<UUID, List<String>> chunkWorkerNodeMap) {
        this.fileId = fileId;
        this.chunkWorkerNodeMap = chunkWorkerNodeMap;
    }

    public UUID getFileId() {
        return fileId;
    }

    public Map<UUID, List<String>> getChunkWorkerNodeMap() {
        return chunkWorkerNodeMap;
    }
}
