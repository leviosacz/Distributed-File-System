# Distributed-File-System
## Here is how to run the Metadata Server: 

Option 1: (Locally) 

1. Find the JAR file located in the metadata_server directory and use it directly.
2. Start the server by using the command: java -jar MetadataServer.jar --server.port=8080.

Option 2: (On AWS)

1. Launch a AWS EC2 instance.
2. Copy the JAR file to the EC2 instance.
3. Install the JDK and Tmux on the EC2 instance. Use openjdk-17-jdk and openjdk-17-jre.
4. Start Tmux.
5. Start the metadata server using the command: sudo java -jar MetadataServer.jar.
6. Press Control + B, then release and press D. This will ensure that the process is not killed when the SSH connection is closed.

## Here is how to run the Document Server: 

Option 1: (Locally) 

1. Build the Project which exports a JAR file under PROJECT_ROOT/out/artifacts
2. Run by using command: 'java -jar DistributedSystemsProject.jar --server.port=8083'. This will start a Leader server.
3. Run multiple workers as desired by using command: java -jar DistributedSystemsProject.jar --server.port=8084/8085/8086...

Option 2: (On AWS)

1. Launch AWS EC2 instances (1 for leader & minimum 3 for workers)
2. Build the project and export the JAR file
3. Copy JAR file to EC2 instances
4. Install JDK and Tmux on the EC2 instances
5. Start tmux
6. Start leader server using 'sudo java -jar DistributedSystemsProject.jar'
7. Start worker server using 'sudo java -jar DistributedSystemsProject.jar --nodeId 'http://<ip_of_machine' '
8. Press Control + B, then release and press D
9. This will ensure that the process is not killed when we kill the SSH connection. 

## For Client Server
The original package name 'com.scu.ds.dfs.dfs-coordinator' is invalid and the client server uses 'com.scu.ds.dfs.dfscoordinator' instead.
