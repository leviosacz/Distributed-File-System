package pers.cz.distributedfilesystem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/metadata")
public class MetadataController {

    private final MetadataService metadataService;

    @Autowired
    public MetadataController(MetadataService metadataService) {
        this.metadataService = metadataService;
    }

    @PostMapping("/upload")
    public FileMetadataWithMap uploadFile(@RequestParam String fileName, @RequestParam long fileSize) {
        return metadataService.addFile(fileName, fileSize);
    }

    @GetMapping("/delete/{fileName}")
    public FileIdWithMap deleteFile(@PathVariable String fileName) {
        return metadataService.deleteFile(fileName);
    }

    @GetMapping("/download/{fileName}")
    public FileIdWithMap downloadFile(@PathVariable String fileName) {
        return metadataService.getFile(fileName);
    }

    @PostMapping("/addWorkerNode")
    public ResponseEntity<String> addWorkerNode(@RequestParam String nodeId, @RequestParam long availableSpace) {
        metadataService.addWorkerNode(nodeId, availableSpace);
        return ResponseEntity.ok("Worker node added successfully");
    }

//    @PostMapping("/file/{fileId}/chunk")
//    public ChunkMetadata addChunk(@PathVariable UUID fileId, @RequestParam int chunkSize) {
//        return metadataService.addChunk(fileId, chunkSize);
//    }
//
//    @DeleteMapping("/file/{fileId}/chunk/{chunkId}")
//    public boolean deleteChunk(@PathVariable UUID fileId, @PathVariable UUID chunkId) {
//        return metadataService.deleteChunk(chunkId);
//    }
//
//    @GetMapping("/file/{fileId}/chunk/{chunkId}")
//    public ChunkMetadata getChunk(@PathVariable UUID fileId, @PathVariable UUID chunkId) {
//        return metadataService.getChunk(fileId, chunkId);
//    }
//
//    @GetMapping("/worker-nodes")
//    public List<WorkerNode> getWorkerNodes() {
//        return metadataService.getWorkerNodes();
//    }
}
