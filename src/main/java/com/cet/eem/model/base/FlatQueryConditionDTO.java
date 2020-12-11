package com.cet.eem.model.base;

import com.cet.eem.common.model.Page;
import lombok.Data;

import java.util.List;

/**
 * 扁平查询条件数据对象
 * @author CKai
 */
@Data
public class FlatQueryConditionDTO {
    /**
     *    filter字符串,与sql中的where查询条件一致，格式一致
     *    例如：
     *    数值范围查询 col1>100 and col1 < 200
     *    字符串比较查询 col2  = 'xxxx' 或 模糊搜索 : col2 like '%xxxx%'
     *    枚举条件查询 col3 in (1,2,3)
     *    条件之间支持 and  or
     *
     */
    public ConditionBlockCompose filter;
    public List<String> props;
    public ModelIdPairDTO treeNode;
    public List<Order> orders;
    public Page page;
    /**
     * 分组查询groupby, method为聚合方式, 支持MAX/MIN/AVG, 其他皆为null, property为对应属性, 聚合的返回为MAX_property格式; 当method为null时, property为分组的元素;
     * 否则property仅参与聚合, 不参与分组. 结果默认返回count_id
     */
    public List<GroupBy> groupbys;


    public boolean includeSubmodel;

    public List<String> includeRelations;

    @Override
    public String toString() {
        return "FlatQueryConditionDTO{" +
                "filter='" + filter + '\'' +
                ", props=" + props +
                ", treeNode=" + treeNode +
                ", orders=" + orders +
                ", page=" + page +
                ", include_submodel=" + includeSubmodel +
                ", include_relations=" + includeRelations +
                '}';
    }
}
