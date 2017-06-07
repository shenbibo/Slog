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
 * [默认日志组装实现类]
 * [detail]
 * Created by Sky on 2017/5/25.
 */
class LogAssemblerImpl extends LogAssembler {

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

            //noinspection StringBufferReplaceableByString
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

    private void logDivider(List<String> messagesList) {
        messagesList.add(MIDDLE_BORDER);
    }

    private String getLineCompoundStr(String line) {
        return HORIZONTAL_DOUBLE_LINE + " " + line;
    }
}
