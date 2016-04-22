package com.bbu.spring.boot.autoconfigure.beetl;

import lombok.extern.slf4j.Slf4j;
import org.beetl.sql.core.ClasspathLoader;
import org.beetl.sql.core.Interceptor;
import org.beetl.sql.core.SQLLoader;
import org.beetl.sql.core.UpperCaseUnderlinedNameConversion;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.ext.DebugInterceptor;
import org.beetl.sql.ext.spring4.BeetlSqlDataSource;
import org.beetl.sql.ext.spring4.SqlManagerFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

import javax.sql.DataSource;

@Configuration
@ConditionalOnBean(DataSource.class)
@EnableConfigurationProperties(BeetlSqlProperties.class)
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
@Slf4j
public class BeetlSqlAutoConfiguration implements TransactionManagementConfigurer {

    @Autowired
    private BeetlSqlProperties properties;

    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    DataSource dataSource;

    @Bean
    @ConditionalOnMissingBean
    public BeetlSqlDataSource beetlSqlDataSource() {
        BeetlSqlDataSource beetlSqlDataSource = new BeetlSqlDataSource();
        beetlSqlDataSource.setMasterSource(dataSource);
        return beetlSqlDataSource;
    }

    @Bean
    @ConditionalOnMissingBean
    public ClasspathLoader classpathLoader() {
        ClasspathLoader classpathLoader = new ClasspathLoader();
        if (properties.getSqlRoot() != null) {
            classpathLoader.setSqlRoot(properties.getSqlRoot());
        }
        return classpathLoader;
    }

    @Bean()
    @ConditionalOnMissingBean
    public SqlManagerFactoryBean sqlManagerFactoryBean(BeetlSqlDataSource beetlSqlDataSource, SQLLoader sqlLoader) {
        SqlManagerFactoryBean factory = new SqlManagerFactoryBean();
        factory.setCs(beetlSqlDataSource);

        MySqlStyle dbStyle = new MySqlStyle();
        factory.setDbStyle(dbStyle);

        factory.setSqlLoader(sqlLoader);
        NameConversion nameConversion = getNameConversion(properties.getNameConversion());
        factory.setNc(nameConversion);

        if (properties.isDebug()) {
            Interceptor[] interceptors = new Interceptor[]{new DebugInterceptor()};
            factory.setInterceptors(interceptors);
        }
        return factory;
    }

    @Bean
    @Override
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        return new DataSourceTransactionManager(dataSource);
    }

    private NameConversion getNameConversion(String nameConversionStr) {
        NameConversion nameConversion = null;
        String packageName = NameConversion.class.getPackage().getName();
        try {
            Class nameConversionClass = Class.forName(packageName + "." + nameConversionStr);
            nameConversion = (NameConversion)nameConversionClass.newInstance();
        } catch (Exception e) {
            log.error("beetlsql.nameConversion不存在, 使用UnderlinedNameConversion");
        }
        if (nameConversion == null) {
            nameConversion = new UnderlinedNameConversion();
        }
        return nameConversion;
    }
}

