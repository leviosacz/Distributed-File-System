package pers.cz.distributedfilesystem;

import java.util.*;

public class FileMetadata {
    private UUID fileId;
    private String fileName;
    private long fileSize;
    private int chunkSize;
    private Map<UUID, List<String>> chunkWorkerNodeIdMap;

    public FileMetadata(UUID fileId, String fileName, long fileSize) {
        this.fileId = fileId;
        this.fileName = fileName;
        this.fileSize = fileSize;
    }

    public UUID getFileId() {
        return fileId;
    }

    public void setFileId(UUID fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public int getChunkSize() {
        return chunkSize;
    }

    public void setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
    }

    public Map<UUID, List<String>> getChunkWorkerNodeIdMap() {
        return chunkWorkerNodeIdMap;
    }

    public void setChunkWorkerNodeIdMap(Map<UUID, List<String>> chunkWorkerNodeIdMap) {
        this.chunkWorkerNodeIdMap = chunkWorkerNodeIdMap;
    }
}
