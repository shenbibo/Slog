package com.sky.slog;

/**
 * 全局配置属性设置
 * [detail]
 * Created by Sky on 2017/5/25.
 */
public class Setting {
    private int methodCount = 1;
    private int logPriority = Slog.FULL;
    private int methodOffset = 0;
    private boolean showThreadInfo = false;
    /** 简单模式，设置为true之后和正常的普通log一样 */
    private boolean simpleMode = false;
    private String prefixTag = Slog.DEFAULT_TAG;

    public int getMethodCount() {
        return methodCount;
    }

    public Setting methodCount(int methodCount) {
        this.methodCount = methodCount < 0 ? 0 : methodCount;
        return this;
    }

    public boolean isShowThreadInfo() {
        return showThreadInfo;
    }

    public Setting showThreadInfo(boolean showThreadInfo) {
        this.showThreadInfo = showThreadInfo;
        return this;
    }

    public int getLogPriority() {
        return logPriority;
    }

    public Setting logPriority(int logPriority) {
        this.logPriority = logPriority;
        return this;
    }

    public String getPrefixTag() {
        return prefixTag;
    }

    public Setting prefixTag(String prefixTag) {
        if (prefixTag != null) {
            this.prefixTag = prefixTag;
        }
        return this;
    }

    public boolean isSimpleMode() {
        return simpleMode;
    }

    /**
     * 如果设置该值为true，则{@link Setting#methodCount},{@link Setting#showThreadInfo},{@link Setting#methodOffset}字段不再生效
     */
    public Setting simpleMode(boolean simpleMode) {
        this.simpleMode = simpleMode;
        return this;
    }

    public int getMethodOffset() {
        return methodOffset;
    }

    public Setting methodOffset(int methodOffset) {
        this.methodOffset = methodOffset;
        return this;
    }

    @Override
    public String toString() {
        return "Setting{" +
                "methodCount=" + methodCount +
                ", logPriority=" + logPriority +
                ", methodOffset=" + methodOffset +
                ", showThreadInfo=" + showThreadInfo +
                ", simpleMode=" + simpleMode +
                ", prefixTag='" + prefixTag + '\'' +
                '}';
    }
}
