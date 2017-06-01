package com.sky.slog;

/**
 * [一句话描述类的作用]
 * [详述类的功能。]
 * Created by sky on 2017/5/27.
 */

public class LogConstant {
    /**
     * 绘制用的格式线
     */
    static final char TOP_LEFT_CORNER = '╔';
    static final char BOTTOM_LEFT_CORNER = '╚';
    static final char MIDDLE_CORNER = '╟';
    static final char HORIZONTAL_DOUBLE_LINE = '║';
    static final String DOUBLE_DIVIDER = "════════════════════════════════════════════";
    static final String SINGLE_DIVIDER = "────────────────────────────────────────────";
    static final String TOP_BORDER = TOP_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER;
    static final String BOTTOM_BORDER = BOTTOM_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER;
    static final String MIDDLE_BORDER = MIDDLE_CORNER + SINGLE_DIVIDER + SINGLE_DIVIDER;

    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    public static final String OBJECT_NULL_STRING = "msg object is null";
}
