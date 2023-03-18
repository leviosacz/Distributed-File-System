package com.scu.ds.dfs.dfscoordinator.model;

import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
public class FileMetadata extends ChunkMapping {

    private  Long chunkSize;
}
