package com.myredis.datastore;

import java.util.HashMap;
import java.util.Map;

public class DataStore {

  private final Map<String, ValueWrapper> store = RDBPersistenceManager.load();

  public void set(String key, String value, Long expiry) {
    store.put(key, new ValueWrapper(value,expiry));
  }

  public ValueWrapper get(String key) {
    return store.get(key);
  }

  public void remove(String key){
    store.remove(key);
  }

  public void saveToRDB() {
    RDBPersistenceManager.save(store);
  }

}
