package net.yslibrary.historian.tree;

import timber.log.Timber;

public class HistorianDebugTree extends Timber.DebugTree {
  @Override
  protected String createStackElementTag(StackTraceElement element) {
    return String.format("(%s:%s) #%s ",
        element.getFileName(),
        element.getLineNumber(),
        element.getMethodName());
  }
}