package com.androider.legacy.common;

import android.util.Log;

/**
 * Created by bao on 2015/4/4.
 */
public final class Logger {

    private static final boolean DEBUG = true;

    private Logger() {}

    private static Logger logger;

    public static Logger getInstance() {
        synchronized (Logger.class) {
            if (logger == null) {
                logger = new Logger();
            }
            return logger;
        }
    }

    public synchronized void debug(String tag, String msg) {
        if (DEBUG) {
            Log.d(tag, msg);
        }
    }

    public synchronized void error(String tag, String msg) {
        if (DEBUG) {
            Log.e(tag, msg);
        }
    }

    public synchronized void warning(String tag, String msg) {
        if (DEBUG) {
            Log.w(tag, msg);
        }
    }

    public synchronized void info(String tag, String msg) {
        if (DEBUG) {
            Log.i(tag, msg);
        }
    }

    public synchronized void verbose(String tag, String msg) {
        if (DEBUG) {
            Log.v(tag, msg);
        }
    }
}
