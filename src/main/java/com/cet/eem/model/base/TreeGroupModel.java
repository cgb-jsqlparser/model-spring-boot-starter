package com.cet.eem.model.base;

import java.util.List;

/**
 * 树的组模型结构
 * @author CKai
 */
public class TreeGroupModel {
    private String name;
    private List<String> models;

    public String getName() {
        return name;
    }

    public List<String> getModels() {
        return models;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setModels(List<String> models) {
        this.models = models;
    }

    @Override
    public String toString() {
        return "TreeGroupModel{" +
                "name='" + name + '\'' +
                ", models=" + models +
                '}';
    }
}
