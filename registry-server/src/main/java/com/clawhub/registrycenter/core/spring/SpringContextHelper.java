package com.clawhub.registrycenter.core.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 获取容器中的ApplicationContext
 */
@Component
public class SpringContextHelper implements ApplicationContextAware {
    /**
     * The constant appCtx.
     */
    private static ApplicationContext appCtx;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        SpringContextHelper.setAppCtx(applicationContext);
    }

    /**
     * 此方法可以把ApplicationContext对象inject到当前类中作为一个静态成员变量。
     *
     * @param applicationContext the application context
     */
    private static void setAppCtx(ApplicationContext applicationContext) {
        appCtx = applicationContext;
    }

    /**
     * 获取ApplicationContext
     *
     * @return the application context
     */
    public static ApplicationContext getApplicationContext() {
        return appCtx;
    }

    /**
     * 这是一个便利的方法，帮助我们快速得到一个BEAN
     *
     * @param beanName the bean name
     * @return the bean
     */
    public static Object getBean(String beanName) {
        return appCtx.getBean(beanName);
    }

    /**
     * 快速获取Spring容器中的bean
     *
     * @param <T>   the type parameter
     * @param clazz the clazz
     * @return the bean
     */
    public <T> T getBean(Class<T> clazz) {
        return appCtx.getBean(clazz);
    }
}
