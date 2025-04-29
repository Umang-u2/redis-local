package com.myredis.datastore;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ValueWrapper {

  private String value;
  private Long expiryTime;

  @JsonCreator
  public ValueWrapper(@JsonProperty("value") String value,
                      @JsonProperty("expiryTime") Long expiryTime) {
    this.value = value;
    this.expiryTime = expiryTime;
  }

  public String getValue() {
    return value;
  }

  public Long getExpiryTime() {
    return expiryTime;
  }

  @JsonIgnore
  public boolean isExpired(){
    if(expiryTime == null){
      return false; // no expiry set
    } else {
      return System.currentTimeMillis()>expiryTime;
    }
  }

  // Optional: Setters, if you want to mutate the object
  public void setValue(String value) {
    this.value = value;
  }

  public void setExpiryTime(Long expiryTime) {
    this.expiryTime = expiryTime;
  }

  @Override
  public String toString() {
    return "ValueWrapper{value='" + value + "', expiryTime=" + expiryTime + "}";
  }
}
