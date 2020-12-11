package com.cet.eem.model.model;

import com.cet.eem.metadata.TableInfoHelper;

/**
 * @ClassName : AbstractModelEntity
 * @Description :
 * @Author : zhangh
 * @Date: 2020-07-22 17:05
 */
public abstract class AbstractModelEntity implements IModel {

    protected Long id;

    protected String modelLabel;

    public AbstractModelEntity() {
        this.modelLabel = TableInfoHelper.getModelLabel(this.getClass());
    }


    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getModelLabel() {
        return this.modelLabel;
    }
}
