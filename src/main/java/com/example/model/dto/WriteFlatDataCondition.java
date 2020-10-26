package com.example.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @ClassName : WriteFlatDataCondition
 * @Description :
 * @Author : zhangh
 * @Date: 2020-09-16 09:11
 */
@Getter
@Setter
public class WriteFlatDataCondition {
    private String modelLabel;
    private List<String> filter;
    private List<String> writeProperty;
    private List<List<Object>> filterData;
    private List<List<Object>> writeData;
    private String operator;
    private String writeMethod;
}
