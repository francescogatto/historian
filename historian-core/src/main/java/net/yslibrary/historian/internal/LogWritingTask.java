package net.yslibrary.historian.internal;

import net.yslibrary.historian.Historian;

/**
 * Runnable implementation writing logs and executing callbacks
 */

public class LogWritingTask implements Runnable {

  private final Historian.Callbacks callbacks;
  private final LogWriterDB logWriterDB;
  private final LogEntity log;

  public LogWritingTask(Historian.Callbacks callbacks,
                        LogWriterDB logWriterDB,
                        LogEntity log) {
    this.callbacks = callbacks;
    this.logWriterDB = logWriterDB;
    this.log = log;
  }

  @Override
  public void run() {

    try {
      logWriterDB.log(log);

      callbacks.onSuccess();
    } catch (final Throwable throwable) {
      callbacks.onFailure(throwable);
    }
  }
}
