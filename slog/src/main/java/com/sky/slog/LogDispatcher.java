package com.sky.slog;

import android.support.annotation.Nullable;

/**
 * [日志分发器]
 * [详述类的功能。]
 * Created by sky on 2017/5/29.
 */

public interface LogDispatcher {
    void log(int priority, String tag, Throwable t, String[] compoundMessages, @Nullable String originalMsg,
            @Nullable Object... args);

    void log(int priority, String tag, String[] compoundMessages, @Nullable Object originalObject);
}
