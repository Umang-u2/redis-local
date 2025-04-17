package com.myredis;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class RedisClient {
  public static void main(String[] args) {
    String host = "localhost";
    int port = 6379;

    try (Socket socket = new Socket(host, port);
         PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
         BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

      System.out.println("Connected to Redis-like server.");

      while(true){
        InputStreamReader isr = new InputStreamReader(System.in);
        Scanner sc = new Scanner(isr);
        String input = sc.nextLine();
        if(input.equalsIgnoreCase("Exit")){
          break;
        }
        writer.println(input);
        String response = reader.readLine();
        System.out.println("Response from server: " + response);
      }

    } catch (IOException e) {
      System.out.println("Client exception: " + e.getMessage());
    }
  }
}
