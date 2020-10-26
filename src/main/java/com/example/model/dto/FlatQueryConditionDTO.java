package com.example.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FlatQueryConditionDTO {
    private ConditionBlockCompose filter;
    private List<String> props;
    private ModelIdPairDTO treeNode;
    private List<Order> orders;
    private Page page;
    private List<GroupBy> groupbys;
    private boolean includeSubmodel;
    private List<String> includeRelations;
}