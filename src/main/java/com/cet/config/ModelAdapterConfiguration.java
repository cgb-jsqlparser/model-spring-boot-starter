package com.cet.config;

import com.cet.annotation.EnableModel;
import com.cet.common.util.JsonUtil;
import com.cet.model.aop.ModelServiceResultAop;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName : ModelAdapterConfiguration
 * @Description :
 * @Author : zhangh
 * @Date: 2020-10-25 20:10
 */
@Configuration
@ConditionalOnClass(EnableModel.class)
@EnableFeignClients(basePackages = {"com.cet.model.feign"})
public class ModelAdapterConfiguration {

    @Bean
    public ModelServiceResultAop modelServiceResultAop() {
        return new ModelServiceResultAop();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return JsonUtil.getMapper();
    }

}
