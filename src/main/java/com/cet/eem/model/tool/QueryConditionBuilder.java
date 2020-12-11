package com.cet.eem.model.tool;

import com.cet.eem.common.model.Page;
import com.cet.eem.model.base.*;
import com.cet.eem.model.constant.GroupOperator;
import com.cet.eem.model.model.BaseEntity;
import com.cet.eem.toolkit.CollectionUtils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 查询条件构造器
 *
 * @author zhangzhuang
 * @date 2020/10/1
 */
public class QueryConditionBuilder<T extends BaseEntity> {
    /**
     * 构造对象，初始化时创建对象
     */
    private final QueryCondition queryCondition;

    /**
     * 父层级查询条件，初始化时创建对象，不允许为null
     */
    private FlatQueryConditionDTO rootCondition;

    /**
     * 分页查询最大数量
     */
    private final static int PAGE_MAX_VALUE = 999999999;


    /**
     * 构造函数
     *
     * @param modelLabel 模型标识
     */
    public QueryConditionBuilder(@NotBlank(message = "模型标识不允许为空") String modelLabel) {
        this.queryCondition = new QueryCondition();
        this.queryCondition.setRootLabel(modelLabel);
        this.rootCondition = new FlatQueryConditionDTO();
        Page page = new Page(0, PAGE_MAX_VALUE);
        this.rootCondition.setPage(page);
        this.queryCondition.setRootCondition(this.rootCondition);
    }

    /**
     * 构造函数
     *
     * @param modelLabel 模型标识
     * @param id         记录id
     */
    public QueryConditionBuilder(@NotBlank(message = "模型标识不允许为空") String modelLabel, Long id) {
        this(modelLabel);
        this.queryCondition.setRootID(id);
    }

    /**
     * 构造函数
     *
     * @param modelLabel 模型标识
     * @param ids        主键标识集合，如果不传，那么该条件会被忽略掉，即查询所有
     */
    public QueryConditionBuilder(@NotBlank(message = "模型标识不允许为空") String modelLabel, List<Long> ids) {
        this(modelLabel);
        if (CollectionUtils.isNotEmpty(ids)) {
            this.where(QueryResultContentTaker.ID, ConditionBlock.OPERATOR_IN, ids);
        }
    }

    /**
     * 构造函数
     *
     * @param condition 查询条件对象
     */
    public QueryConditionBuilder(@NotNull QueryCondition condition) {
        this.queryCondition = condition;
        this.rootCondition = condition.getRootCondition();
        if (this.rootCondition == null) {
            this.rootCondition = new FlatQueryConditionDTO();
            this.queryCondition.setRootCondition(this.rootCondition);
        }
    }

    /**
     * 设置查询结果是否以树的方式返回
     *
     * @param tree 是否以树的方式返回，true以树的方式返回，false以modelLabel_model的方式返回
     * @return 构造对象
     */
    public QueryConditionBuilder<T> queryAsTree(boolean tree) {
        this.queryCondition.setTreeReturnEnable(tree);
        return this;
    }

    /**
     * 设置查询结果以树的方式返回
     *
     * @return 构造对象
     */
    public QueryConditionBuilder<T> queryAsTree() {
        this.queryCondition.setTreeReturnEnable(true);
        return this;
    }

    /**
     * 选择需要查询的字段
     *
     * @param props 表中需要查询字段的集合，当该值为null时，表示查询所有字段
     * @return 构造对象
     */
    public QueryConditionBuilder<T> select(List<String> props) {
        this.rootCondition.setProps(props);
        return this;
    }

    /**
     * 过滤条件
     *
     * @param prop     过滤字段
     * @param operator 操作类型 {@link com.cet.eem.model.base.ConditionBlock}
     * @param value    值
     * @param group    分组
     * @return 构造对象
     */
    public QueryConditionBuilder<T> where(@NotBlank(message = "过滤字段名不允许为空！") String prop,
                                          @NotBlank(message = "过滤类型不允许为空！") String operator,
                                          Object value, Integer group) {
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
    public QueryConditionBuilder<T> where(@NotBlank(message = "过滤字段名不允许为空！") String prop,
                                          @NotBlank(message = "过滤类型不允许为空！") String operator,
                                          Object value) {
        return this.where(prop, operator, value, null);
    }

    /**
     * 排序
     *
     * @param prop 排序字段
     * @param asc  排序方式，true升序，false降序
     * @return 构造对象
     */
    public QueryConditionBuilder<T> orderBy(String prop, boolean asc) {
        List<Order> orders = rootCondition.getOrders();
        if (orders == null) {
            orders = new ArrayList<>();
            rootCondition.setOrders(orders);
        }

        Order order = new Order(prop, asc ? ConditionBlock.ASC : ConditionBlock.DESC, orders.size() + 1);
        orders.add(order);
        return this;
    }

    /**
     * 升序排序
     *
     * @param prop 排序字段
     * @return 构造对象
     */
    public QueryConditionBuilder<T> orderBy(String prop) {
        return this.orderBy(prop, true);
    }

    /**
     * 降序排序
     *
     * @param prop 排序字段
     * @return 构造对象
     */
    public QueryConditionBuilder<T> orderByDescending(String prop) {
        return this.orderBy(prop, false);
    }

    /**
     * 设置分页查询数量
     *
     * @param index 分页起始index
     * @param limit 最大查询数量
     * @return 构造对象
     */
    public QueryConditionBuilder<T> limit(int index, int limit) {
        Page page = rootCondition.getPage();
        if (page == null) {
            page = new Page();
            rootCondition.setPage(page);
        }
        page.index = index;
        page.limit = limit;
        return this;
    }

    /**
     * 设置分组查询条件与或关系
     *
     * @param method true：组内与，组间或；false：组内或，组间与
     * @return 构造对象
     */
    public QueryConditionBuilder<T> composeMethod(boolean method) {
        ConditionBlockCompose compose = createCompose();
        compose.setComposemethod(method);
        return this;
    }

    /**
     * 分组
     *
     * @param field 分组字段
     * @return 构造对象
     */
    public QueryConditionBuilder<T> groupBy(@NotBlank(message = "分组字段不允许为空！") String field) {
        List<GroupBy> groupBys = getGroupBys();

        groupBys.add(new GroupBy(null, field));
        return this;
    }

    /**
     * 数据计数
     *
     * @param field 分组字段
     * @return 构造对象
     */
    public QueryConditionBuilder<T> count(@NotBlank(message = "统计字段不允许为空！") String field) {
        aggregationQuery(field, GroupOperator.COUNT.getValue());

        return this;
    }

    /**
     * 数据求和
     *
     * @param field 分组字段
     * @return 构造对象
     */
    public QueryConditionBuilder<T> sum(@NotBlank(message = "统计字段不允许为空！") String field) {
        aggregationQuery(field, GroupOperator.SUM.getValue());

        return this;
    }

    /**
     * 数据求最大值
     *
     * @param field 分组字段
     * @return 构造对象
     */
    public QueryConditionBuilder<T> max(@NotBlank(message = "统计字段不允许为空！") String field) {
        aggregationQuery(field, GroupOperator.MAX.getValue());

        return this;
    }

    /**
     * 数据求最小值
     *
     * @param field 分组字段
     * @return 构造对象
     */
    public QueryConditionBuilder<T> min(@NotBlank(message = "统计字段不允许为空！") String field) {
        aggregationQuery(field, GroupOperator.MIN.getValue());

        return this;
    }

    /**
     * 聚合函数查询
     *
     * @param field           聚合字段
     * @param aggregationType 数据库聚合方式 {@link GroupOperator}
     */
    private void aggregationQuery(@NotBlank(message = "统计字段不允许为空！") String field, String aggregationType) {
        List<GroupBy> groupBys = getGroupBys();
        Optional<GroupBy> first = groupBys.stream().filter(it -> it.getProperty().equals(field)).findFirst();
        if (first.isPresent()) {
            first.get().setMethod(null);
        } else {
            groupBys.add(new GroupBy(GroupOperator.COUNT.getValue().equals(aggregationType) ? null : aggregationType, field));
        }
    }

    /**
     * 获取分组信息
     *
     * @return 分组信息
     */
    private List<GroupBy> getGroupBys() {
        List<GroupBy> groupBys = this.rootCondition.getGroupbys();
        if (CollectionUtils.isEmpty(groupBys)) {
            groupBys = new ArrayList<>();
            this.rootCondition.setGroupbys(groupBys);
        }
        return groupBys;
    }

    /**
     * 查询子节点
     *
     * @param childLabels 子节点
     * @return 构造对象
     */
    public QueryConditionBuilder<T> selectChildByLabels(List<String> childLabels) {
        if (CollectionUtils.isEmpty(childLabels)) {
            return this;
        }

        List<SingleModelConditionDTO> subLayerConditions = new ArrayList<>();
        for (String childLabel : childLabels) {
            subLayerConditions.add(new SingleModelConditionDTO(childLabel));
        }
        this.queryCondition.setSubLayerConditions(subLayerConditions);
        return this;
    }

    public QueryConditionBuilder<T> selectChild(SingleModelConditionDTO subLayerCondition) {
        List<SingleModelConditionDTO> subLayerConditions = this.queryCondition.getSubLayerConditions();
        if (subLayerConditions == null) {
            subLayerConditions = new ArrayList<>();
            this.queryCondition.setSubLayerConditions(subLayerConditions);
        }

        subLayerConditions.add(subLayerCondition);
        return this;
    }

    /**
     * 查询子节点
     *
     * @param subLayerConditions 子节点查询条件
     * @return 构造对象
     */
    public QueryConditionBuilder<T> selectChildren(List<SingleModelConditionDTO> subLayerConditions) {
        this.queryCondition.setSubLayerConditions(subLayerConditions);
        return this;
    }

    /**
     * 构建查询参数
     *
     * @return 查询参数
     */
    public QueryCondition build() {
        return this.queryCondition;
    }

    /**
     * 创建ConditionBlockCompose对象
     *
     * @return ConditionBlockCompose对象
     */
    private ConditionBlockCompose createCompose() {
        ConditionBlockCompose compose = this.rootCondition.getFilter();
        if (compose == null) {
            compose = new ConditionBlockCompose();
            this.rootCondition.setFilter(compose);
        }

        return compose;
    }
}
