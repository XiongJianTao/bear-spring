package com.bear.framework.webmvc.servlet;

import java.io.File;
import java.util.Locale;

/**
 * @author bear
 * @version 1.0
 * @className BearViewResolver
 * @description TODO
 * @date 2020/8/30 0:22
 */
public class BearViewResolver {

    private final String DEFAULT_TEMPLATE_SUFFX = ".html";

    private File templateRootDir;

    public BearViewResolver(String templateRoot) {
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        templateRootDir = new File(templateRootPath);
    }

    public BearView resolveViewName(String viewName, Locale locale) throws Exception {
        if (viewName == null || "".equals(viewName.trim())) {
            return null;
        }

        viewName = viewName.endsWith(DEFAULT_TEMPLATE_SUFFX) ? viewName : (viewName + DEFAULT_TEMPLATE_SUFFX);
        File file = new File((templateRootDir.getPath() + "/" + viewName).replaceAll("/+", "/"));
        return new BearView(file);
    }

    public File getTemplateDir() {
        return templateRootDir;
    }
}
