package com.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;


/**
 * 程序入口
 *
 * @author lqs2
 */
@EnableAutoConfiguration
@ComponentScan(basePackages = {"com.example.controller", "com.example.config"})
@PropertySource(value = {"classpath:application.yml", "classpath:jdbc.properties", "classpath:objectStorage.properties"})
@MapperScan("com.example.mapper")
public class DarkmeApplication {


    /**
     * 内部启动
     * <p>
     * -XX:+PrintGCDetails -Xmx32M -Xms1M，默认4G
     * 打印详细GC日志 最大堆内存32m 初始堆内存为1m 预计一致回收 特别频繁
     * <p>
     * <p>
     * 外部启动<br>
     */
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(DarkmeApplication.class);
        application.setBannerMode(Banner.Mode.OFF);
        application.run(args);
//        SpringApplication.run(FileTransferStationApplication.class, args);
    }

    @Bean
    @Scope(value = "singleton")//默认也是如此
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

//    @Bean
//    public TomcatServletWebServerFactory tomcatServletWebServerFactory(@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") Connector connector){
//        TomcatServletWebServerFactory tomcat=new TomcatServletWebServerFactory(){
//            @Override
//            protected void postProcessContext(Context context) {
//                SecurityConstraint securityConstraint=new SecurityConstraint();
//                securityConstraint.setUserConstraint("CONFIDENTIAL");
//                SecurityCollection collection=new SecurityCollection();
//                collection.addPattern("/*");
//                securityConstraint.addCollection(collection);
//                context.addConstraint(securityConstraint);
//            }
//        };
//        tomcat.addAdditionalTomcatConnectors(connector);
//        return tomcat;
//    }

//    @Bean
//    public  UndertowServletWebServerFactory undertowServletWebServerFactory() {
//        UndertowServletWebServerFactory factory = new UndertowServletWebServerFactory();
//        factory.addBuilderCustomizers(
//
//                builder -> builder.setServerOption(UndertowOptions.ENABLE_HTTP2, true)
//                        .setServerOption(UndertowOptions.HTTP2_SETTINGS_ENABLE_PUSH,true).addHttpListener(80, "0.0.0.0"));
//        return factory;
//    }

//    @Bean
//    public Connector httpConnector() {
//        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
//        connector.setScheme("http");
//        //Connector监听的http的端口号
//        connector.setPort(80);
//        connector.setSecure(false);
//        //监听到http的端口号后转向到的https的端口号
//        connector.setRedirectPort(443);
//        return connector;
//    }
}