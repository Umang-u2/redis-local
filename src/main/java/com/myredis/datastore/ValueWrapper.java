package com.myredis.datastore;

public class ValueWrapper {

  private String value;
  private Long expiryTime;

  public ValueWrapper(String value, Long expiryTime) {
    this.value = value;
    this.expiryTime = expiryTime;
  }

  public String getValue() {
    return value;
  }

  public Long getExpiryTime() {
    return expiryTime;
  }

  public boolean isExpired(){
    if(expiryTime == null){
      return false; // no expiry set
    } else {
      return System.currentTimeMillis()>expiryTime;
    }
  }
}
