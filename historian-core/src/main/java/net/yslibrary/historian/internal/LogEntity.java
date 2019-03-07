package net.yslibrary.historian.internal;

import android.util.Log;

/**
 * Entity class representing log
 */

public class LogEntity {
  public final String priority;
  public final String message;
  public final long timestamp;
  public final String tag;
  public final String stackException;

  private LogEntity(String priority, String tag, String message, long timestamp) {
    this.priority = priority;
    this.tag = tag;
    this.message = message;
    this.timestamp = timestamp;
    this.stackException = "";
  }

  private LogEntity(String priority, String tag, String message, long timestamp, Throwable t) {
    this.priority = priority;
    this.tag = tag;
    this.message = message;
    this.timestamp = timestamp;
    this.stackException = t.getStackTrace().toString();
  }

  @SuppressWarnings("WeakerAccess")
  public static LogEntity create(int priority, String tag, String message, long timestamp) {
    return new LogEntity(Util.priorityString(priority), tag, message, timestamp);
  }
  public static LogEntity create(int priority, String tag, String message, long timestamp, Throwable t) {
    return new LogEntity(Util.priorityString(priority), tag, message, timestamp, t);
  }
}
