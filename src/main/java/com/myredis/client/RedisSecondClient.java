package com.myredis.client;

import com.myredis.protocol.RespParser;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class RedisSecondClient {
  public static void main(String[] args) {
    String host = "localhost";
    int port = 6379;

    try (Socket socket = new Socket(host, port);
         PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
         BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

      String client = "Client: 1st";
      System.out.println("Connected to Redis-like server by "+client);
      System.out.println("Type commands like: PING, ECHO hello, SET mykey value, GET mykey");
      System.out.println("Type 'exit' to quit.");

      InputStreamReader isr = new InputStreamReader(System.in);
      Scanner sc = new Scanner(isr);

      while(true){
        System.out.print(">");
        String input = sc.nextLine();
        if(input.equalsIgnoreCase("exit")){
          break;
        }

        String[] parts = input.trim().split("\\s+");
        String respCommand = RespParser.toRESP(parts);
        writer.println(respCommand);
        String response = RedisSecondClient.getResponse(socket);
        System.out.println("Response from server: " + response);
      }

    } catch (IOException e) {
      System.out.println("Client exception: " + e.getMessage());
    }
  }

  private static String getResponse(Socket socket) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

    String value = "";
    String line = reader.readLine();
    if (line.startsWith("+")) {
      // Simple String
      value = line.substring(1);
    } else if (line.startsWith("-")) {
      // Error
      value="Error: " + line.substring(1);
    } else if (line.startsWith("$")) {
      int length = Integer.parseInt(line.substring(1));
      if (length == -1) {
        value="(nil)";  // Key not found
      } else {
        char[] buffer = new char[length];
        reader.read(buffer, 0, length);  // Read exactly 'length' characters
        reader.readLine(); // Consume the trailing \r\n after the value
        value = new String(buffer);
      }
    } else {
      value = "Unknown response: " + line;
    }
    return value;
  }
}
