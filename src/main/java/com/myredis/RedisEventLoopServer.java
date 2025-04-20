package com.myredis;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class RedisEventLoopServer {

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
    ByteBuffer buffer = ByteBuffer.allocate(256);

    int read = client.read(buffer);
    if (read == -1) {
      client.close();
      System.out.println("Connection closed by client");
      return;
    }

    buffer.flip();
    String message = new String(buffer.array(), 0, buffer.limit()).trim();
    System.out.println("Received: " + message);


    String response;
    if ("PING".equalsIgnoreCase(message)) {
      response = "PONG\n";
    } else if("EXIT".equalsIgnoreCase(message)) {
      client.close();
      System.out.println("Connection closed by client");
      return;
    } else {
      response = "-UNKNOWN COMMAND\n";
    }

    ByteBuffer responseBuffer = ByteBuffer.wrap(response.getBytes());
    client.write(responseBuffer);
  }

  private static void handleAccept(ServerSocketChannel serverSocketChannel, Selector selector)
          throws IOException {
    SocketChannel client = serverSocketChannel.accept();
    client.configureBlocking(false);
    client.register(selector, SelectionKey.OP_READ);
    System.out.println("Accepted new connection from: " + client.getRemoteAddress());
  }
}
