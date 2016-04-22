package com.bbu.spring.boot.autoconfigure.beetl;

import org.beetl.sql.ext.spring4.BeetlSqlScannerConfigurer;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * BeetlConfigurationApplicationContextInitializer
 *
 * @author weihuazhang
 * @since 16/4/1
 */

@Configuration
@AutoConfigureAfter(BeetlSqlAutoConfiguration.class)
public class BeetlConfigurationApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private ConfigurableApplicationContext applicationContext;

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        Environment env = applicationContext.getEnvironment();

        String basePackage = env.getProperty("beetlsql.basePackage");
        String daoSuffix = env.getProperty("beetlsql.daoSuffix");

        addScannerConfigurerToBeanFactory(basePackage, daoSuffix);
    }

    private void addScannerConfigurerToBeanFactory(String basePackage, String daoSuffix) {

        if (basePackage == null || basePackage.length() == 0) {
            return;
        }

        Class<?> beanClass = BeetlSqlScannerConfigurer.class;
        String beanName = beanClass.getName();

        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext
                .getBeanFactory();
        if (!beanFactory.containsBean(beanName)) {
            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(beanClass);
            beanDefinitionBuilder.addPropertyValue("basePackage", basePackage);
            if (daoSuffix != null && daoSuffix.length() > 0) {
                beanDefinitionBuilder.addPropertyValue("daoSuffix", daoSuffix);
            }
            beanDefinitionBuilder.addPropertyValue("sqlManagerFactoryBeanName", "sqlManagerFactoryBean");
            beanDefinitionBuilder.addPropertyValue("beanName", beanName);
            beanDefinitionBuilder.addPropertyValue("applicationContext", applicationContext);
            beanFactory.registerBeanDefinition(beanName, beanDefinitionBuilder.getBeanDefinition());
        }
    }
}