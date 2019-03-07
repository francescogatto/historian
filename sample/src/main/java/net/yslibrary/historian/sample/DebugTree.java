package net.yslibrary.historian.sample;

import timber.log.Timber;

class DebugTree extends Timber.DebugTree {
  @Override
  protected String createStackElementTag(StackTraceElement element) {
    return String.format("(%s:%s)#%s",
        element.getFileName(),
        element.getLineNumber(),
        element.getMethodName());
  }
}