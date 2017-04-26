/*
 * Copyright (C) 2017 Baifendian Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baifendian.swordfish.webserver.config;

import com.baifendian.swordfish.webserver.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class ApplicationConfig extends WebMvcConfigurerAdapter {
  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(loginInterceptor()).addPathPatterns("/**/*").excludePathPatterns("/login");
  }

  @Bean
  public LoginInterceptor loginInterceptor() {
    return new LoginInterceptor();
  }

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**").allowedOrigins("*").allowedMethods("*");
  }

//  /**
//   * 处理 controller 的解决方法
//   *
//   * @return
//   */
//  @Bean
//  public ErrorAttributes errorAttributes() {
//    return new DefaultErrorAttributes() {
//      @Override
//      public Map<String, Object> getErrorAttributes(RequestAttributes requestAttributes, boolean includeStackTrace) {
//        Map<String, Object> errorAttributes = super.getErrorAttributes(requestAttributes, includeStackTrace);
//        // Customize the default entries in errorAttributes to suit your
//        Throwable exception = (Throwable) requestAttributes.getAttribute(DefaultErrorAttributes.class.getName() + ".ERROR", 0);
//        if (!StringUtils.isEmpty(exception.getMessage())) {
//          errorAttributes.put("message", exception.getMessage());
//        }
//        return errorAttributes;
//      }
//
//    };
//  }

  /**
   * 解决路由中, 可以包含 ., +, 等特殊字符问题
   *
   * @param configurer
   */
  @Override
  public void configureContentNegotiation(final ContentNegotiationConfigurer configurer) {
    // Turn off suffix-based content negotiation
    configurer.favorPathExtension(false);
  }
}
