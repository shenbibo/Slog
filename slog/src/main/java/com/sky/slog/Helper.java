package com.sky.slog;

import android.support.annotation.NonNull;


import com.sky.slog.parse.Parser;

import org.json.*;

import java.io.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import static com.sky.slog.LogConstant.OBJECT_NULL_STRING;

/**
 * [function]
 * [detail]
 * Created by Sky on 2017/5/25.
 */
class Helper {
    /**
     * It is used for json pretty print
     */
    private static final int JSON_INDENT = 2;

    /**
     * Android's max limit for a log entry is ~4076 bytes,
     * so 4000 bytes is used as chunk size since default charset
     * is UTF-8
     */
    private static final int CHUNK_SIZE = 4000;

    /**
     * KEY = Object.getClass().getName();
     * value = Parser.getClass();
     * */
    private static final Map<String, Class<? extends Parser>> parseObjects = new ConcurrentHashMap<>();

    static String covertJson(String json) {
        if (isEmpty(json)) {
            return "Empty/Null json content";
        }
        try {
            json = json.trim();
            if (json.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(json);
                return jsonObject.toString(JSON_INDENT);
            }
            if (json.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(json);
                return jsonArray.toString(JSON_INDENT);
            }
            return "Invalid Json";
        } catch (JSONException e) {
            return "Invalid Json";
        }
    }

    static String covertXml(String xml) {
        if (isEmpty(xml)) {
            return "Empty/Null xml content";
        }
        try {
            Source xmlInput = new StreamSource(new StringReader(xml));
            StreamResult xmlOutput = new StreamResult(new StringWriter());
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.transform(xmlInput, xmlOutput);
            return xmlOutput.getWriter().toString().replaceFirst(">", ">\n");
        } catch (TransformerException e) {
            return "Invalid xml";
        }
    }


    /**
     * 因为logcat对字数有限制4000，所以当字数大于4000时进行拆分成数组
     */
    static String[] splitString(@NonNull String string) {
        int length = string.length();
        if (length <= CHUNK_SIZE) {
            return new String[]{string};
        }

        String[] strings = new String[length / CHUNK_SIZE + 1];
        for (int i = 0, j = 0; i < length; i += CHUNK_SIZE, j++) {
            int count = Math.min(length - i, CHUNK_SIZE);
            strings[j] = string.substring(i, i + count);
        }
        return strings;
    }

    static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    static String formatMessage(String message, Object... args) {
        return args == null ? OBJECT_NULL_STRING : String.format(message, args);
    }

    static String getSimpleClassName(String name) {
        int lastIndex = name.lastIndexOf(".");
        return name.substring(lastIndex + 1);
    }

    static String getStackTraceString(Throwable t) {
        // Don't replace this with Log.getStackTraceString() - it hides
        // UnknownHostException, which is not what we want.
        StringWriter sw = new StringWriter(256);
        PrintWriter pw = new PrintWriter(sw, false);
        t.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }

    static String createThreadInfo(Thread thread){
        return "Thread.name = " + thread.getName();
    }
}
