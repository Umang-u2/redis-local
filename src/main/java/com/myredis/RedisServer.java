package com.myredis;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class RedisServer {

  private final Map<String, String> store = new HashMap<>();

  public static void main(String[] args) {
    int port = 6379;

    try (ServerSocket serverSocket = new ServerSocket(port)) {
      System.out.println("Redis-like server started on port " + port);

      serverSocket.setReuseAddress(true);
      while (true) {
        Socket clientSocket = serverSocket.accept();
        System.out.println("Client connected: " + clientSocket.getInetAddress());

        BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);

        String line;
        while ((line = reader.readLine()) != null) {
          System.out.println("Received: " + line);
          if ("PING".equalsIgnoreCase(line)) {
            writer.println("PONG");
          } else {
            writer.println("-UNKNOWN COMMAND");
          }
        }

        clientSocket.close();
        System.out.println("Client disconnected.");
      }
    } catch (IOException e) {
      System.out.println("Server exception: " + e.getMessage());
    }
  }
}
