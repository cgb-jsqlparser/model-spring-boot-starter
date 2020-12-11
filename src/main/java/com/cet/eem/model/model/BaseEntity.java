package com.cet.eem.model.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 基础实体
 *
 * @author zhangzhuang
 * @date 2020/11/26
 */
@Getter
@Setter
public class BaseEntity implements IModel {
    private Long id;
    private String modelLabel;
    private String name;
    private List<BaseEntity> children;
    @JsonProperty("tree_id")
    private String treeId;
}
