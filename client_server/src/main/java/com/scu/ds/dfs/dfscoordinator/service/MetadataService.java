package com.scu.ds.dfs.dfscoordinator.service;

import com.scu.ds.dfs.dfscoordinator.model.ChunkMapping;
import com.scu.ds.dfs.dfscoordinator.model.FileMetadata;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;

@Component
public interface MetadataService {



    FileMetadata getFileMetadata(MultipartFile file);

    ChunkMapping  getChunkDetailsForDownload(String fileName);

    ChunkMapping getChunkDetailsForDelete(String fileName);


}
