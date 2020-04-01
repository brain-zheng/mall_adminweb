package com.mall.adminweb.config;

import com.mall.adminweb.common.Constants;
import com.mall.adminweb.interceptor.AdminLoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author zheng haijain
 * @createTime 2020-03-21 12:31
 * @description
 */
@Configuration
public class MallAdminWebMvcConfigurer implements WebMvcConfigurer {

    @Autowired
    public AdminLoginInterceptor adminLoginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 添加一个拦截器，拦截以/admin为前缀的url路径（后台登陆拦截）
        registry.addInterceptor(adminLoginInterceptor)
                .addPathPatterns("/admin/**")
                .excludePathPatterns("/admin/login")       // 排除登录页面
                .excludePathPatterns("/admin/dist/**")      // 排除静态资源
                .excludePathPatterns("/admin/plugins/**");
    }

    // 使用自定义静态资源映射目录
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/upload/**").addResourceLocations("file:" + Constants.FILE_UPLOAD_DIC);
    }
}
