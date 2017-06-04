package com.sky.slog;

import java.util.List;

import static com.sky.slog.Helper.createThreadInfo;
import static com.sky.slog.Helper.formatMessage;
import static com.sky.slog.Helper.getSimpleClassName;
import static com.sky.slog.Helper.getStackTraceString;
import static com.sky.slog.Helper.splitString;
import static com.sky.slog.LogConstant.BOTTOM_BORDER;
import static com.sky.slog.LogConstant.HORIZONTAL_DOUBLE_LINE;
import static com.sky.slog.LogConstant.LINE_SEPARATOR;
import static com.sky.slog.LogConstant.MIDDLE_BORDER;
import static com.sky.slog.LogConstant.OBJECT_NULL_STRING;
import static com.sky.slog.LogConstant.TOP_BORDER;

/**
 * [日志中间处理转发类]
 * [detail]
 * Created by Sky on 2017/5/25.
 */
class LogAssemblerImpl extends LogAssembler {

    //    /** 当传递的message是空时，先包装成一个空对象，在分发之前在变成原来的null */
    //    @SuppressWarnings("RedundantStringConstructorCall")
    //    private final String NULL_STRING = new String("");
    //    /** 当日志方法传递的object是空时，先包装成一个空对象，在分发之前在变成原来的null **/
    //    private final Object NULL_OBJECT = new Object();

    //    private Setting setting;
    //    private List<String> callerClassNames;
    //    private LogDispatcher dispatcher;
    //    private String thisClassName = getClass().getName();

    //    @Override
    //    public void log(int priority, @Nullable String tag, @Nullable Throwable t, @Nullable String msg, @Nullable Object... args) {
    //        if (msg == null) {
    //            msg = NULL_STRING;
    //        }
    //        super.log(priority, tag, t, msg, args);
    //    }
    //
    //    @Override
    //    public void object(int priority, String tag, Object object) {
    //        if (object == null) {
    //            object = NULL_OBJECT;
    //        }
    //        super.object(priority, tag, object);
    //    }

    @Override
    protected String[] onSimpleModeLog(int priority, String tag, Throwable t, Object originalObject, Object... args) {
        return compoundMessage(t, originalObject, args);
    }

    @Override
    protected void onFormatModeLogTop(List<String> compoundMessagesList) {
        compoundMessagesList.add(TOP_BORDER);
    }

    @Override
    protected void onFormatModeLogThreadInfo(List<String> compoundMessagesList, Thread curThread) {
        String threadInfo = createThreadInfo(curThread);
        compoundMessagesList.add(getLineCompoundStr(threadInfo));
        logDivider(compoundMessagesList);
    }

    @Override
    protected void onFormatModeLogMethodStackTrace(List<String> compoundMessagesList, int methodCount, int stackOffset, StackTraceElement[] trace) {
        // 构造堆栈数据
        String level = "";
        for (int i = methodCount; i > 0; i--) {
            int stackIndex = i + stackOffset;
            if (stackIndex >= trace.length) {
                continue;
            }

            StringBuilder builder = new StringBuilder();
            builder.append("║ ")
                   .append(level)
                   .append(getSimpleClassName(trace[stackIndex].getClassName()))
                   .append(".")
                   .append(trace[stackIndex].getMethodName())
                   .append("(")
                   .append(trace[stackIndex].getFileName())
                   .append(":")
                   .append(trace[stackIndex].getLineNumber())
                   .append(")");
            level += "   ";

            compoundMessagesList.add(builder.toString());
        }

        logDivider(compoundMessagesList);
    }

    @Override
    protected void onFormatModeLogContent(List<String> compoundMessagesList, Throwable t, Object originalObject, Object[] args) {
        String[] compoundMessages = compoundMessage(t, originalObject, args);
        for (String compoundMessage : compoundMessages) {
            String[] splitMessages = compoundMessage.split(LINE_SEPARATOR);
            for (String splitMessage : splitMessages) {
                compoundMessagesList.add(getLineCompoundStr(splitMessage));
            }
        }
    }

    @Override
    protected void onFormatModeLogBottom(List<String> compoundMessagesList) {
        compoundMessagesList.add(BOTTOM_BORDER);
    }

    //    @Override
    //    public void json(String json) {
    //        log(DEBUG, null, null, covertJson(json));
    //    }

    //    @Override
    //    public void xml(String xml) {
    //        log(DEBUG, null, null, covertXml(xml));
    //    }

    //    @Override
    //    void init(@NonNull List<String> callerClassName, @NonNull Setting logSetting, @NonNull LogDispatcher logDispatcher) {
    //        this.callerClassNames = callerClassName;
    //        setting = logSetting;
    //        dispatcher = logDispatcher;
    //    }

    //    private void logParse(int priority, String tag, Throwable t, Object originalObject, Object... args) {
    //        if (!isLoggable(priority)) {
    //            return;
    //        }
    //
    //        String finalTag = tag != null ? setting.createCompoundTag(tag) : setting.getLocalTag();
    //        // 简单模式不组装消息直接返回
    //        if (setting.isLocalSimpleMode()) {
    //            logSimpleMode(priority, finalTag, t, originalObject, args);
    //            return;
    //        }
    //
    //        logCompoundMode(priority, finalTag, t, originalObject, args);
    //
    //    }

//    private void logSimpleMode(int priority, String tag, Throwable t, Object originalObject, Object... args) {
//        String[] compoundMessages = compoundMessage(t, originalObject, args);
//        dispatchLog(priority, tag, t, compoundMessages, originalObject, args);
//    }
//
//    private void logCompoundMode(int priority, String tag, Throwable t, Object originalObject, Object... args) {
//        int methodCount = setting.getLocalMethodCount();
//        // 初始化时指定容量，避免发生拷贝
//        ArrayList<String> compoundMessages = new ArrayList<>(12 + (int) (methodCount / 0.75));
//        logTopBorder(compoundMessages);
//        logHeaderContent(compoundMessages, methodCount);
//
//        if (methodCount > 0) {
//            logDivider(compoundMessages);
//        }
//
//        logContent(compoundMessages, t, originalObject, args);
//        logBottomBorder(compoundMessages);
//        dispatchLog(priority, tag, t, compoundMessages.toArray(new String[compoundMessages.size()]), originalObject, args);
//    }

    /** 组装格式化字符串或解析对象，并且如果长度超过限制则进行分割 */
    private String[] compoundMessage(Throwable t, Object originalObject, Object... args) {
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


    private void logTopBorder(List<String> messagesList) {
        messagesList.add(TOP_BORDER);
    }

    @SuppressWarnings("StringBufferReplaceableByString")
    private void logHeaderContent(List<String> messagesList, int methodCount) {
        // 获取当前线程信息和stack，组装线程数据
        Thread curThread = Thread.currentThread();
        StackTraceElement[] trace = curThread.getStackTrace();
        if (setting.isLocalShowThreadInfo()) {
            String threadInfo = createThreadInfo(curThread);
            messagesList.add(getLineCompoundStr(threadInfo));
            logDivider(messagesList);
        }

        // 校正最终要输出的方法数目，不能比整个栈还长
        int stackOffset = getFinalStackOffset(trace);
        if (methodCount + stackOffset > trace.length) {
            methodCount = trace.length - stackOffset - 1;
        }

        // 构造堆栈数据
        String level = "";
        for (int i = methodCount; i > 0; i--) {
            int stackIndex = i + stackOffset;
            if (stackIndex >= trace.length) {
                continue;
            }

            StringBuilder builder = new StringBuilder();
            builder.append("║ ")
                   .append(level)
                   .append(getSimpleClassName(trace[stackIndex].getClassName()))
                   .append(".")
                   .append(trace[stackIndex].getMethodName())
                   .append("(")
                   .append(trace[stackIndex].getFileName())
                   .append(":")
                   .append(trace[stackIndex].getLineNumber())
                   .append(")");
            level += "   ";

            messagesList.add(builder.toString());
        }
    }

    private void logBottomBorder(List<String> messagesList) {
        messagesList.add(BOTTOM_BORDER);
    }

    private void logDivider(List<String> messagesList) {
        messagesList.add(MIDDLE_BORDER);
    }

    private void logContent(List<String> messagesList, Throwable t, Object originalObject, Object... args) {
        String[] compoundMessages = compoundMessage(t, originalObject, args);
        for (String compoundMessage : compoundMessages) {
            String[] splitMessages = compoundMessage.split(LINE_SEPARATOR);
            for (String splitMessage : splitMessages) {
                messagesList.add(getLineCompoundStr(splitMessage));
            }
        }
    }

    //    private void dispatchLog(int priority, String tag, Throwable t, String[] compoundMessages, Object originalObject,
    //            Object... args) {
    //        if (originalObject instanceof String) {
    //            dispatcher.log(priority, tag, t, compoundMessages, originalObject == NULL_STRING ? null : (String) originalObject,
    //                    args);
    //        } else {
    //            dispatcher.log(priority, tag, compoundMessages, originalObject == NULL_OBJECT ? null : originalObject);
    //        }
    //    }

    //    private int getFinalStackOffset(StackTraceElement[] stackTraceElements) {
    //        int userStackOffset = setting.getLocalStackOffset();
    //        return userStackOffset + getMinStackOffset(stackTraceElements);
    //    }
    //
    //    /**
    //     * 获取最小的堆栈偏移值，获取线程的stack，基本算法为当第一次出现stack中元素的类型不为logController和其调用者时，
    //     * 就认为当前位置为最小偏移值。
    //     *
    //     * @param trace the stack trace
    //     * @return the stack offset
    //     */
    //    private int getMinStackOffset(StackTraceElement[] trace) {
    //        for (int i = MIN_STACK_OFFSET; i < trace.length; i++) {
    //            StackTraceElement e = trace[i];
    //            String name = e.getClassName();
    //            if (!name.equals(thisClassName) && !callerClassNames.contains(name)) {
    //                // 因为数组从0，开始所有要-1
    //                return --i;
    //            }
    //        }
    //        return -1;
    //    }

    private String getLineCompoundStr(String line) {
        return HORIZONTAL_DOUBLE_LINE + " " + line;
    }

    //    private boolean isLoggable(int priority) {
    //        return priority >= setting.getLogPriority() && priority > FULL && priority < NONE;
    //    }
}
