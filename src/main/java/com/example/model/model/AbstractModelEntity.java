package com.example.model.model;

import com.example.metadata.TableInfoHelper;
import lombok.Getter;
import lombok.Setter;

/**
 * @ClassName : AbstractModelEntity
 * @Description :
 * @Author : zhangh
 * @Date: 2020-07-22 17:05
 */
public abstract class AbstractModelEntity {

    @Getter
    @Setter
    protected Long id;

    @Getter
    protected String modelLabel;

    public AbstractModelEntity() {
        this.modelLabel = TableInfoHelper.getModelLabel(this.getClass());
    }
}
