package com.bear.framework.beans.support;

import com.bear.framework.beans.config.BearBeanDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author bear
 * @version 1.0
 * @className BearBeanDefinitionReader
 * @description TODO
 * @date 2020/8/26 17:29
 */
public class BearBeanDefinitionReader {

    private List<String> registyBeanClasses = new ArrayList<String>();

    private final Properties config = new Properties();

    // 固定配置文件中的key,相当于xml中的规范
    private final static String SCAN_PACKAGE = "scanPackage";

    public BearBeanDefinitionReader(String... locations) {
        // 通过URL定位找到其所对应的文件，然后转换为文件流
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(locations[0].replace("classpath:", ""));
        try {
            config.load(is);
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

        doScanner(config.getProperty(SCAN_PACKAGE));
    }

    private void doScanner(String scanPackage) {
        URL url = this.getClass().getClassLoader().getResource("/" + scanPackage.replaceAll("\\.", "/"));
        File classPath = new File(url.getFile());
        for (File file : classPath.listFiles()) {
            if (file.isDirectory()) {
                doScanner(scanPackage + "." + file.getName());
            } else {
                if (!file.getName().endsWith(".class")) {
                    continue;
                }
                String className = scanPackage + "." + file.getName().replaceAll(".class", "");
                this.registyBeanClasses.add(className);
            }
        }

    }

    public Properties getConfig() {
        return this.config;
    }

    /**
     * 把配置文件中扫描到的所有的配置信息转换为BearBeanDefinition对象， 以便于之后IOC容器操作
     *
     * @return
     */
    public List<BearBeanDefinition> loadBeanDefinitions() {
        List<BearBeanDefinition> result = new ArrayList<>();
        for (String className : registyBeanClasses) {
            BearBeanDefinition beanDefinition = doCreateBeanDefinition(className);
            if (beanDefinition == null) {
                continue;
            }
            result.add(beanDefinition);
        }
        return result;
    }

    /**
     * 把每一个配置信息解析成一个BeanDefinition
     *
     * @param className 类名称
     * @return BeanDefinition
     */
    private BearBeanDefinition doCreateBeanDefinition(String className) {
        try {
            Class<?> beanClass = Class.forName(className);
            // 有可能是一个接口，用他的实现类作为beanClassName
            if (!beanClass.isInterface()) {
                BearBeanDefinition beanDefinition = new BearBeanDefinition();
                beanDefinition.setBeanClassName(className);
                beanDefinition.setFactoryBeanName(beanClass.getSimpleName());
                return beanDefinition;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}
