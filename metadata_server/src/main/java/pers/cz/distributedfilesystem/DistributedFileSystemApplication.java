package pers.cz.distributedfilesystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class DistributedFileSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(DistributedFileSystemApplication.class, args);

        // Create an instance of MetadataService
        MetadataService metadataService = new MetadataService();

        // Schedule the health check task to run every 5 minutes
//        metadataService.scheduler.scheduleAtFixedRate(
//                metadataService::checkWorkerNodeHealth,
//                0, // initial delay
//                5, // period
//                TimeUnit.MINUTES
//        );
    }

}
