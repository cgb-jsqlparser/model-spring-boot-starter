package com.cet.eem.model.feign;


import com.cet.eem.common.feign.ModelDataService;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * 模型服务的数据接口
 * 避免扫描到其他Feign接口
 *
 * @author CKai
 */
@FeignClient(contextId = "model-service-adapter", value = "model-service", url = "${cet.eem.url.model-service:'model1'}")
public interface ModelDataServiceSubstitution extends ModelDataService {

}
