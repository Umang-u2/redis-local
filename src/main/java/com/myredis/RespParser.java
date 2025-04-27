package com.myredis;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class RespParser {

  public static List<String> parse(ByteBuffer buffer) throws IOException{
    buffer.flip();
    byte[] bytes = new byte[buffer.limit()];
    buffer.get(bytes);
    ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
    BufferedReader reader = new BufferedReader(new InputStreamReader(bais));

    List<String> parts = new ArrayList<>();

    String line = reader.readLine();
    if (line == null || !line.startsWith("*")) {
      throw new IOException("Invalid RESP input. Expected array.");
    }
    if (line == null || !line.startsWith("*")) {
      throw new IOException("Invalid RESP input. Expected array.");
    }
    int numElements = Integer.parseInt(line.substring(1));

    for(int i=0;i< numElements;i++){
      String lenLine = reader.readLine();
      if (lenLine == null || !lenLine.startsWith("$")) {
        throw new IOException("Invalid RESP input. Expected bulk string.");
      }

      int length = Integer.parseInt(lenLine.substring(1));
      char[] data = new char[length];
      int read = reader.read(data, 0, length);
      if (read != length) {
        throw new IOException("Invalid RESP input. Unexpected string length.");
      }

      reader.readLine();
      parts.add(new String(data));
    }

    return parts;
  }

  public static String toRESP(String[] parts) {
    StringBuilder sb = new StringBuilder();
    sb.append("*").append(parts.length).append("\r\n");
    for (String part : parts) {
      sb.append("$").append(part.length()).append("\r\n");
      sb.append(part).append("\r\n");
    }
    return sb.toString();
  }
}
