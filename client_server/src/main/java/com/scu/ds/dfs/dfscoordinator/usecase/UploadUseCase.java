package com.scu.ds.dfs.dfscoordinator.usecase;

import com.scu.ds.dfs.dfscoordinator.exception.DocumentServiceException;
import com.scu.ds.dfs.dfscoordinator.exception.MetadataServiceException;
import com.scu.ds.dfs.dfscoordinator.model.FileMetadata;
import com.scu.ds.dfs.dfscoordinator.service.DocumentService;
import com.scu.ds.dfs.dfscoordinator.service.MetadataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Component
public class UploadUseCase {
    private final MetadataService metadataService;
    private final DocumentService documentService;

    public String upload(MultipartFile file) {
        FileMetadata fileMetadata = null;
        String response = null;
        try {
            fileMetadata = metadataService.getFileMetadata(file);
            response = documentService.uploadDocument(file, fileMetadata);
        } catch (MetadataServiceException me) {
            throw me;
        } catch (DocumentServiceException de) {
            metadataService.getChunkDetailsForDelete(file.getName());
            throw de;
        }
        return response;
    }
}
