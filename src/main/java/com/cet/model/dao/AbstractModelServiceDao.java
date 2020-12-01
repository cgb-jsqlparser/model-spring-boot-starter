package com.cet.model.dao;

import com.cet.eem.common.feign.ModelDataService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @ClassName : AbstractModelServiceDao
 * @Description : 模型服务访问通用基类
 * @Author : zhangh
 * @Date: 2020-07-21 18:17
 */
public abstract class AbstractModelServiceDao {

    protected ModelDataService modelDataService;

    @Autowired
    public void setModelDataService(ModelDataService modelDataService) {
        this.modelDataService = modelDataService;
    }

}