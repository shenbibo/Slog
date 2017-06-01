package com.sky.slog.parse;



import com.sky.slog.ParseObject;

import java.util.Collection;
import java.util.Iterator;

import static com.sky.slog.LogConstant.OBJECT_NULL_STRING;

/**
 * Collection解析器
 * <p>
 * 已迭代的方式解析集合，并且递归解析其元素对象
 * Created by sky on 2017/5/28.
 */

public class CollectionParser implements Parser<Collection> {
    public static final CollectionParser COLLECTION_PARSER = new CollectionParser();

    @Override
    public Class<Collection> getParseType() {
        return Collection.class;
    }

    @Override
    public String parseToString(Collection collection) {
        if (collection == null) {
            return OBJECT_NULL_STRING;
        }

        Iterator it = collection.iterator();
        if (!it.hasNext()) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (; ; ) {
            Object object = it.next();
            sb.append(object == collection ? "(this Collection)" : ParseObject.objectToString(object));
            if (!it.hasNext()) {
                return sb.append(']').toString();
            }
            sb.append(',').append(' ');
        }
    }
}
