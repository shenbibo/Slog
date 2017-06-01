package com.sky.slog.parse;

/**
 * 对象解析接口，返回解析的结果为String
 *
 * Created by sky on 2017/5/27.
 */

public interface Parser<T> {
    Class<T> getParseType();

    /**
     * @param t, 要解析的数据类型
     * */
    String parseToString(T t);
}
