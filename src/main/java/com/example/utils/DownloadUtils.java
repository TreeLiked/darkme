package com.example.utils;

import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class DownloadUtils {
    public static String getName(String agent, String filename) throws UnsupportedEncodingException {
        if (agent.contains("MSIE")) {
            // IE浏览器
            filename = URLEncoder.encode(filename, "utf-8");
            filename = filename.replace("+", " ");
        } else if (agent.contains("Firefox")) {
            // 火狐浏览器
            BASE64Encoder base64Encoder = new BASE64Encoder();
            filename = "=?utf-8?B?" + base64Encoder.encode(filename.getBytes("utf-8")) + "?=";
        } else {
            // 其它浏览器
            filename = URLEncoder.encode(filename, "utf-8");
        }
        return filename;
    }

    /**
     * 其中encoded_text是经过RFC 3986的“百分号URL编码”规则处理过的文件名
     * </pre>
     *
     * @param response
     * @param filename
     * @return
     */
    public static void setFileDownloadHeader(HttpServletResponse response, String filename) {
        String headerValue = "attachment;";
        headerValue += " filename=\"" + encodeURIComponent(filename) + "\";";
        headerValue += " filename*=utf-8''" + encodeURIComponent(filename);
        response.setHeader("Content-Disposition", headerValue);
    }

    /**
     * <pre>
     * 符合 RFC 3986 标准的“百分号URL编码”
     * 在这个方法里，空格会被编码成%20，而不是+
     * 和浏览器的encodeURIComponent行为一致
     * </pre>
     * @param value
     * @return
     */
    public static String encodeURIComponent(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8").replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }
}
