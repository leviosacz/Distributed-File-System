package pers.cz.distributedfilesystem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.UUID;

import java.util.*;
import java.util.concurrent.*;

@Service
public class MetadataService {

  private final Map<UUID, FileMetadata> fileMetadataMap;
  private final Map<UUID, ChunkMetadata> chunkMetadataMap;
  private final Map<String, List<UUID>> fileChunkMap;
  private final Map<UUID, List<String>> chunkWorkerNodeIdMap;
  private final Map<String, WorkerNode> workerNodeMap;

  private final ExecutorService executor;
  public final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

  @Autowired
  public MetadataService() {
    this.fileMetadataMap = new HashMap<UUID, FileMetadata>();
    this.chunkMetadataMap = new HashMap<UUID, ChunkMetadata>();
    this.fileChunkMap = new HashMap<>();
    this.chunkWorkerNodeIdMap = new HashMap<UUID, List<String>>();
    this.workerNodeMap = new HashMap<>();
    this.executor = Executors.newFixedThreadPool(10); // create a thread pool with 10 threads
    scheduler.scheduleAtFixedRate(this::checkWorkerNodeHealth, 0, 5, TimeUnit.MINUTES);
  }

  public synchronized FileMetadataWithMap addFile(String fileName, long fileSize) {
    UUID fileId = generateFileId();
    FileMetadata fileMetadata = new FileMetadata(fileId, fileName, fileSize);
    fileMetadataMap.put(fileId, fileMetadata);

    // Calculate chunk size and number of chunks
    int chunkSize = calculateChunkSize(fileSize);
    int numChunks = (int) Math.ceil((double) fileSize / chunkSize);

    // Add file chunks
    List<UUID> chunkIds = new ArrayList<>();
    for (int i = 0; i < numChunks; i++) {
      ChunkMetadata chunkMetadata = addChunk(fileId, chunkSize);
      UUID chunkId = chunkMetadata.getChunkId();
      chunkIds.add(chunkId);
      List<String> workerNodeIds = chunkMetadata.getWorkerNodeIds();
      chunkWorkerNodeIdMap.put(chunkId, workerNodeIds);
    }
    fileChunkMap.put(fileName, chunkIds);

    fileMetadata.setChunkSize(chunkSize);
    fileMetadata.setChunkWorkerNodeIdMap(chunkWorkerNodeIdMap);

    FileMetadataWithMap result = new FileMetadataWithMap(
            fileId,
            chunkSize,
            chunkWorkerNodeIdMap
    );
    System.out.println("Added file: " + fileName);
    return result;
  }

  public synchronized FileIdWithMap deleteFile(String fileName) {
    Map<UUID, List<String>> originalChunkWorkerNodeIdMap = new HashMap<>();
    System.out.println("Searching file: " + fileName);
    UUID fileId = getFileIdByFileName(fileName);
    if (fileChunkMap.containsKey(fileName)) {
      System.out.println("Found file: " + fileName);
      List<UUID> chunkIds = fileChunkMap.get(fileName);
      for (UUID chunkId : chunkIds) {
        List<String> workerNodeIds = chunkMetadataMap.get(chunkId).getWorkerNodeIds();
        originalChunkWorkerNodeIdMap.put(chunkId, workerNodeIds);
        executor.execute(() -> deleteChunk(chunkId)); // execute deleteChunk in a separate thread
      }
      fileChunkMap.remove(fileName);
      System.out.println("Deleted file info: " + fileName);
    }
    else {
      System.out.println("Couldn't find file: " + fileName);
    }
    FileIdWithMap result = new FileIdWithMap(fileId, originalChunkWorkerNodeIdMap);
    System.out.println("Got file info: " + fileName);
    return result;
  }

  public synchronized FileIdWithMap getFile(String fileName) {
    System.out.println("Searching file: " + fileName);
    // System.out.println("fileChunkMap: " + fileChunkMap);
    if (fileChunkMap.containsKey(fileName)) {
      System.out.println("Found file: " + fileName);
      List<UUID> chunkIds = fileChunkMap.get(fileName);
      Map<UUID, List<String>> resultmap = new HashMap<>();
      for (UUID chunkId : chunkIds) {
        resultmap.put(chunkId, chunkWorkerNodeIdMap.get(chunkId));
      }
      UUID fileId = getFileIdByFileName(fileName);
      FileIdWithMap result = new FileIdWithMap(fileId, resultmap);
      System.out.println("Got file info: " + fileName);
      return result;
    }
    System.out.println("Couldn't find file: " + fileName);
    return null;
  }

  public synchronized ChunkMetadata addChunk(UUID fileId, int chunkSize) {
    UUID chunkId = generateChunkId();
    ChunkMetadata chunkMetadata = new ChunkMetadata(chunkId, chunkSize, fileId, null);
    chunkMetadataMap.put(chunkId, chunkMetadata);

    // Execute selectWorkerNodes() method in a separate thread
    executor.submit(() -> {
      List<WorkerNode> workerNodes = selectWorkerNodes(chunkId, chunkSize);
      List<String> workerNodeIds = new ArrayList<>();
      for (WorkerNode workerNode : workerNodes) {
        workerNodeIds.add(workerNode.getNodeId());
      }
      chunkMetadata.setWorkerNodeIds(workerNodeIds);
      updateChunkWorkerNodeIdMap(chunkId, workerNodeIds);
    });

    return chunkMetadata;
  }

  public synchronized boolean deleteChunk(UUID chunkId) {
    if (chunkMetadataMap.containsKey(chunkId)) {
      ChunkMetadata chunkMetadata = chunkMetadataMap.get(chunkId);
      UUID fileId = chunkMetadata.getFileId();
      String fileName = fileMetadataMap.get(fileId).getFileName();
      List<UUID> chunkIds = fileChunkMap.get(fileName);
      if (chunkIds != null) {
        chunkIds.remove(chunkId);
      }
      chunkMetadataMap.remove(chunkId);
      chunkWorkerNodeIdMap.remove(chunkId);
      return true;
    }
    return false;
  }

  public synchronized ChunkMetadata getChunk(UUID fileId, UUID chunkId) {
    if (chunkMetadataMap.containsKey(chunkId)) {
      ChunkMetadata chunkMetadata = chunkMetadataMap.get(chunkId);
      return chunkMetadata;
    }
    return null;
  }

  public synchronized void addWorkerNode(String nodeId, long availableSpace) {
    if (!workerNodeMap.containsKey(nodeId)) {
      WorkerNode workerNode = new WorkerNode(nodeId, availableSpace);
      workerNodeMap.put(nodeId, workerNode);
      System.out.println("Added worker node: " + nodeId);
    } else {
      System.out.println("Worker node already exists: " + nodeId);
    }
  }

  public List<WorkerNode> getWorkerNodes() {
    return new ArrayList<>(workerNodeMap.values());
  }

  private UUID generateFileId() {
    return UUID.randomUUID();
  }

  private UUID generateChunkId() {
    return UUID.randomUUID();
  }

  private List<WorkerNode> selectWorkerNodes(UUID chunkId, int chunkSize) {
    List<WorkerNode> workerNodes = getWorkerNodes();
    List<WorkerNode> availableWorkerNodes = new ArrayList<>();
    for (WorkerNode workerNode : workerNodes) {
      if (workerNode.getAvailableSpace() >= chunkMetadataMap.get(chunkId).getChunkSize()) {
        availableWorkerNodes.add(workerNode);
      }
    }
    Comparator<WorkerNode> byFreeSpaceDescending = Comparator.comparingLong(WorkerNode::getAvailableSpace).reversed();
    availableWorkerNodes.sort(byFreeSpaceDescending);
    List<WorkerNode> selectedWorkerNodes = availableWorkerNodes.subList(0, Math.min(3, availableWorkerNodes.size()));
    for (WorkerNode workerNode : selectedWorkerNodes) {
      long newAvailableSpace = workerNode.getAvailableSpace() - chunkSize;
      workerNode.setAvailableSpace(newAvailableSpace);
      workerNodeMap.put(workerNode.getNodeId(), workerNode);
    }
    return selectedWorkerNodes;
  }

  // Helper method to update the chunkWorkerNodeIdMap in a synchronized manner
  private synchronized void updateChunkWorkerNodeIdMap(UUID chunkId, List<String> workerNodeIds) {
    chunkWorkerNodeIdMap.put(chunkId, workerNodeIds);
  }

  private int calculateChunkSize(long fileSize) {
    int maxChunkSize = 10 * 1024 * 1024; // Maximum chunk size is 10 MB
    int minChunkSize = 64 * 1024; // Minimum chunk size is 64 KB

    int chunkSize = maxChunkSize;
    int numChunks = (int) (fileSize / maxChunkSize) + 1;

    // Calculate the chunk size such that each chunk is between 64KB and 100MB
    while (chunkSize > minChunkSize && fileSize / chunkSize > numChunks) {
      chunkSize = chunkSize / 2;
    }

    return chunkSize;
  }

  public void checkWorkerNodeHealth() {
    for (String nodeId : workerNodeMap.keySet()) {
      WorkerNode workerNode = workerNodeMap.get(nodeId);
      boolean isHealthy = workerNode.isHealthy();
      if (!isHealthy) {
        System.out.println("Worker node " + nodeId + " is not healthy");
        List<UUID> chunkIds = new ArrayList<>(chunkWorkerNodeIdMap.keySet());
        for (UUID chunkId : chunkIds) {
          List<String> workerNodeIds = chunkWorkerNodeIdMap.get(chunkId);
          workerNodeIds.remove(nodeId);
          chunkWorkerNodeIdMap.put(chunkId, workerNodeIds);
        }

//        // These are with chunk replication in the case of worker node failure
//        for (UUID chunkId : chunkIds) {
//          List<String> workerNodeIds = chunkWorkerNodeIdMap.get(chunkId);
//          if (workerNodeIds.contains(nodeId)) {
//            // Replicate the chunk to another worker node with the largest available space
//            WorkerNode node2 = getWorkerNodeWithLargestAvailableSpace();
//            if (node2 == null) {
//              System.out.println("No worker node available for replication");
//              continue;
//            }
//            long chunkSize = chunkMetadataMap.get(chunkId).getChunkSize();
//            boolean isReplicated = workerNode.replicateChunk(chunkId, node2.getNodeId(), chunkSize);
//            if (isReplicated) {
//              // Update the mapping in MetadataService
//              workerNodeIds.remove(nodeId);
//              workerNodeIds.add(node2.getNodeId());
//              chunkWorkerNodeIdMap.put(chunkId, workerNodeIds);
//            }
//          }
//        }
      }
    }
  }

  private WorkerNode getWorkerNodeWithLargestAvailableSpace() {
    WorkerNode result = null;
    long largestAvailableSpace = 0;
    for (WorkerNode workerNode : workerNodeMap.values()) {
      long availableSpace = workerNode.getAvailableSpace();
      if (availableSpace > largestAvailableSpace) {
        largestAvailableSpace = availableSpace;
        result = workerNode;
      }
    }
    return result;
  }

  private UUID getFileIdByFileName(String fileName) {
    List<UUID> chunkIds = fileChunkMap.get(fileName);
    if (chunkIds != null && !chunkIds.isEmpty()) {
      UUID chunkId = chunkIds.get(0);  // assuming the first chunk contains the file ID
      ChunkMetadata chunkMetadata = chunkMetadataMap.get(chunkId);
      if (chunkMetadata != null) {
        // System.out.println("Got chunkId: " + chunkId);
        return chunkMetadata.getFileId();
      }
    }
    return null;  // return null if fileName not found or no chunk found
  }

}
