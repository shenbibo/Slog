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

    private String getLineCompoundStr(String line) {
        return HORIZONTAL_DOUBLE_LINE + " " + line;
    }
}
