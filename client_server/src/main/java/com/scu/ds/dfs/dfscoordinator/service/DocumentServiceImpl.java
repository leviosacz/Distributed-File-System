package com.scu.ds.dfs.dfscoordinator.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.scu.ds.dfs.dfscoordinator.exception.DocumentServiceException;
import com.scu.ds.dfs.dfscoordinator.model.ChunkMapping;
import com.scu.ds.dfs.dfscoordinator.model.FileMetadata;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

import java.io.IOException;
import java.time.Duration;

@Log
@Component
public class DocumentServiceImpl implements DocumentService{

    private WebClient webClient;

    private ObjectMapper objectMapper;
    public DocumentServiceImpl(@Value("${document.base.url}") String documentBaseUrl, @Autowired ObjectMapper objectMapper){
        webClient = WebClient
                .builder()
                .baseUrl(documentBaseUrl)
                .exchangeStrategies(ExchangeStrategies.builder()
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(16 * 1024 * 1024))
                        .build())
                .build();
        this.objectMapper = objectMapper;
    }

    @Override
    public String uploadDocument(MultipartFile file, FileMetadata fileMetadata) {

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        log.info("In DocumentServiceImpl");
        try {
            MultipartBodyBuilder builder = new MultipartBodyBuilder();
            builder.part("file", new ByteArrayResource(file.getBytes())).filename(file.getName());
            builder.part("documentId", fileMetadata.getFileId());
            builder.part("chunkToWorkerMap", ow.writeValueAsString(fileMetadata.getChunkWorkerNodeMap()));
            builder.part("chunkSize",fileMetadata.getChunkSize());
            log.info("Calling Document service to upload file " + file.getName());

            return webClient.post()
                    .uri("/upload")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA.toString())
                    .bodyValue(builder.build())
                    .exchangeToMono(this::handleDocumentServiceDefaultResponse)
                    .retryWhen(buildRetrySpec())
                    .block();

        } catch (IOException e) {
            log.info("Error " + e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] downloadDocument(ChunkMapping chunkMapping) {
        log.info("Calling Document service to download file " );

        MultipartBodyBuilder builder = new MultipartBodyBuilder();

        builder.part("documentId", chunkMapping.getFileId());
        builder.part("chunkToWorkerMap",chunkMapping.getChunkWorkerNodeMap());

        log.info("Calling Document service to download file ");

        return webClient.post()
                        .uri( "/downloadDocument")
                        .bodyValue(builder.build())
                        .exchangeToMono(this::handleDownloadFileResponse)
                        .retryWhen(buildRetrySpec())
                        .map(ByteArrayResource::getByteArray)
                        .block();
    }

    @Override
    public String deleteDocument(ChunkMapping chunkMapping) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();

        builder.part("documentId", chunkMapping.getFileId());
        builder.part("chunkToWorkerMap",chunkMapping.getChunkWorkerNodeMap());

        log.info("Calling Document service to download file ");

        return webClient.post()
                .uri( "/downloadDocument")
                .bodyValue(builder.build())
                .exchangeToMono(this::handleDocumentServiceDefaultResponse)
                .retryWhen(buildRetrySpec())
                .block();
    }

    private Mono<String> handleDocumentServiceDefaultResponse(ClientResponse clientResponse) {

        return clientResponse.bodyToMono(String.class)
                .map(response -> {
                    if (clientResponse.statusCode().is4xxClientError()) {
                        throw new DocumentServiceException("Client error occurred while calling Document service", clientResponse.statusCode());
                    } else if (clientResponse.statusCode().is5xxServerError()) {
                        throw new DocumentServiceException("Server error occurred while calling Document service", clientResponse.statusCode());
                    }
                    return response;
                })
                .switchIfEmpty(Mono.error(new DocumentServiceException("Unknown error occurred while calling Document service", clientResponse.statusCode())));
    }

    private Mono<ByteArrayResource> handleDownloadFileResponse(ClientResponse clientResponse) {

        return clientResponse.bodyToMono(ByteArrayResource.class)
                .map(response -> {
                    if (clientResponse.statusCode().is4xxClientError()) {
                        throw new DocumentServiceException("Client error occurred while calling Document service", clientResponse.statusCode());
                    } else if (clientResponse.statusCode().is5xxServerError()) {
                        throw new DocumentServiceException("Server error occurred while calling Document service", clientResponse.statusCode());
                    }
                    return response;
                })
                .switchIfEmpty(Mono.error(new DocumentServiceException("Unknown error occurred while calling Document service", clientResponse.statusCode())));
    }

    private RetryBackoffSpec buildRetrySpec() {
        return Retry.fixedDelay(3, Duration.ofMillis(500))
                .filter(e -> {
                    if(e instanceof DocumentServiceException) {
                        return ((DocumentServiceException)e).getStatus().is5xxServerError();
                    }
                    return true;
                })
                .onRetryExhaustedThrow(((retryBackoffSpec, retrySignal) -> retrySignal.failure()))
                .doBeforeRetry(retrySignal -> log.info("Executing retry attempt number:"+(int)(retrySignal.totalRetries() + 1)));
    }
}
