package com.bear.mvcframework.v1.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class BearDispatcherServlet extends HttpServlet {

    private Map<String, Object> mapping = new ConcurrentHashMap<>();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doDispatch(req, resp);
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) {
        String uri = req.getRequestURI();
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        InputStream is;
        try {
            String contextConfigLoader = config.getInitParameter("contextConfigLoader");
            is = this.getClass().getClassLoader().getResourceAsStream(contextConfigLoader);
            Properties properties = new Properties();
            properties.load(is);
            String scanPackage = properties.getProperty("scanPackage");
            doScanner(scanPackage);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void doScanner(String scanPackage) {
        URL url = this.getClass().getClassLoader().getResource(scanPackage.replaceAll("\\.", "/"));
        File file = new File(url.getFile());

        for (File classDir : file.listFiles()) {
            if (classDir.isDirectory()) {
                doScanner(scanPackage + "." + file.getName());
            } else if (classDir.getName().endsWith(".class")) {
                this.mapping.put(file.getName().replace(".class", ""), null);
            }
        }
    }
}
