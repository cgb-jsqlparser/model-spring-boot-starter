package com.cet.eem.model.tool;

import com.cet.eem.toolkit.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @ClassName : QueryResultContentTaker
 * @Description : 方便取出QueryCondition查询内容
 * @Author : zhangh
 * @Date: 2020-12-10 16:47
 */
public class QueryResultContentTaker {

    public static final String CHILDREN = "children";
    public static final String ID = "id";
    public static final String MODEL_LABEL = "modelLabel";

    /**
     * 获取第一个元素的所有子元素
     *
     * @param queryResult 实际类型必须是List<Map<String,Object>> 查询结果
     * @return 子元素
     */
    @SuppressWarnings({"unchecked"})
    public static List<Map<String, Object>> getChildrenAtFirstElement(Object queryResult) {
        List<Map<String, Object>> actualResultType = (List<Map<String, Object>>) queryResult;
        Map<String, Object> map = actualResultType.stream().findFirst().orElse(null);
        if (Objects.isNull(map)) {
            return null;
        }
        return (List<Map<String, Object>>) map.get(CHILDREN);
    }

    /**
     * 获取获取第一个元素的第一个子元素
     *
     * @param queryResult 实际类型必须是List<Map<String,Object>> 查询结果
     * @return 子元素
     */
    public static Map<String, Object> getFirstChildrenAtFirstElement(Object queryResult) {
        List<Map<String, Object>> children = getChildrenAtFirstElement(queryResult);
        if (CollectionUtils.isEmpty(children)) {
            return Collections.emptyMap();
        }
        return children.stream().findFirst().orElse(Collections.emptyMap());
    }

    /**
     * 获取ID
     *
     * @param queryResult
     * @return
     */
    public static Long getId(Map<String, Object> queryResult) {
        Object id = queryResult.get(ID);
        if (Objects.isNull(id)) {
            return null;
        }
        return Long.parseLong(id.toString());
    }

    /**
     * 获取模型名
     *
     * @param queryResult
     * @return
     */
    public static String getModelLabel(Map<String, Object> queryResult) {
        return (String) queryResult.get(MODEL_LABEL);
    }
}
