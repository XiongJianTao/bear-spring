package com.bear.framework.webmvc.servlet;

import com.sun.istack.internal.Nullable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author bear
 * @version 1.0
 * @className BearView
 * @description TODO
 * @date 2020/8/30 0:24
 */
public class BearView {

    public final String DEFAULT_CONTENT_TYPE = "text/html;charset=utf-8";

    private File viewFile;

    public BearView(File viewFile) {
        this.viewFile = viewFile;
    }

    void render(@Nullable Map<String, ?> model, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        StringBuffer sb = new StringBuffer();

        RandomAccessFile ra = new RandomAccessFile(this.viewFile, "r");

        String line = null;
        while ((line = ra.readLine()) != null) {
            line = new String(line.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
            Pattern pattern = Pattern.compile("￥\\{[^\\}]+\\}", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(line);
            while (matcher.find()) {
                String paramName = matcher.group();
                paramName = paramName.replaceAll("$\\{|\\}", "");
                Object paramValue = model.get(paramName);
                if (paramName == null) {
                    continue;
                }
                line = matcher.replaceAll(paramValue.toString());
                matcher = pattern.matcher(line);
            }
            sb.append(line);
        }

        response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
        response.getWriter().write(sb.toString());
    }
}
