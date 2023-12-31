package org.jetbrains.idea.maven.server;

import org.codehaus.plexus.logging.Logger;

public class Maven3ServerConsoleLogger extends MavenRemoteObject implements Logger, MavenServerConsoleIndicatorWrapper {
  private static final String LINE_SEPARATOR = System.getProperty("line.separator");

  private MavenServerConsoleIndicatorImpl myWrappee;
  private int myThreshold;

  void doPrint(int level, String message, Throwable throwable) {
    if (level < myThreshold) return;

    if (!message.endsWith(LINE_SEPARATOR)) {
      message += LINE_SEPARATOR;
    }

    if (myWrappee != null) {
      myWrappee.printMessage(level, message, wrapException(throwable));
    }
  }

  @Override
  public void setWrappee(MavenServerConsoleIndicatorImpl wrappee) {
    myWrappee = wrappee;
  }

  @Override
  public void debug(String string, Throwable throwable) {
    doPrint(MavenServerConsoleIndicator.LEVEL_DEBUG, string, throwable);
  }

  @Override
  public void info(String string, Throwable throwable) {
    doPrint(MavenServerConsoleIndicator.LEVEL_INFO, string, throwable);
  }

  @Override
  public void warn(String string, Throwable throwable) {
    doPrint(MavenServerConsoleIndicator.LEVEL_WARN, string, throwable);
  }

  @Override
  public void error(String string, Throwable throwable) {
    doPrint(MavenServerConsoleIndicator.LEVEL_ERROR, string, throwable);
  }

  @Override
  public void fatalError(String string, Throwable throwable) {
    doPrint(MavenServerConsoleIndicator.LEVEL_FATAL, string, throwable);
  }

  @Override
  public void debug(String message) {
    debug(message, null);
  }

  @Override
  public boolean isDebugEnabled() {
    return getThreshold() <= MavenServerConsoleIndicator.LEVEL_DEBUG;
  }

  @Override
  public void info(String message) {
    info(message, null);
  }

  @Override
  public boolean isInfoEnabled() {
    return getThreshold() <= MavenServerConsoleIndicator.LEVEL_INFO;
  }

  @Override
  public void warn(String message) {
    warn(message, null);
  }

  @Override
  public boolean isWarnEnabled() {
    return getThreshold() <= MavenServerConsoleIndicator.LEVEL_WARN;
  }

  @Override
  public void error(String message) {
    error(message, null);
  }

  @Override
  public boolean isErrorEnabled() {
    return getThreshold() <= MavenServerConsoleIndicator.LEVEL_ERROR;
  }

  @Override
  public void fatalError(String message) {
    fatalError(message, null);
  }

  @Override
  public boolean isFatalErrorEnabled() {
    return getThreshold() <= MavenServerConsoleIndicator.LEVEL_FATAL;
  }

  @Override
  public void setThreshold(int threshold) {
    this.myThreshold = threshold;
  }

  @Override
  public int getThreshold() {
    return myThreshold;
  }

  @Override
  public Logger getChildLogger(String s) {
    return null;
  }

  @Override
  public String getName() {
    return toString();
  }
}
