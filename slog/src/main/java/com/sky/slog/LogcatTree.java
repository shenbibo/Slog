package com.sky.slog;

import android.util.Log;

/**
 * [打印日志到logcat]
 * [detail]
 * Created by Sky on 2017/5/26.
 */

public class LogcatTree extends Tree {
    @Override
    protected void log(int priority, String tag, String message) {
        if (priority == Log.ASSERT) {
            Log.wtf(tag, message);
        } else {
            Log.println(priority, tag, message);
        }
    }
}
