package com.bbu.spring.boot.autoconfigure.beetl;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * BeetlSqlProperties
 *
 * @author weihuazhang
 * @since 16/4/1
 */
@Data
@ConfigurationProperties(prefix = "beetlsql")
public class BeetlSqlProperties {

    /**
     * 哪些Dao类可以自动注入,DAO接口所在包名
     */
    private String basePackage;

    /**
     * 是否开启debug,用来打印sql语句，参数和执行时间
     */
    private boolean debug = false;

    /**
     *  sql语句加载来源;
     *  sql文件(.md)的路径
     */
    private String sqlRoot = "/sql";

    /**
     * 通过类后缀来自动注入Dao
     */
    private String daoSuffix = "Dao";

    /**
     * DefaultNameConversion:默认java风格
     * UnderlinedNameConversion:下划线命名转换
     * UpperCaseUnderlinedNameConversion:下划线命名转换,对应的数据库全大写
     * HumpNameConversion:驼峰命名转换
     */
    private String nameConversion = "UnderlinedNameConversion";
}
