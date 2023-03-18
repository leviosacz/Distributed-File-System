package com.scu.ds.dfs.dfscoordinator.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ChunkMapping {

    String fileId;

    Map<String, List<String>> chunkWorkerNodeMap;
}
