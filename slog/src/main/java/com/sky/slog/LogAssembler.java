package com.sky.slog;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * [日志组装器，对日志进行包裹，显示成特定的类型]
 * [detail]
 * Created by Sky on 2017/5/25.
 */

public abstract class LogAssembler {
    /** Log a verbose message with optional format args. */
    public abstract void v(String normalMsg, @Nullable Object... args);

    /** Log a debug message with optional format args. */
    public abstract void d(String normalMsg, @Nullable Object... args);

    /** Log a debug message for the object */
    public abstract void dO(Object object);

    /** Log an info message with optional format args. */
    public abstract void i(String normalMsg, @Nullable Object... args);

    /** Log a debug message for the object */
    public abstract void iO(Object object);

    /** Log a warning message with optional format args. */
    public abstract void w(String normalMsg, @Nullable Object... args);

    /** Log a warning exception and a message with optional format args. */
    public abstract void w(Throwable t, String normalMsg, @Nullable Object... args);

    /** Log an error message with optional format args. */
    public abstract void e(String normalMsg, @Nullable Object... args);

    /** Log an error exception and a message with optional format args. */
    public abstract void e(Throwable t, String normalMsg, @Nullable Object... args);

    /** Log an assert message with optional format args. */
    public abstract void wtf(String normalMsg, @Nullable Object... args);

    /** Log an throwable and assert message with optional format args. */
    public abstract void wtf(Throwable t, String normalMsg, @Nullable Object... args);

    /** Log at {@code priority} an exception and a message with optional format args. */
    public abstract void log(int priority, String tag, Throwable t, String normalMsg, @Nullable Object... args);

    /** 打印日志 */
    public abstract void object(int priority, @Nullable String tag, @Nullable Object object);

    public abstract void json(String json);

    public abstract void xml(String xml);

    /** 设置接下来该线程打印一次日志的tag */
    public abstract LogAssembler t(String tag);

    /**
     * 调用该方法后，确定接下来打印的日志显示堆栈内方法的个数，若{@code simpleCode}为true，则设置无效
     *
     * @param methodCount
     */
    public abstract LogAssembler m(Integer methodCount);

    /**
     * 为true，则为普通log打印，有最高的效率
     *
     * @param simpleMode
     */
    public abstract LogAssembler s(Boolean simpleMode);

    /**
     * 调用该方法后，确定接下来打印的日志是否携带线程信息，若{@code simpleCode}为true，则设置无效
     *
     * @param hideThreadInfo
     */
    public abstract LogAssembler th(Boolean hideThreadInfo);

    /**
     * 调用该方法后，设置打印堆栈方法的偏移值，默认值为0，若{@code simpleCode}为true，则设置无效
     */
    public abstract LogAssembler o(Integer methodOffset);

    /**
     * @param callerClass   调用该方法的类的class
     * @param logSetting    日志配置
     * @param logDispatcher 日志分发器
     */
    abstract void init(@NonNull Class<?> callerClass, @NonNull Setting logSetting, @NonNull LogDispatcher logDispatcher);
}
