package com.leansoft.nano.log;

import android.util.Log;

public class ALog {
    private static String debugTag = "myApp";
    private static DebugLevel debugLevel = DebugLevel.VERBOSE;

    private ALog() {
    }

    public static String getDebugTag() {
        return ALog.debugTag;
    }

    public static void setDebugTag(final String debugTag) {
        ALog.debugTag = debugTag;
    }

    public static DebugLevel getDebugLevel() {
        return ALog.debugLevel;
    }

    public static void setDebugLevel(final DebugLevel debugLevel) {
        if (debugLevel == null) {
            throw new IllegalArgumentException("debugLevel must not be null!");
        }
        ALog.debugLevel = debugLevel;
    }

    public static void v(final String message) {
        ALog.v(debugTag, message);
    }

    public static void v(final String tag, final String message) {
        if (isDebuggable(DebugLevel.VERBOSE) == false) {
            return;
        }
        Log.v(tag, message);
    }
    
    
	public static void debugLongMessage(final String tag, String msg) {
        if (isDebuggable(DebugLevel.DEBUG) == false) {
            return;
        }
		println(tag, msg);
	}
	
	private static void println(final String tag, String msg) {
		if (msg == null) return;

	    int length = msg.length();
	    int c = Log.println(Log.DEBUG, tag, msg);
       final int len = c;
	    while (c < length)
       {
          String str = null;
          if (c + len > length)
          {
             str = msg.substring(c, length);
          }
          else
          {
             str = msg.substring(c, c + len);
          }
	       c += Log.println(Log.DEBUG, tag, msg);
	    }
	}
    

    public static void d(final String message) {
        ALog.d(debugTag, message, null);
    }

    public static void d(final String message, final Throwable throwable) {
        ALog.d(debugTag, message, throwable);
    }

    public static void d(final String tag, final String message) {
        ALog.d(tag, message, null);
    }

    public static void d(final String tag, final String message, final Throwable throwable) {
        if (isDebuggable(DebugLevel.DEBUG) == false) {
            return;
        }
        if (throwable == null) {
            Log.d(tag, message);
        } else {
            Log.d(tag, message, throwable);
        }
    }

    public static void i(final String message) {
        ALog.i(debugTag, message, null);
    }

    public static void i(final String message, final Throwable throwable) {
        ALog.i(debugTag, message, throwable);
    }

    public static void i(final String tag, final String message) {
        ALog.i(tag, message, null);
    }

    public static void i(final String tag, final String message, final Throwable throwable) {
        if (isDebuggable(DebugLevel.INFO) == false) {
            return;
        }
        if (throwable == null) {
            Log.i(tag, message);
        } else {
            Log.i(tag, message, throwable);
        }
    }

    public static void w(final String message) {
        ALog.w(debugTag, message, null);
    }

    public static void w(final Throwable throwable) {
        ALog.w(debugTag, "", throwable);
    }

    public static void w(final String tag, final String message) {
        ALog.w(tag, message, null);
    }

    public static void w(final String tag, final String message, final Throwable throwable) {
        if (isDebuggable(DebugLevel.WARNING) == false) {
            return;
        }
        if (throwable == null) {
            Log.w(tag, message, new Exception());
        } else {
            Log.w(tag, message, throwable);
        }
    }

    public static void e(final String message) {
        ALog.e(debugTag, message, null);
    }

    public static void e(final Throwable throwable) {
        ALog.e(debugTag, "", throwable);
    }

    public static void e(final String tag, final String message) {
        ALog.e(tag, message, null);
    }

    public static void e(final String tag, final String message, final Throwable throwable) {
        if (isDebuggable(DebugLevel.ERROR) == false) {
            return;
        }
        if (throwable == null) {
            Log.e(tag, message);
            return;
        }
        Log.e(tag, message, throwable);
    }

    public static boolean isDebuggable(DebugLevel level) {
        return debugLevel.isDebuggable(level);
    }

    public static enum DebugLevel implements Comparable<DebugLevel> {
        NONE, ERROR, WARNING, INFO, DEBUG, VERBOSE;

        public static DebugLevel ALL = DebugLevel.VERBOSE;

        private boolean isDebuggable(final DebugLevel debugLevel) {
            return this.compareTo(debugLevel) >= 0;
        }
    }
}
