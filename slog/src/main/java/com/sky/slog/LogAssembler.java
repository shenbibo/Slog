package com.sky.slog;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.sky.slog.Helper.covertJson;
import static com.sky.slog.Helper.covertXml;
import static com.sky.slog.Helper.formatMessage;
import static com.sky.slog.Helper.getStackTraceString;
import static com.sky.slog.Helper.splitString;
import static com.sky.slog.LogConstant.OBJECT_NULL_STRING;
import static com.sky.slog.Slog.DEBUG;
import static com.sky.slog.Slog.FULL;
import static com.sky.slog.Slog.NONE;

/**
 * 日志组装器，对日志进行包裹，显示成特定的类型
 * Created by Sky on 2017/5/25.
 */

public abstract class LogAssembler {
    /**
     * 最小的栈偏移值，因为该类本身和包裹它的类，所以默认偏移2
     */
    protected static final int MIN_STACK_OFFSET = 6;

    /** 当传递的message是空时，先包装成一个空对象，在分发之前在变成原来的null */
    @SuppressWarnings("RedundantStringConstructorCall")
    protected final String NULL_STRING = new String("");
    /** 当日志方法传递的object是空时，先包装成一个空对象，在分发之前在变成原来的null **/
    protected final Object NULL_OBJECT = new Object();

    protected Setting setting;
    protected List<String> callerClassNames;
    protected LogDispatcher dispatcher;
    protected String thisClassName = getClass().getName();

    /** Log at {@code priority} an exception and a message with optional format args. */
    public void log(int priority, String tag, Throwable t, String msg, @Nullable Object... args) {
        if (msg == null) {
            msg = NULL_STRING;
        }

        logParse(priority, tag, t, msg, args);
    }

    /** 打印对象 */
    public void object(int priority, @Nullable String tag, @Nullable Object object) {
        if (object == null) {
            object = NULL_OBJECT;
        }

        if (object instanceof String) {
            log(priority, tag, null, (String) object);
        } else {
            logParse(priority, tag, null, object);
        }
    }

    public void json(String json) {
        log(DEBUG, null, null, covertJson(json));
    }

    public void xml(String xml) {
        log(DEBUG, null, null, covertXml(xml));
    }

    /**
     * @param callerClassName 其上层调用这的类名数组，主要用于过滤日志的打印堆栈
     * @param logSetting      日志配置
     * @param logDispatcher   日志分发器
     */
    void init(@NonNull List<String> callerClassName, @NonNull Setting logSetting, @NonNull LogDispatcher logDispatcher) {
        this.callerClassNames = callerClassName;
        setting = logSetting;
        dispatcher = logDispatcher;
    }

    protected void logParse(int priority, String tag, Throwable t, Object originalObject, Object... args) {
        if (!isLoggable(priority)) {
            return;
        }

        String finalTag = tag != null ? tag : setting.getLocalTag();
        String[] compoundMessages;

        // 简单模式不组装消息直接返回
        if (setting.isLocalSimpleMode()) {
            compoundMessages = onSimpleModeLog(priority, finalTag, t, originalObject, args);
        } else {
            int localMethodCount = setting.getLocalMethodCount();
            // 初始化时指定容量，避免发生拷贝
            List<String> compoundMessagesList = new ArrayList<>(12 + (int) (localMethodCount / 0.75));
            Thread curThread = Thread.currentThread();

            // 添加日志头
            onFormatModeLogTop(compoundMessagesList);

            // 添加线程信息
            if (setting.isLocalShowThreadInfo()) {
                onFormatModeLogThreadInfo(compoundMessagesList, curThread);
            }

            // 添加函数调用堆栈
            if (localMethodCount > 0) {
                StackTraceElement[] trace = curThread.getStackTrace();
                int finalStackOffset = getFinalStackOffset(trace);
                int finalMethodCount = getFinalMethodCount(localMethodCount, finalStackOffset, trace.length);
                if (finalMethodCount > 0) {
                    onFormatModeLogMethodStackTrace(compoundMessagesList, finalMethodCount, finalStackOffset, trace);
                }
            }

            // 打印消息内容
            onFormatModeLogContent(compoundMessagesList, t, originalObject, args);

            // 添加日志尾
            onFormatModeLogBottom(compoundMessagesList);

            compoundMessages = compoundMessagesList.toArray(new String[compoundMessagesList.size()]);
        }

        dispatchLog(priority, finalTag, t, compoundMessages, originalObject, args);
    }

    protected String[] onSimpleModeLog(int priority, String tag, Throwable t, Object originalObject, Object... args){
        return compoundMessage(t, originalObject, args);
    }

    protected abstract void onFormatModeLogThreadInfo(List<String> compoundMessagesList, Thread curThread);

    protected abstract void onFormatModeLogMethodStackTrace(List<String> compoundMessagesList, int methodCount, int stackOffset, StackTraceElement[] trace);

    protected abstract void onFormatModeLogContent(List<String> compoundMessagesList, Throwable t, Object originalObject, Object[] args);

    protected boolean isLoggable(int priority) {
        return priority >= setting.getLogPriority() && priority > FULL && priority < NONE;
    }

    protected void onFormatModeLogTop(List<String> compoundMessagesList) {
    }

    protected void onFormatModeLogBottom(List<String> compoundMessagesList) {
    }

    protected void dispatchLog(int priority, String tag, Throwable t, String[] compoundMessages, Object originalObject,
            Object... args) {
        if (originalObject instanceof String) {
            dispatcher.log(priority, tag, t, compoundMessages, originalObject == NULL_STRING ? null : (String) originalObject,
                    args);
        } else {
            dispatcher.log(priority, tag, compoundMessages, originalObject == NULL_OBJECT ? null : originalObject);
        }
    }

    protected int getFinalStackOffset(StackTraceElement[] stackTraceElements) {
        int userStackOffset = setting.getLocalStackOffset();
        return userStackOffset + getMinStackOffset(stackTraceElements);
    }

    /**
     * 获取最小的堆栈偏移值，获取线程的stack，基本算法为当第一次出现stack中元素的类型不为logController和其调用者时，
     * 就认为当前位置为最小偏移值。
     *
     * @param trace the stack trace
     * @return the stack offset
     */
    protected int getMinStackOffset(StackTraceElement[] trace) {
        for (int i = MIN_STACK_OFFSET; i < trace.length; i++) {
            StackTraceElement e = trace[i];
            String name = e.getClassName();
            if (!name.equals(thisClassName) && !callerClassNames.contains(name)) {
                // 因为数组从0，开始所有要-1
                return --i;
            }
        }
        return -1;
    }

    /**
     * 校正最终要输出的方法数目，不能比整个栈还长
     */
    protected int getFinalMethodCount(int localMethodCount, int stackOffset, int trackLength) {
        int methodCount = localMethodCount;
        if (methodCount + stackOffset > trackLength) {
            methodCount = trackLength - stackOffset - 1;
        }
        return methodCount;
    }

    /** 组装格式化字符串或解析对象，并且如果长度超过限制则进行分割 */
    protected String[] compoundMessage(Throwable t, Object originalObject, Object... args) {
        String compoundMessage;
        if (originalObject == NULL_OBJECT || originalObject == NULL_STRING) {
            compoundMessage = OBJECT_NULL_STRING;
        } else {
            if (originalObject instanceof String) {
                // 优化代码，如果args参数为null,或者长度为0，则直接返回字符串
                if (args == null || args.length == 0) {
                    compoundMessage = (String) originalObject;
                } else {
                    compoundMessage = formatMessage((String) originalObject, args);
                }
            } else {
                compoundMessage = ParseObject.objectToString(originalObject);
            }
        }

        if (t != null) {
            compoundMessage += " : " + getStackTraceString(t);
        }

        return splitString(compoundMessage);
    }
}
