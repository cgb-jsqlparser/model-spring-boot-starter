package com.example.config;

import com.example.model.aop.ModelServiceResultAop;
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
@EnableFeignClients(basePackages = {"com.example.model.feign"})
public class ModelAdapterConfiguration {

    @Bean
    public ModelServiceResultAop modelServiceResultAop() {
        return new ModelServiceResultAop();
    }

}
