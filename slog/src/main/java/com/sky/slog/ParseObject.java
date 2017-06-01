package com.sky.slog;



import com.sky.slog.parse.*;

import java.util.concurrent.CopyOnWriteArrayList;

import static com.sky.slog.LogConstant.OBJECT_NULL_STRING;

/**
 * [解析对象]
 * [详述类的功能。]
 * Created by sky on 2017/5/28.
 */

public final class ParseObject {

    private static final CopyOnWriteArrayList<Parser> OBJECT_PARSERS = new CopyOnWriteArrayList<>(new Parser[]{
            IntentParser.INTENT_PARSER,
            BundleParser.BUNDLE_PARSER,
            ArrayParser.ARRAY_PARSER,
            CollectionParser.COLLECTION_PARSER,
            MapParser.MAP_PARSER
    });


    static void init() {}

    /**
     * 每一个确切的类型，只能添加一个对象解析器，如果要添加的已经存在，则替换原来的旧的
     * 将新添加的解析器添加到列表头部
     */
    static void addObjectParser(Parser parserAdapter) {
        if (parserAdapter == null) {
            return;
        }

        Class<? extends Parser> clazz = parserAdapter.getClass();
        synchronized (ParseObject.class) {
            removeObjectParser(clazz);
            OBJECT_PARSERS.add(0, parserAdapter);
        }
    }

    static void removeObjectParser(Parser parserAdapter) {
        if (parserAdapter == null) {
            return;
        }

        removeObjectParser(parserAdapter.getClass());
    }

    static void removeObjectParser(Class<? extends Parser> clazz) {
        if (clazz == null) {
            return;
        }

        synchronized (ParseObject.class) {
            for (Parser parser : OBJECT_PARSERS) {
                if (clazz == parser.getClass()) {
                    OBJECT_PARSERS.remove(parser);
                    break;
                }
            }
        }
    }


    public static String objectToString(Object object) {
        if (object == null) {
            return OBJECT_NULL_STRING;
        }

        if (object instanceof String) {
            return (String) object;
        }

        Parser parser;
        Class<?> objectClazz = object.getClass();
        // 先检测是否有确切的class类型的解析适配器
        if ((parser = getActualParse(objectClazz)) != null) {
            //noinspection unchecked
            return parser.parseToString(object);
        }

        // 再检查是否有其超类的解析适配器
        if ((parser = getAssignableParse(objectClazz)) != null) {
            //noinspection unchecked
            return parser.parseToString(object);
        }

        // 因为已经将arrayParser添加到解析器列表，所以此处补充对基本数据类型的数组的支持
        if (objectClazz.isArray()) {
            return ArrayParser.parseArrayToString(object);
        }

        return object.toString();
    }


    /**
     * 从适配器表中获取确切的与给定对象class相等的解析器，不包括其超类的
     */
    private static Parser<?> getActualParse(Class<?> clazz) {
        for (Parser<?> parser : OBJECT_PARSERS) {
            if (clazz == parser.getParseType()) {
                return parser;
            }
        }
        return null;
    }

    /**
     * 从适配器表中获取确切的与给定对象class，或是其超类的解析器
     */
    private static Parser<?> getAssignableParse(Class<?> clazz) {
        for (Parser<?> parser : OBJECT_PARSERS) {
            if (parser.getParseType().isAssignableFrom(clazz)) {
                return parser;
            }
        }
        return null;
    }
}
