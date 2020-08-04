package com.bear.mvcframework.v1.servlet;

import com.bear.mvcframework.v1.annotation.BearAutowired;
import com.bear.mvcframework.v1.annotation.BearController;
import com.bear.mvcframework.v1.annotation.BearRequestMapping;
import com.bear.mvcframework.v1.annotation.BearService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class BearDispatcherServlet extends HttpServlet {

    private final Properties contextConfig = new Properties();

    // 保存所有扫描到的类全限定名
    private final List<String> classNames = new ArrayList<>();

    // IOC容器
    private final Map<String, Object> IOC = new HashMap<>();

    //
    private final List<HandlerMapping<?>> HandlerMapping = new ArrayList<>();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doDispatch(req, resp);
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Map<String, String[]> parameterMap = req.getParameterMap();

        HandlerMapping handlerMapping = getHandler(req);
        if (handlerMapping == null) {
            resp.getWriter().write("404 Not Found!!!");
            return;
        }
        Method method = null;

        // 获取方法的形参列表
        Class<?>[] parameterTypes = handlerMapping.getParamTypes();

        // 形参对应的值数组
        Object[] paramValues = new Object[parameterTypes.length];

        for (Map.Entry<String, String[]> param : parameterMap.entrySet()) {
            String value = Arrays.toString(param.getValue())
                    .replaceAll("\\[]", "")
                    .replaceAll("\\s", ",");
            if (!handlerMapping.getParamIndexMapping().containsKey(param.getKey())) {
                continue;
            }
            int index = (int) handlerMapping.getParamIndexMapping().get(param.getKey());
            paramValues[index] = value;
        }
        // 获取方法的类名称
        String beanName = toLowerFirstCase(method.getDeclaringClass().getSimpleName());
        try {
            Class<?> returnType = method.getReturnType();
            resp.getWriter().write(returnType.cast(method.invoke(this.IOC.get(beanName), paramValues)).toString());
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private HandlerMapping getHandler(HttpServletRequest req) {
        if (HandlerMapping.size() == 0) {
            return null;
        }
        String uri = req.getRequestURI();
        for (HandlerMapping handlerMapping : this.HandlerMapping) {
            if (handlerMapping.getUrl().equals(uri)) {
                return handlerMapping;
            }
        }
        return null;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {

        // 1.加载配置文件
        doLoadConfig(config.getInitParameter("contextConfigLocation"));

        // 2.扫描相关的类
        doScanner(contextConfig.getProperty("scanPackage"));

        // 3.初始化扫描的类，并且将他们放入到IOC中
        try {
            doInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 4.完成依赖注入
        doAutoWired();

        // 5.初始化HandlerMapping
        try {
            initHandlerMapping();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initHandlerMapping() throws Exception {
        if (this.IOC.isEmpty()) {
            return;
        }

        for (Map.Entry<String, Object> entry : this.IOC.entrySet()) {
            Class<?> clazz = entry.getValue().getClass();
            if (!clazz.isAnnotationPresent(BearController.class)) {
                continue;
            }
            String baseUrl = "";
            if (clazz.isAnnotationPresent(BearRequestMapping.class)) {
                baseUrl = clazz.getAnnotation(BearRequestMapping.class).value();
            }
            // 获取所有public方法
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                if (!method.isAnnotationPresent(BearRequestMapping.class)) {
                    continue;
                }
                BearRequestMapping bearRequestMapping = method.getAnnotation(BearRequestMapping.class);
                String url = ("/" + baseUrl + "/" + bearRequestMapping.value()).replaceAll("/+", "/");

                this.HandlerMapping.add(new HandlerMapping(url, entry.getValue(), method));
            }
        }
    }

    private void doAutoWired() {
        for (Map.Entry<String, Object> entry : this.IOC.entrySet()) {
            Field[] fields = entry.getValue().getClass().getDeclaredFields();
            for (Field field : fields) {
                BearAutowired bearAutowired = field.getAnnotation(BearAutowired.class);

                if (null == bearAutowired) {
                    continue;
                }

                String beanName = bearAutowired.value().trim();
                if ("".equals(beanName)) {
                    beanName = field.getType().getName();
                }

                field.setAccessible(true);

                try {
                    field.set(entry.getValue(), this.IOC.get(beanName));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void doInstance() throws Exception {
        if (!(this.classNames.size() > 0)) {
            return;
        }

        try {
            // 根据类全限定名，通过反射生成实例
            for (String className : this.classNames) {
                Class<?> clazz = Class.forName(className);
                Object instance = clazz.newInstance();
                if (clazz.isAnnotationPresent(BearController.class)) {
                    String beanName = toLowerFirstCase(clazz.getSimpleName());
                    this.IOC.put(beanName, instance);
                } else if (clazz.isAnnotationPresent(BearService.class)) {
                    BearService bearService = clazz.getAnnotation(BearService.class);
                    String beanName = bearService.value();
                    if ("".equals(beanName.trim())) {
                        beanName = toLowerFirstCase(beanName);
                    }

                    this.IOC.put(beanName, instance);
                    // service接口的实例
                    for (Class<?> clazzInterface : clazz.getInterfaces()) {
                        if (this.IOC.containsKey(clazzInterface.getName())) {
                            throw new Exception("The \"" + clazzInterface.getName() + "\" is exists");
                        }
                        this.IOC.put(clazzInterface.getName(), instance);
                    }
                }
            }
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }

    }

    private String toLowerFirstCase(String simpleName) {
        if (null == simpleName || "".equals(simpleName)) {
            return "";
        }
        char[] chars = simpleName.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }

    private void doLoadConfig(String contextConfigLocation) {
        InputStream is = null;
        try {
            is = this.getClass().getClassLoader().getResourceAsStream(contextConfigLocation);
            contextConfig.load(is);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void doScanner(String scanPackage) {
        URL url = this.getClass().getClassLoader().getResource(scanPackage.replaceAll("\\.", "/"));
        File classDir = new File(url.getFile());

        for (File file : classDir.listFiles()) {
            if (file.isDirectory()) {
                doScanner(scanPackage + "." + file.getName());
            } else if (file.getName().endsWith(".class")) {
                this.classNames.add(scanPackage + "." + file.getName().replace(".class", ""));
            }
        }
    }


    private Object convert(Class<?> type, String value) {
        if (Integer.class == type) {
            return Integer.valueOf(value);
        } else if (String.class == type) {
            return value.replaceAll("\\[\\]", "")
                    .replaceAll("\\s", ",");
        }
        return value;
    }
}
