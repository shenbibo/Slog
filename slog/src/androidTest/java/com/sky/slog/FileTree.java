package com.sky.slog;

import android.support.annotation.Nullable;

/**
 * [一句话描述类的作用]
 * [详述类的功能。]
 * Created by sky on 2017/6/6.
 */

public class FileTree extends Tree {

    // 处理对象类型的日志，注意该接口方法，也可以根据原始的`originalObject`参数进行自定义处理
    @Override
    protected void prepareObjectLog(int priority, String tag, String[] compoundMessages, @Nullable Object originalObject) {
        super.prepareObjectLog(priority, tag, compoundMessages, originalObject);
    }

    // 处理String类型的日志，注意该接口方法，也可以根据原始的`originalMessage`参数进行自定义处理
    @Override
    protected void prepareStringLog(int priority, String tag, Throwable t, String[] compoundMessages, @Nullable String originalMessages, @Nullable Object... args) {
        super.prepareStringLog(priority, tag, t, compoundMessages, originalMessages, args);
    }

    // 该方法为必须要实现的父类抽象方法
    @Override
    protected void log(int priority, String tag, String message) {
        // ... 省略代码将日志保存到文件中
    }
}
