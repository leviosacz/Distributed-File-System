package com.scu.ds.dfs.dfscoordinator.controller;

import com.scu.ds.dfs.dfscoordinator.exception.DocumentServiceException;
import com.scu.ds.dfs.dfscoordinator.exception.MetadataServiceException;
import com.scu.ds.dfs.dfscoordinator.model.ChunkMapping;
import com.scu.ds.dfs.dfscoordinator.model.FileMetadata;
import com.scu.ds.dfs.dfscoordinator.service.DocumentService;
import com.scu.ds.dfs.dfscoordinator.service.MetadataService;
import com.scu.ds.dfs.dfscoordinator.usecase.UploadUseCase;
import io.netty.handler.codec.http.HttpStatusClass;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Log
@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
public class DocumentController {
    private final DocumentService documentService;
    private final MetadataService metadataService;

    private final UploadUseCase uploadUseCase;

    private final Path root = Paths.get("/Users/akshatakadam/IdeaProjects/dfs-coordinator/uploads");

    @PostMapping("/upload")
    public ResponseEntity uploadDocument(@RequestParam("file") MultipartFile file) throws IOException {
        HttpStatus status  = HttpStatus.CREATED;
        String response = null;
        try {
            response = uploadUseCase.upload(file);
        } catch (MetadataServiceException e) {
            status = e.getStatus();
        } catch (DocumentServiceException e) {
            status = e.getStatus();
        }
        return ResponseEntity.status(status).body(response);
    }

    @ResponseBody
    @GetMapping("/download/{fileName:.+}")
    public ResponseEntity downloadDocument(@PathVariable String fileName) {

        ChunkMapping chunkMapping = metadataService.getChunkDetailsForDownload(fileName);
        byte[] data = documentService.downloadDocument(chunkMapping);
        //byte[] data = documentService.downloadDocument(fileName);
        ByteArrayResource fileResource = new ByteArrayResource(data);
        return ResponseEntity.ok().contentLength(data.length).header(HttpHeaders.CONTENT_TYPE, "application/octet-stream").header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + fileName + "\"").body(fileResource);
    }

    @ResponseBody
    @GetMapping("/delete/{fileName:.+}")
    public ResponseEntity deleteDocument(@PathVariable String fileName) {

        ChunkMapping chunkMapping = metadataService.getChunkDetailsForDownload(fileName);
        String status = documentService.deleteDocument(chunkMapping);

        return ResponseEntity.ok(status);
    }


    @PostMapping("/upload2")
    public ResponseEntity uploadDocument2(@RequestParam("file") MultipartFile file) throws IOException {
        Integer status = 0;
        log.info("In Document Controller");
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();

            Files.copy(file.getInputStream(), this.root.resolve(file.getOriginalFilename()));

        } catch (Exception e) {
            if (e instanceof FileAlreadyExistsException) {
                log.info("File  already exists");
                return ResponseEntity.badRequest().body("A file of that name already exists.");
            }
            throw new RuntimeException(e.getMessage());
        }

        return ResponseEntity.ok("File uploaded successfully");

    }

    @ResponseBody
    @GetMapping("/download2/{file:.+}")
    public ResponseEntity downloadDocument2(@PathVariable String file) {
        log.info("Downloading file " + file);
        //if(Files.isReadable(this.root.resolve(file.name()))){
        byte[] data = null;
        ByteArrayResource fileResource = null;
        try {
            data = Files.readAllBytes(Paths.get(root.toString() + "/" + file));
            fileResource = new ByteArrayResource(data);
            return ResponseEntity.ok().contentLength(data.length).header(HttpHeaders.CONTENT_TYPE, "application/octet-stream").header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + file + "\"").body(fileResource);
        } catch (MalformedURLException e) {
            log.info("Exception" + e.getMessage());
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
