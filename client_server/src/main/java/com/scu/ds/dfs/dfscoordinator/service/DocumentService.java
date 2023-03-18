package com.scu.ds.dfs.dfscoordinator.service;

import com.scu.ds.dfs.dfscoordinator.model.ChunkMapping;
import com.scu.ds.dfs.dfscoordinator.model.FileMetadata;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.web.multipart.MultipartFile;

public interface DocumentService {

    String uploadDocument(MultipartFile file, FileMetadata fileMetadata);

    byte[] downloadDocument(ChunkMapping chunkMapping);

    String deleteDocument(ChunkMapping chunkMapping);

}
