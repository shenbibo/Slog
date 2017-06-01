package com.sky.slog.parse;


import com.sky.slog.ParseObject;

import java.util.*;

import static com.sky.slog.LogConstant.OBJECT_NULL_STRING;

/**
 * [一句话描述类的作用]
 * [详述类的功能。]
 * Created by sky on 2017/5/28.
 */

public class ArrayParser implements Parser<Object[]> {
    public static final ArrayParser ARRAY_PARSER = new ArrayParser();

    @Override
    public Class<Object[]> getParseType() {
        return Object[].class;
    }

    /**
     * 如果要解析基本类型的数组请直接调用{@link ArrayParser#parseArrayToString(Object)}
     *
     * @throws ClassCastException 如果传入基本类型数组
     */
    @Override
    public String parseToString(Object[] objects) {
        return parseArrayToString(objects);
    }


    /**
     * @param object 如果 object 不是一个数组，则返回{@code object.toString()}
     */
    public static String parseArrayToString(Object object) {
        if (object == null) {
            return OBJECT_NULL_STRING;
        }

        Class clazz = object.getClass();

        if (!clazz.isArray()) {
            return object.toString();
        }

        if (!clazz.getComponentType().isPrimitive()) {
            return deepToString((Object[]) object);
        }

        return primitiveArrayToString(object, clazz);
    }

    /**
     * 拷贝自{@link Arrays#deepToString(Object[])}
     */
    private static String deepToString(Object[] a) {
        if (a == null) {
            return OBJECT_NULL_STRING;
        }

        int bufLen = 20 * a.length;
        if (a.length != 0 && bufLen <= 0) {
            bufLen = Integer.MAX_VALUE;
        }
        StringBuilder buf = new StringBuilder(bufLen);
        deepToString(a, buf, new HashSet<Object[]>());
        return buf.toString();
    }

    private static void deepToString(Object[] a, StringBuilder buf, Set<Object[]> dejaVu) {
        int iMax = a.length - 1;
        if (iMax == -1) {
            buf.append("[]");
            return;
        }

        dejaVu.add(a);
        buf.append('[');
        for (int i = 0; ; i++) {

            Object element = a[i];
            if (element == null) {
                buf.append(OBJECT_NULL_STRING);
            } else {
                Class eClass = element.getClass();

                if (eClass.isArray()) {
                    if (eClass.getComponentType().isPrimitive()) {
                        buf.append(primitiveArrayToString(element, eClass));
                    } else { // element is an array of object references
                        //noinspection SuspiciousMethodCalls
                        if (dejaVu.contains(element)) {
                            buf.append("[...]");
                        } else {
                            buf.append(ParseObject.objectToString(element));
                        }
                    }
                } else {  // element is non-null and not an array
                    buf.append(ParseObject.objectToString(element));
                }
            }
            if (i == iMax) {
                break;
            }
            buf.append(", ");
        }
        buf.append(']');
        dejaVu.remove(a);
    }

    private static String primitiveArrayToString(Object object, Class objectClass) {
        if (objectClass == byte[].class) {
            return Arrays.toString((byte[]) object);
        } else if (objectClass == short[].class) {
            return Arrays.toString((short[]) object);
        } else if (objectClass == int[].class) {
            return (Arrays.toString((int[]) object));
        } else if (objectClass == long[].class) {
            return Arrays.toString((long[]) object);
        } else if (objectClass == char[].class) {
            return Arrays.toString((char[]) object);
        } else if (objectClass == float[].class) {
            return Arrays.toString((float[]) object);
        } else if (objectClass == double[].class) {
            return Arrays.toString((double[]) object);
        } else if (objectClass == boolean[].class) {
            return Arrays.toString((boolean[]) object);
        } else {
            throw new RuntimeException("object is not a primitive array");
        }
    }
}
