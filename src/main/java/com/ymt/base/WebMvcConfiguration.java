package com.ymt.base;


import com.ymt.interceptor.TokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**
 * @author ymt
 */
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Autowired
    TokenInterceptor tokenInterceptor;



    /**
     * 注册拦截器
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        InterceptorRegistration interceptorToken = registry.addInterceptor(tokenInterceptor).addPathPatterns("/**");
        // 主页
        interceptorToken.excludePathPatterns("/login");
        interceptorToken.excludePathPatterns("/showMedia");
        interceptorToken.excludePathPatterns("/csrf");
        interceptorToken.excludePathPatterns("/test/*");
        interceptorToken.excludePathPatterns("/test");
        interceptorToken.excludePathPatterns("/");
        interceptorToken.excludePathPatterns("/error");
        interceptorToken.excludePathPatterns("/getBase");
        interceptorToken.excludePathPatterns("/getBaseImage");
        interceptorToken.excludePathPatterns("/getAllImage");
        interceptorToken.excludePathPatterns("/del");
        interceptorToken.excludePathPatterns("/ai");
        interceptorToken.excludePathPatterns("/talk");
        interceptorToken.excludePathPatterns("/limit/*");
        interceptorToken.excludePathPatterns("/swagger-resources/**", "/webjars/**", "/v2/**", "/swagger-ui.html/**");
    }



}
