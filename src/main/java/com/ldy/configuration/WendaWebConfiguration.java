package com.ldy.configuration;

import com.ldy.interceptor.LoginRequiredInterceptor;
import com.ldy.interceptor.PassportInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * 注册拦截器
 */
@Component
public class WendaWebConfiguration extends WebMvcConfigurerAdapter {
    private final PassportInterceptor passportInterceptor;

    private final LoginRequiredInterceptor loginRequiredInterceptor;

    @Autowired
    public WendaWebConfiguration(LoginRequiredInterceptor loginRequiredInterceptor, PassportInterceptor passportInterceptor) {
        this.loginRequiredInterceptor = loginRequiredInterceptor;
        this.passportInterceptor = passportInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(passportInterceptor);
        registry.addInterceptor(loginRequiredInterceptor).addPathPatterns("/user/**");//这个拦截器必须在后面，因为需要上一个拦截器的hostholder变量
        super.addInterceptors(registry);
    }
}
