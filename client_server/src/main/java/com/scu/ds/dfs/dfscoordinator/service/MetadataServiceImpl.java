package com.scu.ds.dfs.dfscoordinator.service;

import com.scu.ds.dfs.dfscoordinator.exception.MetadataServiceException;
import com.scu.ds.dfs.dfscoordinator.model.ChunkMapping;
import com.scu.ds.dfs.dfscoordinator.model.FileMetadata;
import lombok.extern.java.Log;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;
import reactor.util.retry.RetrySpec;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;

@Log
@Component
public class MetadataServiceImpl implements MetadataService {

    WebClient webClient;

    public MetadataServiceImpl(@Value("${metadata.base.url}") String metadataBaseUrl) {
        webClient = WebClient
                .builder()
                .baseUrl(metadataBaseUrl)
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer
                                .defaultCodecs()
                                .maxInMemorySize(16 * 1024 * 1024))
                        .build())
                .build();
    }

    @Override
    public FileMetadata getFileMetadata(MultipartFile file) {

        MultiValueMap<String, String> bodyValues = new LinkedMultiValueMap<>();
        bodyValues.add("fileName", file.getName());
        bodyValues.add("fileSize", Long.toString(file.getSize()));

        return webClient.post()
                .uri("/metadata/upload")
                .body(BodyInserters.fromFormData(bodyValues))
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToMono(this::handleGetFileMetadataResponse)
                .retryWhen(buildRetrySpec())
                .block();
    }

    @Override
    public ChunkMapping getChunkDetailsForDownload(String fileName) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/metadata/{filename}").build(fileName))
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToMono(this::handleGetFileMetadataResponse)
                .retryWhen(buildRetrySpec())
                .block();


    }

    @Override
    public ChunkMapping getChunkDetailsForDelete(String fileName) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/metadata/delete/{filename}").build(fileName))
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToMono(this::handleGetFileMetadataResponse)
                .retryWhen(buildRetrySpec())
                .block();
    }

    private Mono<FileMetadata> handleGetFileMetadataResponse(ClientResponse clientResponse) {
        String s = "{\n" +
                "    \"fileId\": \"e3c6b004-c577-41d4-b072-125086373dc6\",\n" +
                "    \"chunkSize\": 10485760,\n" +
                "    \"chunkWorkerNodeMap\": {\n" +
                "        \"e6dc4c9c-7c0f-4688-9545-722455619946\": [\n" +
                "            \"http://18.188.139.215\",\n" +
                "            \"http://18.119.127.166\",\n" +
                "            \"http://3.15.219.220\"\n" +
                "        ],\n" +
                "        \"9d3a0095-9668-47d6-a93f-8fa67cffbff5\": [\n" +
                "            \"http://18.188.139.215\",\n" +
                "            \"http://18.119.127.166\",\n" +
                "            \"http://3.15.219.220\"\n" +
                "        ],\n" +
                "        \"d123abf6-ee7e-421d-8401-a69083e301c0\": [\n" +
                "            \"http://18.188.139.215\",\n" +
                "            \"http://18.119.127.166\",\n" +
                "            \"http://3.15.219.220\"\n" +
                "        ],\n" +
                "        \"e8f51dda-d229-4c9d-9cc1-b6c9e8d87c0c\": [\n" +
                "            \"http://18.188.139.215\",\n" +
                "            \"http://18.119.127.166\",\n" +
                "            \"http://3.15.219.220\"\n" +
                "        ],\n" +
                "        \"7498a0c9-f040-4fa1-892b-e87786de1935\": [\n" +
                "            \"http://18.188.139.215\",\n" +
                "            \"http://18.119.127.166\",\n" +
                "            \"http://3.15.219.220\"\n" +
                "        ],\n" +
                "        \"595bc73a-8ab1-48da-bf76-eeaa8e40025e\": [\n" +
                "            \"http://18.188.139.215\",\n" +
                "            \"http://18.119.127.166\",\n" +
                "            \"http://3.15.219.220\"\n" +
                "        ],\n" +
                "        \"a1b134b0-5f0c-40cd-9f8a-204a1158d0d2\": [\n" +
                "            \"http://18.188.139.215\",\n" +
                "            \"http://18.119.127.166\",\n" +
                "            \"http://3.15.219.220\"\n" +
                "        ],\n" +
                "        \"7af779c7-656a-43e4-8ebf-940db9167116\": [\n" +
                "            \"http://18.188.139.215\",\n" +
                "            \"http://18.119.127.166\",\n" +
                "            \"http://3.15.219.220\"\n" +
                "        ],\n" +
                "        \"20e36e81-b1c0-4ed4-87bc-03b73b68c747\": [],\n" +
                "        \"c584f68b-21b5-4434-b971-434da66da9cd\": [],\n" +
                "        \"e23e2411-54c1-4af2-a5ff-cda7e61fd989\": [\n" +
                "            \"http://18.188.139.215\",\n" +
                "            \"http://18.119.127.166\",\n" +
                "            \"http://3.15.219.220\"\n" +
                "        ],\n" +
                "        \"c931257c-9ff9-4231-9be8-fb22e8cd87d6\": [\n" +
                "            \"http://18.188.139.215\",\n" +
                "            \"http://18.119.127.166\",\n" +
                "            \"http://3.15.219.220\"\n" +
                "        ],\n" +
                "        \"b4dd8ae1-7b9a-43ba-b050-ffed76548323\": [\n" +
                "            \"http://18.188.139.215\",\n" +
                "            \"http://18.119.127.166\",\n" +
                "            \"http://3.15.219.220\"\n" +
                "        ],\n" +
                "        \"24fd20c6-a635-44f9-ba35-8f4735933080\": [\n" +
                "            \"http://18.188.139.215\",\n" +
                "            \"http://18.119.127.166\",\n" +
                "            \"http://3.15.219.220\"\n" +
                "        ],\n" +
                "        \"e9a52b3a-b6ac-45c1-a29c-f1718c10c0d7\": [\n" +
                "            \"http://18.188.139.215\",\n" +
                "            \"http://18.119.127.166\",\n" +
                "            \"http://3.15.219.220\"\n" +
                "        ],\n" +
                "        \"41e03b64-429b-4a85-a957-3872d1104f75\": [\n" +
                "            \"http://18.188.139.215\",\n" +
                "            \"http://18.119.127.166\",\n" +
                "            \"http://3.15.219.220\"\n" +
                "        ],\n" +
                "        \"9be06de0-768e-4508-acab-823c6b0c6a06\": [\n" +
                "            \"http://18.188.139.215\",\n" +
                "            \"http://18.119.127.166\",\n" +
                "            \"http://3.15.219.220\"\n" +
                "        ],\n" +
                "        \"ffbfc0cf-c521-469d-a895-4bf46f20c4d1\": [\n" +
                "            \"http://18.188.139.215\",\n" +
                "            \"http://18.119.127.166\",\n" +
                "            \"http://3.15.219.220\"\n" +
                "        ],\n" +
                "        \"4a6e9686-21e5-4a4b-acbd-e4030ff10895\": [\n" +
                "            \"http://18.188.139.215\",\n" +
                "            \"http://18.119.127.166\",\n" +
                "            \"http://3.15.219.220\"\n" +
                "        ],\n" +
                "        \"ba7402e0-5851-4041-8522-aae462a6577b\": [\n" +
                "            \"http://18.188.139.215\",\n" +
                "            \"http://18.119.127.166\",\n" +
                "            \"http://3.15.219.220\"\n" +
                "        ],\n" +
                "        \"fb5a25bb-e598-4f91-9839-dc6ef45dee80\": [\n" +
                "            \"http://18.188.139.215\",\n" +
                "            \"http://18.119.127.166\",\n" +
                "            \"http://3.15.219.220\"\n" +
                "        ],\n" +
                "        \"04526898-3432-4668-bf1f-c26def72d233\": [\n" +
                "            \"http://18.188.139.215\",\n" +
                "            \"http://18.119.127.166\",\n" +
                "            \"http://3.15.219.220\"\n" +
                "        ],\n" +
                "        \"4220e9df-82fe-4f96-8c02-415b2d577a3c\": [\n" +
                "            \"http://18.188.139.215\",\n" +
                "            \"http://18.119.127.166\",\n" +
                "            \"http://3.15.219.220\"\n" +
                "        ],\n" +
                "        \"1498b5f0-9a8f-43fa-9b02-e896b0d2bcbd\": [\n" +
                "            \"http://18.188.139.215\",\n" +
                "            \"http://18.119.127.166\",\n" +
                "            \"http://3.15.219.220\"\n" +
                "        ],\n" +
                "        \"9c521dcc-6d0d-4a30-9cb1-0947098d90ff\": [\n" +
                "            \"http://18.188.139.215\",\n" +
                "            \"http://18.119.127.166\",\n" +
                "            \"http://3.15.219.220\"\n" +
                "        ],\n" +
                "        \"cdbb3b00-503d-4a7d-a4ae-a4f0ae3e7933\": [\n" +
                "            \"http://18.188.139.215\",\n" +
                "            \"http://18.119.127.166\",\n" +
                "            \"http://3.15.219.220\"\n" +
                "        ],\n" +
                "        \"3ab3412c-9c7b-442f-8253-b8b866b2e8be\": [\n" +
                "            \"http://18.188.139.215\",\n" +
                "            \"http://18.119.127.166\",\n" +
                "            \"http://3.15.219.220\"\n" +
                "        ],\n" +
                "        \"51143a55-053b-44cc-b1f4-af1d023ec30f\": [\n" +
                "            \"http://18.188.139.215\",\n" +
                "            \"http://18.119.127.166\",\n" +
                "            \"http://3.15.219.220\"\n" +
                "        ],\n" +
                "        \"4f91a6b0-1546-48d5-ad8a-71e8b274028b\": [\n" +
                "            \"http://18.188.139.215\",\n" +
                "            \"http://18.119.127.166\",\n" +
                "            \"http://3.15.219.220\"\n" +
                "        ],\n" +
                "        \"9a555f76-dc0a-422c-b709-f8d66de8b202\": [\n" +
                "            \"http://18.188.139.215\",\n" +
                "            \"http://18.119.127.166\",\n" +
                "            \"http://3.15.219.220\"\n" +
                "        ],\n" +
                "        \"73a6040d-66ce-4b4c-84ec-188a801108ed\": [\n" +
                "            \"http://18.188.139.215\",\n" +
                "            \"http://18.119.127.166\",\n" +
                "            \"http://3.15.219.220\"\n" +
                "        ],\n" +
                "        \"0a2f4dd1-39bd-4b19-9878-70a47e38fecd\": [\n" +
                "            \"http://18.188.139.215\",\n" +
                "            \"http://18.119.127.166\",\n" +
                "            \"http://3.15.219.220\"\n" +
                "        ],\n" +
                "        \"db0304e3-6ec8-4cb3-81a8-d5d1651fd006\": [\n" +
                "            \"http://18.188.139.215\",\n" +
                "            \"http://18.119.127.166\",\n" +
                "            \"http://3.15.219.220\"\n" +
                "        ],\n" +
                "        \"9f866737-0e1b-41d7-8b57-2100b8dcd10d\": [\n" +
                "            \"http://18.188.139.215\",\n" +
                "            \"http://18.119.127.166\",\n" +
                "            \"http://3.15.219.220\"\n" +
                "        ],\n" +
                "        \"63b38c99-c34d-4304-aea2-419d07da08ba\": [\n" +
                "            \"http://18.188.139.215\",\n" +
                "            \"http://18.119.127.166\",\n" +
                "            \"http://3.15.219.220\"\n" +
                "        ],\n" +
                "        \"65be8796-70ad-474f-bc84-08adf560d0ff\": [\n" +
                "            \"http://18.188.139.215\",\n" +
                "            \"http://18.119.127.166\",\n" +
                "            \"http://3.15.219.220\"\n" +
                "        ],\n" +
                "        \"544d848f-648a-4dca-93c4-deb515d2b81c\": [\n" +
                "            \"http://18.188.139.215\",\n" +
                "            \"http://18.119.127.166\",\n" +
                "            \"http://3.15.219.220\"\n" +
                "        ],\n" +
                "        \"cade67ad-ecec-4e40-ac4b-16c9d4e1fcc3\": [\n" +
                "            \"http://18.188.139.215\",\n" +
                "            \"http://18.119.127.166\",\n" +
                "            \"http://3.15.219.220\"\n" +
                "        ]\n" +
                "    }\n" +
                "}";
        return clientResponse.bodyToMono(FileMetadata.class)
                .map(response -> {
                    if (clientResponse.statusCode().is4xxClientError()) {
                        throw new MetadataServiceException("Client error occurred while calling Metadata service", clientResponse.statusCode());
                    } else if (clientResponse.statusCode().is5xxServerError()) {
                        throw new MetadataServiceException("Server error occurred while calling Metadata service", clientResponse.statusCode());
                    }
                    return response;
                })
                .switchIfEmpty(Mono.error(new MetadataServiceException("Unknown error occurred while calling Metadata service", clientResponse.statusCode())));
    }


    private RetryBackoffSpec buildRetrySpec() {
        return Retry.fixedDelay(3, Duration.ofMillis(500))
                .filter(e -> {
                    if(e instanceof MetadataServiceException) {
                        return ((MetadataServiceException)e).getStatus().is5xxServerError();
                    }
                    return true;
                })
                .onRetryExhaustedThrow(((retryBackoffSpec, retrySignal) -> retrySignal.failure()))
                .doBeforeRetry(retrySignal -> log.info("Executing retry attempt number:"+(int)(retrySignal.totalRetries() + 1)));
    }
}
