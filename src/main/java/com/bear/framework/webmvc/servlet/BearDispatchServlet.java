package com.bear.framework.webmvc.servlet;

import com.bear.framework.annotation.BearController;
import com.bear.framework.annotation.BearRequestMapping;
import com.bear.framework.context.BearApplicationContext;
import com.sun.istack.internal.Nullable;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author bear
 * @version 1.0
 * @className BearDispatchServlet
 * @description TODO
 * @date 2020/8/29 22:57
 */
@Slf4j
public class BearDispatchServlet extends HttpServlet {

    private final String CONTEXT_CONFIG_LOCATION = "contextConfigLocation";

    private BearApplicationContext applicationContext;

    @Nullable
    private List<BearHandlerMapping> handlerMappings = new ArrayList<>();

    private Map<BearHandlerMapping, BearHandlerAdapter> HandlerAdapters = new ConcurrentHashMap<>();

    private List<BearViewResolver> viewResolvers = new ArrayList<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            this.doDispacth(req, resp);
        } catch (Exception e) {
            resp.getWriter().write("500 Exception,Detail:\r\n" + Arrays.toString(e.getStackTrace()).replaceAll("\\[|\\]", "").replaceAll(",\\s", "\r\n"));
            e.printStackTrace();
        }
    }

    private void doDispacth(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        // 1、通过从request中拿到URL，去匹配一个handlerMapping
        BearHandlerMapping handler = getHandler(req);

        if (handler == null) {
            // 404
            processDispatchResult(req, resp, new BearModelAndView("404"));
            return;
        }

        // 2、准备好调用前的参数
        BearHandlerAdapter ha = getHandlerAdapter(handler);

        // 3、真正的调用方法,返回modelAndView 存储了要传到页面上的值和模板名称
        BearModelAndView mv = ha.handle(req, resp, handler);

        // 这一步才是真正的输出
        processDispatchResult(req, resp, mv);
    }

    private void processDispatchResult(HttpServletRequest req, HttpServletResponse resp, BearModelAndView mv) throws Exception {
        // 把ModelAndView变成一个HTML、OutputStream、json、freemark、veolcity
        if (mv == null) {
            return;
        }

        // 如果ModelAndView不为null
        if (this.viewResolvers.isEmpty()) {
            for (BearViewResolver viewResolver : this.viewResolvers) {
                BearView view = viewResolver.resolveViewName(mv.getView(), Locale.CHINESE);
                view.render(mv.getModel(), req, resp);
            }
        }
    }

    private BearHandlerAdapter getHandlerAdapter(BearHandlerMapping handler) {
        if (this.HandlerAdapters.isEmpty()) {
            return null;
        }
        BearHandlerAdapter ha = this.HandlerAdapters.get(handler);

        return null;
    }

    private BearHandlerMapping getHandler(HttpServletRequest req) {
        if (handlerMappings.isEmpty()) {
            return null;
        }

        String uri = req.getRequestURI();
        String contextPath = req.getContextPath();
        String url = uri.replace(contextPath, "").replaceAll("/+", "/");

        for (BearHandlerMapping handlerMapping : this.handlerMappings) {
            Matcher matcher = handlerMapping.pattern.matcher(url);

            if (!matcher.matches()) {
                continue;
            }

            return handlerMapping;
        }
        return null;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        String localtions = config.getInitParameter(CONTEXT_CONFIG_LOCATION);
        // 初始化ApplicationContext
        applicationContext = new BearApplicationContext(localtions);
        // 初始化SpringMVC 九大组件
        initStrategies(applicationContext);
    }

    protected void initStrategies(BearApplicationContext context) {
        // 初始化多文件上传组件
        initMultipartResolver(context);

        // 本地语言初始化
        initLocaleResolver(context);

        //初始化模板处理器
        initThemeResolver(context);

        // 初始化映射关系
        initHandlerMappings(context);

        // 处理器适配器
        initHandlerAdapters(context);

        // 初始化异常拦截器
        initHandlerExceptionResolvers(context);

        // 初始化师徒预处理器
        initRequestToViewNameTranslator(context);

        // 初始化师徒处理器
        initViewResolvers(context);

        //
        initFlashMapManager(context);
    }

    private void initFlashMapManager(BearApplicationContext context) {

    }

    private void initViewResolvers(BearApplicationContext context) {
        // 拿到模板的存放目录
        String templateRoot = context.getConfig().getProperty("templateRoot");
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();

        File templateRootDir = new File(templateRootPath);
        for (String template : templateRootDir.list()) {
            this.viewResolvers.add(new BearViewResolver(templateRoot));
        }
    }

    private void initRequestToViewNameTranslator(BearApplicationContext context) {

    }

    private void initHandlerExceptionResolvers(BearApplicationContext context) {

    }

    private void initHandlerAdapters(BearApplicationContext context) {
        // 把一个request请求变成一个handler，参数都是字符串的，自动配到handler中的形参

        for (BearHandlerMapping handlerMapping : this.handlerMappings) {
            this.HandlerAdapters.put(handlerMapping, new BearHandlerAdapter());
        }
    }

    private void initHandlerMappings(BearApplicationContext context) {
        String[] beanDefinitionNames = context.getBeanDefinitionNames();
        try {
            for (String beanDefinitionName : beanDefinitionNames) {
                Object controller = context.getBean(beanDefinitionName);

                Class<?> clazz = controller.getClass();
                if (!clazz.isAnnotationPresent(BearController.class)) {
                    continue;
                }

                String baseUrl = "";

                if (clazz.isAnnotationPresent(BearRequestMapping.class)) {
                    BearRequestMapping requestMapping = clazz.getAnnotation(BearRequestMapping.class);
                    baseUrl = requestMapping.value();
                }

                // 获取method的处理
                Method[] methods = clazz.getMethods();
                for (Method method : methods) {
                    if (!method.isAnnotationPresent(BearRequestMapping.class)) {
                        continue;
                    }
                    BearRequestMapping requestMapping = method.getAnnotation(BearRequestMapping.class);
                    String regex = (baseUrl + "/" + requestMapping.value()).replaceAll("\\*", ".*").replaceAll("/+", "/");
                    Pattern pattern = Pattern.compile(regex);
                    handlerMappings.add(new BearHandlerMapping(pattern, controller, method));
                    log.info("Mapped " + regex + "," + method);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initThemeResolver(BearApplicationContext context) {
    }

    private void initLocaleResolver(BearApplicationContext context) {
    }

    private void initMultipartResolver(BearApplicationContext context) {
    }
}
