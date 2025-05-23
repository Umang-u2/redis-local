package com.myredis.server;

import com.myredis.datastore.RDBPersistenceManager;
import com.myredis.datastore.ValueWrapper;
import com.myredis.protocol.RespParser;
import com.myredis.datastore.DataStore;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RedisEventLoopServer {

  private final DataStore dataStore;
  private static final int PORT = 6379;
  // Create once during server setup
  private static final ExecutorService bgExecutor = Executors.newSingleThreadExecutor();

  public RedisEventLoopServer(){
    dataStore = new DataStore();
  }

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

      RedisEventLoopServer server = new RedisEventLoopServer();
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
            server.handleRead(key);
          }
        }
      }

    } catch (IOException ioe){
      ioe.printStackTrace();
    }
  }

  private void handleRead(SelectionKey key) throws IOException {
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

      String command = parts.get(0).toUpperCase();
      String response = createResponse(parts, command);
      System.out.println(response);
      ByteBuffer responseBuffer = ByteBuffer.wrap(response.getBytes());
    client.write(responseBuffer);
    } catch (IOException e){
      String error = "-malformed request\r\n";
      client.write(ByteBuffer.wrap(error.getBytes()));
    }
  }

  Long expiryTime = null;
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
          dataStore.set(parts.get(1), parts.get(2), null);
          return "+OK\r\n";
        } else if(parts.size() ==5 && "EX".equalsIgnoreCase(parts.get(3))) {
          int seconds = Integer.parseInt(parts.get(4));
          expiryTime = System.currentTimeMillis() + (seconds * 1000L);
          dataStore.set(parts.get(1),parts.get(2),expiryTime);
          return "+OK\r\n";
        } else if(parts.size() ==5 && "PX".equalsIgnoreCase(parts.get(3))) {
          long milliSeconds = Long.parseLong(parts.get(4));
          expiryTime = System.currentTimeMillis() + milliSeconds;
          dataStore.set(parts.get(1), parts.get(2), expiryTime);
          return "+OK\r\n";
        } else {
         return "-wrong number of arguments\r\n";
        }
      case "GET":
        if(parts.size() == 2) {
          ValueWrapper value = dataStore.get(parts.get(1));
          if (value == null) {
            return "--1\r\n";
          } else if(value.isExpired()){
            dataStore.remove(parts.get(1));
            return "--1\r\n";
          } else {
            return "$" + value.getValue().length() + "\r\n" + value.getValue() + "\r\n";
          }
        } else return "-wrong number of arguments\r\n";
      case "SAVE":
        if(parts.size()>1){
          return "-wrong number of arguments\r\n";
        } else {
          bgExecutor.submit(() -> {
            try {
              dataStore.saveToRDB();
              System.out.println("[RDB] Save complete.");
            } catch (Exception e) {
              System.err.println("[RDB] Save failed: " + e.getMessage());
            }
          });
          return "+OK\r\n";
        }
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
