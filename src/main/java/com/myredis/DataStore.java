package com.myredis;

import java.util.HashMap;
import java.util.Map;

public class DataStore {

  private final Map<String, String> store = new HashMap<>();

  public void set(String key, String value) {
    store.put(key, value);
  }

  public String get(String key) {
    return store.get(key);
  }
}
