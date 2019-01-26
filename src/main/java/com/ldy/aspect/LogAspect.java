package com.ldy.aspect;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * AOP实例
 */
@Aspect
@Component//与@service类似
public class LogAspect {
    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);

    @Before("execution(* com.ldy.controller.IndexController.*(..))")//第一个*表示返回值
    public void beforeMethod() {
        logger.info("before method" + new Date());
    }

    @After("execution(* com.ldy.controller.IndexController.*(..))")
    public void afterMethod() {
        logger.info("after method" + new Date());
    }
}
