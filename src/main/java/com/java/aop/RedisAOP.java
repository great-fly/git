package com.java.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/*@Service
@Controller
@Repository*/
@Component  //组件 将类交给spring容器管理
@Aspect     //表示我是一个切面
public class RedisAOP {
    //公式 aop = 切入点表达式   +   通知方法
    //@Pointcut("bean(itemCatServiceImpl)")
    //@Pointcut("within(com.java.service.*)")
    //@Pointcut("execution(* com.java.service.*.*(..))")   //.* 当前包的一级子目录
    @Pointcut("execution(* com.java.service..*.*(..))")    //..* 当前包的所有的子目录
    public void pointCut(){

    }

    //如何获取目标对象的相关参数?
    //ProceedingJoinPoint is only supported for around advice

    @Before("pointCut()")
    public void before(JoinPoint joinPoint) {   //接入点
        Object target = joinPoint.getTarget();
        Object[] args = joinPoint.getArgs();

    }
}
