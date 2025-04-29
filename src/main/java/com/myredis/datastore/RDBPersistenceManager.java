package com.myredis.datastore;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RDBPersistenceManager {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  public static void save(Map<String, ValueWrapper> store){
    try{
      String filePath = ConfigurationManager.getRdbFilePath();
      System.out.println("[RDB] Saving store with keys: " + store.keySet());
      objectMapper.writeValue(new File(filePath),store);
    } catch (StreamWriteException e) {
      e.printStackTrace();
    } catch (DatabindException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static Map<String, ValueWrapper> load(){
    String filePath = ConfigurationManager.getRdbFilePath();
    File file = new File(filePath);
    if(!file.exists()){
      System.out.println("[RDB] No snapshot found. Starting with empty store.");
      return new HashMap<>();
    }

    try {
      Map<String,ValueWrapper> store = objectMapper.readValue(file,
              new TypeReference<Map<String, ValueWrapper>>() {}
      );
      return store;
    } catch (StreamReadException e) {
      System.err.println("[RDB] Failed to load snapshot: " + e.getMessage());
      return new HashMap<>();
    } catch (DatabindException e) {
      System.err.println("[RDB] Failed to load snapshot: " + e.getMessage());
      return new HashMap<>();
    } catch (IOException e) {
      System.err.println("[RDB] Failed to load snapshot: " + e.getMessage());
      return new HashMap<>();
    }
  }
}
