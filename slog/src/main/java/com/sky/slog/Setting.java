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
    private String defaultTag = Slog.DEFAULT_TAG;

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

    public String getDefaultTag() {
        return defaultTag;
    }

    public Setting defaultTag(String prefixTag) {
        if (prefixTag != null) {
            this.defaultTag = prefixTag;
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

    private ThreadLocal<String> threadLocalTag = new ThreadLocal<>();
    private ThreadLocal<Integer> threadLocalMethodCount = new ThreadLocal<>();
    private ThreadLocal<Boolean> threadLocalSimpleMode = new ThreadLocal<>();
    private ThreadLocal<Boolean> threadLocalShowThreadInfo = new ThreadLocal<>();
    private ThreadLocal<Integer> threadLocalMethodOffset = new ThreadLocal<>();

    Setting t(String tag) {
        if (tag != null) {
            threadLocalTag.set(tag);
        }
        return this;
    }

    Setting m(Integer methodCount) {
        if (methodCount != null) {
            threadLocalMethodCount.set(methodCount);
        }
        return this;
    }

    Setting s(Boolean simpleMode) {
        if (simpleMode != null) {
            threadLocalSimpleMode.set(simpleMode);
        }
        return this;
    }

    Setting th(Boolean showThreadInfo) {
        if (showThreadInfo != null) {
            threadLocalShowThreadInfo.set(showThreadInfo);
        }
        return this;
    }

    Setting o(Integer methodOffset) {
        if (methodOffset != null) {
            threadLocalMethodOffset.set(methodOffset);
        }
        return this;
    }

    String getLocalTag() {
        String localTag = threadLocalTag.get();
        if (localTag != null) {
            threadLocalTag.remove();
        } else {
            localTag = defaultTag;
        }

        return localTag;
    }

    int getLocalMethodCount() {
        Integer localMethodCount = threadLocalMethodCount.get();
        if (localMethodCount != null) {
            threadLocalMethodCount.remove();
        } else {
            localMethodCount = methodCount;
        }
        return localMethodCount;
    }

    int getLocalStackOffset() {
        Integer localMethodOffset = threadLocalMethodOffset.get();
        if (localMethodOffset != null) {
            this.threadLocalMethodOffset.remove();
        } else {
            localMethodOffset = methodOffset;
        }

        return localMethodOffset;
    }

    boolean isLocalSimpleMode() {
        Boolean localSimpleMode = threadLocalSimpleMode.get();
        if (localSimpleMode != null) {
            this.threadLocalSimpleMode.remove();
        } else {
            localSimpleMode = simpleMode;
        }
        return localSimpleMode;
    }

    boolean isLocalShowThreadInfo() {
        Boolean localShowThreadInfo = threadLocalShowThreadInfo.get();
        if (localShowThreadInfo != null) {
            threadLocalShowThreadInfo.remove();
        } else {
            localShowThreadInfo = showThreadInfo;
        }
        return localShowThreadInfo;
    }

    @Override
    public String toString() {
        return "Setting{" +
                "methodCount=" + methodCount +
                ", logPriority=" + logPriority +
                ", methodOffset=" + methodOffset +
                ", showThreadInfo=" + showThreadInfo +
                ", simpleMode=" + simpleMode +
                ", defaultTag='" + defaultTag + '\'' +
                '}';
    }
}
