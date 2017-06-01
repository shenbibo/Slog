package com.sky.slog;


import com.sky.slog.LogController.LogManagerImpl;

/**
 * 对象创建工具类
 * Created by sky on 2017/5/29.
 */
final class LogFactory {
    static LogAssembler createLogController(){
        return new LogAssemblerImpl();
    }

    static LogController createLogManager(){
        return new LogManagerImpl();
    }
}
