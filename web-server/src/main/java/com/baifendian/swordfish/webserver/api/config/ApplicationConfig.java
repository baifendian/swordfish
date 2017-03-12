
package com.baifendian.swordfish.webserver.api.config;

import com.baifendian.swordfish.webserver.api.dto.BaseResponse;
import com.baifendian.swordfish.webserver.api.interceptor.LoginInterceptor;
//import com.baifendian.swordfish.webserver.api.service.schedule.DagService;
import com.baifendian.swordfish.rpc.MasterService.Iface;
import com.bfd.harpc.main.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by caojingwei on 16/7/26.
 */
@Configuration
public class ApplicationConfig extends WebMvcConfigurerAdapter {

    /** LOGGER */
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    /** master 的 harpc 配置 */
    private static final String MASTER_CLIENT_FILE_PATH = "classpath:master-client.properties";

    @Value("${domain}")
    private String domain;

    public String getDomain() {
        return domain;
    }

    //@Autowired
    //DagService dagSingleton;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // registry.addInterceptor(loginInterceptor()).addPathPatterns("/*")
        /* .excludePathPatterns("/login") */;
       //registry.addInterceptor(loginInterceptor()).addPathPatterns("/**/*");
    }

    @Bean
    public LoginInterceptor loginInterceptor() {
        return new LoginInterceptor();
    }

    /*
    @Bean
    public Integer initDag() {
        dagSingleton.init();
        return 0;
    }
    */

    @Bean
    BaseResponse baseResponse(){
        return new BaseResponse();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("*");
    }
}
