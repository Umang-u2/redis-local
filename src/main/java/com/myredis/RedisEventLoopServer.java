package com.myredis;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

public class RedisEventLoopServer {

  private final DataStore dataStore = new DataStore();
  private static final int PORT = 6379;

  public static void main(String[] args) throws IOException {
    try(
            Selector selector = Selector.open();
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            ) {
      //Bind to port
      serverSocketChannel.bind(new InetSocketAddress(PORT));
      //Set to non-blockable
      serverSocketChannel.configureBlocking(false);
      //register with the selector for accept operations
      serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

      System.out.println("Server started on port " + PORT);

      while(true){
        selector.select(); //wait for events
        Set<SelectionKey> selectedKeys = selector.selectedKeys();

        Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

        while(keyIterator.hasNext()){
          SelectionKey key = keyIterator.next();
          keyIterator.remove(); // remove to avoid reprocessing

          if(key.isAcceptable()){
            handleAccept(serverSocketChannel, selector);
          } else if(key.isReadable()){
            handleRead(key);
          }
        }
      }

    } catch (IOException ioe){
      ioe.printStackTrace();
    }
  }

  private static void handleRead(SelectionKey key) throws IOException {
    SocketChannel client = (SocketChannel) key.channel();
    ByteBuffer buffer = ByteBuffer.allocate(512);

    int read = client.read(buffer);
    if (read == -1) {
      client.close();
      System.out.println("Connection closed by client");
      return;
    }

    try{
      List<String> parts = RespParser.parse(buffer);
      if(parts.isEmpty()) return;
      RedisEventLoopServer server = new RedisEventLoopServer();

      String command = parts.get(0).toUpperCase();
      String response = server.createResponse(parts, command);
      System.out.println(response);
      ByteBuffer responseBuffer = ByteBuffer.wrap(response.getBytes());
    client.write(responseBuffer);
    } catch (IOException e){
      String error = "-malformed request\r\n";
      client.write(ByteBuffer.wrap(error.getBytes()));
    }
  }

  private String createResponse(List<String> parts, String command) {
    String response="";
    switch (command) {
      case "PING":
        response = "+PONG\r\n";
        break;
      case "ECHO":
        response = parts.size() > 1 ? "+" + parts.get(1) + "\r\n" : "-ERR wrong number of arguments\r\n";
        break;
      case "SET":
        if(parts.size() == 3){
          dataStore.set(parts.get(1), parts.get(2));
          return "+OK\r\n";
        } else {
         return "-wrong number of arguments\r\n";
        }
      case "GET":
        if(parts.size() == 2) {
          String value = dataStore.get(parts.get(1));
          if (value == null) {
            return "--1\r\n";
          } else {
            return "$" + value.length() + "\r\n" + value + "\r\n";
          }
        } else return "-wrong number of arguments\r\n";
      default:
        response = "-unknown command '" + command + "'\r\n";
    }
    return response;
  }

  private static void handleAccept(ServerSocketChannel serverSocketChannel, Selector selector)
          throws IOException {
    SocketChannel client = serverSocketChannel.accept();
    client.configureBlocking(false);
    client.register(selector, SelectionKey.OP_READ);
    System.out.println("Accepted new connection from: " + client.getRemoteAddress());
  }
}
