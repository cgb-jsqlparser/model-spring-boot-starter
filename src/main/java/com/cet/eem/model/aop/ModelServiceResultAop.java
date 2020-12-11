package com.cet.eem.model.aop;


import com.cet.eem.common.util.JsonUtil;
import com.cet.eem.common.model.Result;
import com.cet.eem.exceptions.ModelServiceCallException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * @ClassName : ModelServiceResultAop
 * @Description : 拦截模型服务feign接口的结果 对于结果不成功的，抛出异常
 * @Author : zhangh
 * @Date: 2020-07-22 16:16
 */
@Aspect
@Slf4j
public class ModelServiceResultAop {

    @Pointcut("execution(* com.cet.eem.model.feign.ModelDataService.*(..))")
    public void resultHandler() {

    }

    @AfterReturning(value = "resultHandler()", returning = "result")
    public void doAfter(JoinPoint joinPoint, Object result) {
        if (result instanceof Result) {
            Result<?> modelServiceResult = (Result<?>) result;
            if (modelServiceResult.getCode() != 0) {
                String name = joinPoint.getSignature().getName();
                Object[] args = joinPoint.getArgs();
                log.error("model service feign interface:{},call failure,param:{},result:{}", name, JsonUtil.toJSONString(args), JsonUtil.toJSONString(result));
                throw new ModelServiceCallException("model service feign interface call failure");
            }
        }
    }
}
