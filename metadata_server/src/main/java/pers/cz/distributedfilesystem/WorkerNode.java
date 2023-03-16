package pers.cz.distributedfilesystem;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.UUID;

public class WorkerNode {
    private String nodeId;
    private long availableSpace;

    public WorkerNode(String nodeId, long availableSpace) {
        this.nodeId = nodeId;
        this.availableSpace = availableSpace;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public long getAvailableSpace() {
        return availableSpace;
    }

    public void setAvailableSpace(long availableSpace) {
        this.availableSpace = availableSpace;
    }

    public boolean isHealthy() {
        try {
            // Create a URL object from the node ID
            URL url = new URL(nodeId + "/health");

            // Open a connection to the URL
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set the HTTP request method to GET
            connection.setRequestMethod("GET");

            // Set a timeout for the HTTP request
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            // Get the response code from the HTTP request
            int responseCode = connection.getResponseCode();

            // Check if the response code indicates success
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Get the response body from the HTTP request
                Scanner scanner = new Scanner(connection.getInputStream());
                String responseBody = scanner.useDelimiter("\\A").next();
                scanner.close();

                // Check if the response body contains a "healthStatus" field with the value "true"
                return responseBody.contains("\"healthStatus\": \"true\"");
            } else {
                // If the response code indicates failure, return false
                return false;
            }

        } catch (Exception e) {
            // If an exception occurs, return false
            return false;
        }
    }

    public boolean replicateChunk(UUID chunkId, String destinationNode, long chunkSize) {
        try {
            // Create a URL object from the destination node ID
            URL url = new URL(destinationNode + "/chunk/" + chunkId);

            // Open a connection to the URL
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set the HTTP request method to PUT
            connection.setRequestMethod("PUT");

            // Set a timeout for the HTTP request
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            // Get the response code from the HTTP request
            int responseCode = connection.getResponseCode();

            // Check if the response code indicates success
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // If successful, update the available space on the target node
                availableSpace -= chunkSize;
                return true;
            } else {
                // If the response code indicates failure, return false
                return false;
            }
        } catch (Exception e) {
            // If an exception occurs, return false
            return false;
        }
    }
}
