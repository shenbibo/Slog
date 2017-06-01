package com.sky.slog;


import android.support.annotation.Nullable;

import static com.sky.slog.Slog.*;

/**
 * [默认实现中，只处理合成和的字符串]
 * [detail]
 * Created by Sky on 2017/5/25.
 */
public abstract class Tree {

    public void v(String tag, Throwable t, String[] compoundMessages,
            @Nullable String originalMessage, @Nullable Object... args) {
        prepareStringLog(VERBOSE, tag, t, compoundMessages, originalMessage, args);
    }

    public void v(String tag, String[] compoundMessages, @Nullable Object object) {
        prepareObjectLog(VERBOSE, tag, compoundMessages, object);
    }

    public void d(String tag, Throwable t, String[] compoundMessages,
            @Nullable String originalMessage, @Nullable Object... args) {
        prepareStringLog(DEBUG, tag, t, compoundMessages, originalMessage, args);
    }

    public void d(String tag, String[] compoundMessages, @Nullable Object object) {
        prepareObjectLog(DEBUG, tag, compoundMessages, object);
    }

    public void i(String tag, Throwable t, String[] compoundMessages,
            @Nullable String originalMessage, @Nullable Object... args) {
        prepareStringLog(INFO, tag, t, compoundMessages, originalMessage, args);
    }

    public void i(String tag, String[] compoundMessages, @Nullable Object object) {
        prepareObjectLog(INFO, tag, compoundMessages, object);
    }

    public void w(String tag, Throwable t, String[] compoundMessages,
            @Nullable String originalMessage, @Nullable Object... args) {
        prepareStringLog(WARN, tag, t, compoundMessages, originalMessage, args);
    }

    public void w(String tag, String[] compoundMessages, @Nullable Object object) {
        prepareObjectLog(WARN, tag, compoundMessages, object);
    }

    public void e(String tag, Throwable t, String[] compoundMessages,
            @Nullable String originalMessages, @Nullable Object... args) {
        prepareStringLog(ERROR, tag, t, compoundMessages, originalMessages, args);
    }

    public void e(String tag, String[] compoundMessages, @Nullable Object object) {
        prepareObjectLog(ERROR, tag, compoundMessages, object);
    }

    public void wtf(String tag, Throwable t, String[] compoundMessages,
            @Nullable String originalMessages, @Nullable Object... args) {
        prepareStringLog(ASSERT, tag, t, compoundMessages, originalMessages, args);
    }

    public void wtf(String tag, String[] compoundMessages, @Nullable Object object) {
        prepareObjectLog(ASSERT, tag, compoundMessages, object);
    }

    /**
     * 对String日志进行处理，它被所有打印String日志的上述公共方法调用，该方法内部最终调用抽象{@link Tree#log(int, String, String)}方法
     * <br>
     * 默认实现中只处理合成后的日志，不处理原始日志，所有的上述方法都调用该方法，子类如果复写该方法，根据需要确定在最后调用super方法
     */
    protected void prepareStringLog(int priority, String tag, Throwable t,
            String[] compoundMessages, @Nullable String originalMessages, @Nullable Object... args) {
        defaultOnMessages(priority, tag, compoundMessages);
    }

    /**
     * 对Object日志进行处理，它被所有打印String日志的上述公共方法调用，该方法内部最终调用抽象{@link Tree#log(int, String, String)}方法
     * <br>
     * 默认实现中只处理合成后的日志，不处理原始日志，所有的上述方法都调用该方法，子类如果复写该方法，根据需要确定在最后调用super方法
     */
    protected void prepareObjectLog(int priority, String tag, String[] compoundMessages, @Nullable Object originalObject) {
        defaultOnMessages(priority, tag, compoundMessages);
    }

    /**
     * 默认返回true，默认实现中该方法被{@link Tree#prepareStringLog(int, String, Throwable, String[], String, Object...)},
     * {@link Tree#prepareObjectLog(int, String, String[], Object)}调用。
     */
    protected boolean isLoggable(int priority, String tag) {
        return true;
    }

    /**
     * 最终对日志进行处理，比如打印到logcat中，默认实现中该方法被{@link Tree#prepareStringLog(int, String, Throwable, String[],
     * String, Object...)},{@link Tree#prepareObjectLog(int, String, String[], Object)}调用。
     */
    protected abstract void log(int priority, String tag, String message);

    /**
     * 默认只处理封装好的数据，原始数据不处理
     */
    private void defaultOnMessages(int priority, String tag, String[] compoundMessages) {
        if (isLoggable(priority, tag)) {
            if (compoundMessages.length == 1) {
                log(priority, tag, compoundMessages[0]);
            } else {
                logMessages(priority, tag, compoundMessages);
            }
        }
    }

    private synchronized void logMessages(int priority, String tag, String[] compoundMessages) {
        for (String message : compoundMessages) {
            log(priority, tag, message);
        }
    }
}
