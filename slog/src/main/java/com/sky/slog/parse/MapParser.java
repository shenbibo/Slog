package com.sky.slog.parse;


import com.sky.slog.ParseObject;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import static com.sky.slog.LogConstant.OBJECT_NULL_STRING;

/**
 * [一句话描述类的作用]
 * [详述类的功能。]
 * Created by sky on 2017/5/28.
 */

public class MapParser implements Parser<Map> {
    public static final MapParser MAP_PARSER = new MapParser();

    @Override
    public Class<Map> getParseType() {
        return Map.class;
    }

    @Override
    public String parseToString(Map map) {
        if (map == null) {
            return OBJECT_NULL_STRING;
        }

        Iterator i = map.entrySet().iterator();
        if (!i.hasNext()) {
            return "{}";
        }

        StringBuilder sb = new StringBuilder();
        sb.append('{');
        for (; ; ) {
            Entry entry = (Entry) i.next();
            Object key = entry.getKey();
            Object value = entry.getValue();
            sb.append(key == map ? "(this Map)" : ParseObject.objectToString(key));
            sb.append('=');
            sb.append(value == map ? "(this Map)" : ParseObject.objectToString(value));
            if (!i.hasNext()) {
                return sb.append('}').toString();
            }
            sb.append(',').append(' ');
        }
    }
}
