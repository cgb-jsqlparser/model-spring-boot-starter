package com.cet.eem.config;

import com.cet.eem.annotation.EnableModel;
import com.cet.eem.common.util.JsonUtil;
import com.cet.eem.model.aop.ModelServiceResultAop;
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
public class ModelAdapterConfiguration {

    @Bean
    public ObjectMapper objectMapper() {
        return JsonUtil.getMapper();
    }

}
