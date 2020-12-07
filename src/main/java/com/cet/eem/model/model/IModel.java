package com.cet.eem.model.model;

/**
 * @ClassName : IModel
 * @Description : 模型接口
 * @Author : zhangh
 * @Date: 2020-07-22 17:05
 */
public interface IModel {
    /**
     * 获取模型Id
     *
     * @return id
     */
    Long getId();

    /**
     * 设置模型id
     *
     * @param id
     */
    void setId(Long id);

    /**
     * 获取模型名
     *
     * @return ModelLabel
     */
    String getModelLabel();
}
