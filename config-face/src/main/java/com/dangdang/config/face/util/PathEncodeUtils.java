package com.dangdang.config.face.util;

import com.google.common.base.Charsets;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public final class PathEncodeUtils {

    public static String encodePath(String path) {
        try {
            return URLEncoder.encode(path, Charsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
        }
        return path;
    }

    public static String decodePath(String path) {
        try {
            return URLDecoder.decode(path, Charsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
        }
        return path;
    }
}
