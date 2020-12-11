package com.cet.eem.config;

import com.cet.eem.common.service.RedisService;
import com.cet.eem.model.aop.ModelServiceResultAop;
import com.cet.eem.model.feign.ModelDataService;
import com.cet.eem.model.tool.ModelServiceUtils;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName : ModelFeignConfig
 * @Description :
 * @Author : zhangh
 * @Date: 2020-12-11 16:21
 */
@Configuration
@EnableFeignClients(basePackages = {"com.cet.eem.model.feign"})
public class ModelFeignConfiguration {

    /**
     * start model feign result aop
     *
     * @return
     */
    @Bean
    public ModelServiceResultAop modelServiceResultAop() {
        return new ModelServiceResultAop();
    }

    /**
     * 工具类托管
     *
     * @param modelService
     * @param redisService
     * @return
     */
    @Bean
    public ModelServiceUtils modelServiceUtil(ModelDataService modelService, RedisService redisService) {
        return new ModelServiceUtils(modelService, redisService);
    }
}
