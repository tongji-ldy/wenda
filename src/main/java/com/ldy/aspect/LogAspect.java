package com.ldy.aspect;

import org.aspectj.lang.JoinPoint;
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

    @Before("execution(* com.ldy.controller.*.*(..))")
    public void beforeMethod(JoinPoint joinPoint) {
        StringBuilder sb = new StringBuilder();
        for (Object arg : joinPoint.getArgs()) {
            if (arg != null) {
                sb.append("arg:").append(arg.toString()).append("|");
            }
        }
        logger.info("before method" + sb.toString());
    }


    @After("execution(* com.ldy.controller.*.*(..))")
    public void afterMethod() {
        logger.info("after method" + new Date());
    }
}
