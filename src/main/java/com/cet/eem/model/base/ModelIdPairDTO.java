package com.cet.eem.model.base;

/**
 * 模型键
 * @author CKai
 */
public class ModelIdPairDTO {
    public Long id;
    public String modelLabel;

    public ModelIdPairDTO(){}

    public ModelIdPairDTO(Long id, String modelLabel){
        this.id = id;
        this.modelLabel = modelLabel;
    }

    public String getModelLabel() {
        return modelLabel;
    }

    public void setModelLabel(String modelLabel) {
        this.modelLabel = modelLabel;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "ModelIdPairDTO{" +
                "modelLabel='" + modelLabel + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
