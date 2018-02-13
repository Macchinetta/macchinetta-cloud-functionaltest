/*
 * Copyright(c) 2017 NTT Corporation.
 */
package jp.co.ntt.cloud.functionaltest.app.common;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ImportResource;

/**
 * Springbootアプリケーション構築クラス。
 * @author NTT 電電太郎
 */
@ImportResource({ "classpath*:META-INF/spring/applicationContext.xml",
        "classpath*:META-INF/spring/spring-security.xml",
        "classpath*:/META-INF/spring/spring-mvc.xml" })
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class,
        JmxAutoConfiguration.class, WebMvcAutoConfiguration.class })
public class Bootstrap extends SpringBootServletInitializer {

    /**
     * {@inheritDoc}
     */
    @Override
    protected SpringApplicationBuilder configure(
            SpringApplicationBuilder application) {
        setRegisterErrorPageFilter(false);
        return application.sources(Bootstrap.class);
    }
}
