package com.myredis.datastore;

import java.nio.file.Paths;

public class ConfigurationManager {

  private static final String DEFAULT_RDB_FILENAME = "dump.rdb";
  private static final String DEFAULT_RDB_DIR = System.getProperty("user.dir"); // Default to current working dir
  private static String rdbDirectory = System.getenv("RDB_DIR") != null ? System.getenv("RDB_DIR") : DEFAULT_RDB_DIR;

  // Get the RDB file directory
  public static String getRdbDirectory() {
    return rdbDirectory;
  }

  // Get the full path to the RDB file
  public static String getRdbFilePath() {
    return Paths.get(getRdbDirectory(), DEFAULT_RDB_FILENAME).toString();
  }

  // Set a custom directory (if needed)
  public static void setRdbDirectory(String directory) {
    rdbDirectory = directory;
  }

}
