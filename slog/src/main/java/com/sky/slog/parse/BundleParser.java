package com.sky.slog.parse;

import android.os.Bundle;

import com.sky.slog.ParseObject;

import static com.sky.slog.LogConstant.LINE_SEPARATOR;

/**
 * [一句话描述类的作用]
 * [详述类的功能。]
 * Created by sky on 2017/5/27.
 */
public class BundleParser implements Parser<Bundle> {
    public static final BundleParser BUNDLE_PARSER = new BundleParser();

    @Override
    public Class<Bundle> getParseType() {
        return Bundle.class;
    }

    @Override
    public String parseToString(Bundle bundle) {
        StringBuilder builder = new StringBuilder(bundle.getClass().getName() + " [" + LINE_SEPARATOR);
        for (String key : bundle.keySet()) {
            builder.append(String.format("'%s' => %s " + LINE_SEPARATOR,
                    key, ParseObject.objectToString(bundle.get(key))));
        }
        builder.append("]");
        return builder.toString();
    }
}
