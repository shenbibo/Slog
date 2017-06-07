package com.sky.slog;

import java.util.Collections;
import java.util.List;

import static com.sky.slog.Helper.formatMessage;
import static com.sky.slog.Helper.getSimpleClassName;
import static com.sky.slog.Helper.getStackTraceString;
import static com.sky.slog.Helper.splitString;
import static com.sky.slog.LogConstant.LINE_SEPARATOR;
import static com.sky.slog.LogConstant.OBJECT_NULL_STRING;

/**
 * 简单版本的日志，不显示任何的横线。
 * <p>
 * 详细内容。
 *
 * @author sky on 2017/6/7
 */

public class SimpleLogAssembler extends LogAssembler {

    @Override
    protected void onFormatModeLogMethodStackTrace(List<String> compoundMessagesList, int methodCount, int stackOffset,
            StackTraceElement[] trace) {
        for (int i = methodCount; i > 0; i--) {
            int stackIndex = i + stackOffset;
            if (stackIndex >= trace.length) {
                continue;
            }

            //noinspection StringBufferReplaceableByString
            StringBuilder builder = new StringBuilder();
            builder.append(getSimpleClassName(trace[stackIndex].getClassName()))
                   .append(".")
                   .append(trace[stackIndex].getMethodName())
                   .append("(")
                   .append(trace[stackIndex].getFileName())
                   .append(":")
                   .append(trace[stackIndex].getLineNumber())
                   .append(")");

            compoundMessagesList.add(builder.toString());
        }
    }

    @Override
    protected void onFormatModeLogThreadInfo(List<String> compoundMessagesList, Thread curThread) {
        compoundMessagesList.add(Helper.createThreadInfo(curThread));
    }

    @Override
    protected void onFormatModeLogContent(List<String> compoundMessagesList, Throwable t, Object originalObject, Object[] args) {
        String[] compoundMessages = compoundMessage(t, originalObject, args);
        for (String compoundMessage : compoundMessages) {
            String[] splitMessages = compoundMessage.split(LINE_SEPARATOR);
            Collections.addAll(compoundMessagesList, splitMessages);
        }
    }
}
