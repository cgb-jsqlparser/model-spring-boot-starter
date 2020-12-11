package com.cet.eem.model.base;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author zhangzhuang
 * @className ModelSingeWriteVo
 * @description 批量插入/更新单模型数据
 * @date 2020/8/7 11:41
 * <p>
 * modelLabel指定模型标签, filter指定查询字段, writeProperty指定插入/更新字段, filterData和writeData以二维数组形式传入数据,
 * operator指定更新类型(PLUS为累加, MULTIPLY为累乘, null或其他为更新), writeMethod指定写入方法: INSERT-插入, UPDATE-更新
 */
@Data
public class ModelSingeWriteVo {

    /**
     * 插入
     */
    public static final String INSERT = "INSERT";
    /**
     * 更新
     */
    public static final String UPDATE = "UPDATE";

    private List<String> filter;
    private List<List<Object>> filterData;
    private String modelLabel;
    private String operator = null;
    private List<List<Object>> writeData;
    private String writeMethod;
    private List<String> writeProperty;
}
