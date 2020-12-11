package com.cet.eem.model.tool;



import com.cet.eem.model.base.ConditionBlock;
import com.cet.eem.model.base.ConditionBlockCompose;
import com.cet.eem.model.base.SingleModelConditionDTO;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

/**
 * 子查询条件构造器
 *
 * @author zhangzhuang
 * @date 2020/12/4
 */
public class SubConditionBuilder {
    protected SingleModelConditionDTO subCondition;

    private SubConditionBuilder() {

    }

    public SubConditionBuilder(@NotBlank(message = "模型标识不允许为空") String modelLabel) {
        this.subCondition = new SingleModelConditionDTO(modelLabel);
    }

    /**
     * 设置当前查询节点与父节点等层级深度
     *
     * @param depth 查询深度
     * @return 构造对象
     */
    public SubConditionBuilder queryDepth(Integer depth) {
        this.subCondition.setDepth(depth);
        return this;
    }

    /**
     * 选择需要查询的字段
     *
     * @param props 表中需要查询字段的集合，当该值为null时，表示查询所有字段
     * @return 构造对象
     */
    public SubConditionBuilder select(List<String> props) {
        this.subCondition.setProps(props);
        return this;
    }

    /**
     * 过滤条件
     *
     * @param prop     过滤字段
     * @param operator 操作类型 {@link ConditionBlock}
     * @param value    值
     * @param group    分组
     * @return 构造对象
     */
    public SubConditionBuilder where(@NotBlank(message = "过滤字段名不允许为空！") String prop,
                                     @NotBlank(message = "过滤类型不允许为空！") String operator,
                                     Object value,
                                     Integer group) {
        ConditionBlockCompose compose = createCompose();

        List<ConditionBlock> filters = compose.getExpressions();
        if (filters == null) {
            filters = new ArrayList<>();
            compose.setExpressions(filters);
        }
        filters.add(new ConditionBlock(prop, operator, value, group));
        return this;
    }

    /**
     * 过滤条件
     *
     * @param prop     过滤字段
     * @param operator 操作类型
     * @param value    值
     * @return 构造对象
     */
    public SubConditionBuilder where(@NotBlank(message = "过滤字段名不允许为空！") String prop,
                                     @NotBlank(message = "过滤类型不允许为空！") String operator,
                                     Object value) {
        return this.where(prop, operator, value, null);
    }

    /**
     * 设置分组查询条件与或关系
     *
     * @param method true：组内与，组间或；false：组内或，组间与
     * @return 构造对象
     */
    public SubConditionBuilder composeMethod(boolean method) {
        ConditionBlockCompose compose = createCompose();
        compose.setComposemethod(method);
        return this;
    }

    /**
     * 构建查询参数
     *
     * @return 查询参数
     */
    public SingleModelConditionDTO build() {
        return this.subCondition;
    }

    /**
     * 创建ConditionBlockCompose对象
     *
     * @return ConditionBlockCompose对象
     */
    private ConditionBlockCompose createCompose() {
        ConditionBlockCompose compose = this.subCondition.getFilter();
        if (compose == null) {
            compose = new ConditionBlockCompose();
            this.subCondition.setFilter(compose);
        }

        return compose;
    }
}
