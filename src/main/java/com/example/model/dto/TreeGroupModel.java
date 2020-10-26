package com.example.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 树的组模型结构
 *
 * @author CKai
 */
@Getter
@Setter
public class TreeGroupModel {

    private String name;

    private List<String> models;
}
