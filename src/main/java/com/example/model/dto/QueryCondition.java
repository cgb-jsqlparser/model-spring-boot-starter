package com.example.model.dto;


import com.example.common.util.JsonUtil;
import com.example.model.constant.ConditionOperator;
import com.example.model.constant.GroupOperator;
import com.example.model.constant.OrderOperator;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Getter
@Setter
public class QueryCondition {

    /**
     * 定义模型组，用于指定查询与根节点直接关联的模型实例，并以其定义的标签返回
     */
    private List<TreeGroupModel> groups;
    /**
     * 子层级模型及筛选条件
     */
    private List<SingleModelConditionDTO> subLayerConditions;

    /**
     * 顶层节点的查询条件
     */
    private FlatQueryConditionDTO rootCondition;

    /**
     * 根节点的模型Label
     */
    private String rootLabel;
    /**
     * 根节点id，0或空表示所有
     */
    private Long rootID;
    /**
     * 返回的形式，true-树:children，false-层次结构：_model
     */
    private Boolean treeReturnEnable;

    public static class Builder {
        /**
         * 定义模型组，用于指定查询与根节点直接关联的模型实例，并以其定义的标签返回
         */
        private List<TreeGroupModel> groups;
        /**
         * 子层级模型及筛选条件
         */
        private List<SingleModelConditionDTO> subLayerConditions;

        /**
         * 顶层节点的查询条件
         */
        private FlatQueryConditionDTO rootCondition;

        /**
         * 根节点的模型Label
         */
        private String rootLabel;
        /**
         * 根节点id，0或空表示所有
         */
        private Long rootID;
        /**
         * 返回的形式，true-树:children，false-层次结构：_model
         */
        private Boolean treeReturnEnable;
        /**
         * 排序优先级
         */
        private int orderPriority;

        public Builder(String rootLabel) {
            this.rootLabel = rootLabel;
        }

        /**
         * 根据主键筛选
         *
         * @param id
         * @return
         */
        public Builder filterByPrimaryKey(Long id) {
            this.rootID = id;
            return this;
        }

        /**
         * 封装查询条件
         *
         * @param cloumn
         * @param conditionOperator
         * @param value
         * @return
         */
        public Builder where(String cloumn, ConditionOperator conditionOperator, Object value) {
            ConditionBlockCompose conditionBlockCompose = null;
            if (this.rootCondition == null) {
                this.rootCondition = new FlatQueryConditionDTO();
            }
            if (this.rootCondition.getFilter() == null) {
                conditionBlockCompose = new ConditionBlockCompose();
                this.rootCondition.setFilter(conditionBlockCompose);
            }
            conditionBlockCompose = this.rootCondition.getFilter();
            ConditionBlock conditionBlock = new ConditionBlock(cloumn, conditionOperator.getValue(), value);
            conditionBlockCompose.getExpressions().add(conditionBlock);
            return this;
        }

        /**
         * 封装查询条件
         *
         * @param cloumn
         * @param conditionOperator
         * @param value
         * @param tagId             分组条件>0
         * @return
         */
        public Builder where(String cloumn, ConditionOperator conditionOperator, Object value, int tagId) {
            ConditionBlockCompose conditionBlockCompose = null;
            if (this.rootCondition == null) {
                this.rootCondition = new FlatQueryConditionDTO();
            }
            if (this.rootCondition.getFilter() == null) {
                conditionBlockCompose = new ConditionBlockCompose();
                this.rootCondition.setFilter(conditionBlockCompose);
            }
            conditionBlockCompose = this.rootCondition.getFilter();
            ConditionBlock conditionBlock = new ConditionBlock(cloumn, conditionOperator.getValue(), value);
            conditionBlock.setTagId(tagId);
            conditionBlockCompose.getExpressions().add(conditionBlock);
            return this;
        }

        /**
         * where 多条件用or连接，同组用and
         *
         * @return
         */
        public Builder composeWithOr() {
            ConditionBlockCompose conditionBlockCompose = null;
            if (this.rootCondition == null) {
                this.rootCondition = new FlatQueryConditionDTO();
            }
            if (this.rootCondition.getFilter() == null) {
                conditionBlockCompose = new ConditionBlockCompose();
                this.rootCondition.setFilter(conditionBlockCompose);
            }
            conditionBlockCompose = this.rootCondition.getFilter();
            conditionBlockCompose.setComposeMethod(true);
            return this;
        }

        /**
         * where 多条件用And连接，同组用or
         *
         * @return
         */
        public Builder composeWithAnd() {
            ConditionBlockCompose conditionBlockCompose = null;
            if (this.rootCondition == null) {
                this.rootCondition = new FlatQueryConditionDTO();
            }
            if (this.rootCondition.getFilter() == null) {
                conditionBlockCompose = new ConditionBlockCompose();
                this.rootCondition.setFilter(conditionBlockCompose);
            }
            conditionBlockCompose = this.rootCondition.getFilter();
            conditionBlockCompose.setComposeMethod(false);
            return this;
        }

        /**
         * 查询拥有的子模块数据
         *
         * @param singleModelConditionDTO
         * @return
         */
        public Builder own(SingleModelConditionDTO singleModelConditionDTO) {
            if (subLayerConditions == null) {
                subLayerConditions = new ArrayList<>(8);
            }
            List<String> collect = subLayerConditions.stream().map(SingleModelConditionDTO::getModelLabel).collect(Collectors.toList());
            if (collect.contains(singleModelConditionDTO.getModelLabel())) {
                return this;
            }
            subLayerConditions.add(singleModelConditionDTO);
            return this;
        }

        /**
         * @param cloumns 要查询的字段
         * @return
         */
        public Builder select(String... cloumns) {
            if (this.rootCondition == null) {
                this.rootCondition = new FlatQueryConditionDTO();
            }
            this.rootCondition.setProps(Arrays.asList(cloumns));
            return this;
        }

        /**
         * 用于分组查询
         * 使用前必须先聚合
         *
         * @param cloumn
         * @param groupOperator
         * @return
         */
        public Builder select(String cloumn, GroupOperator groupOperator) {
            if (this.rootCondition == null) {
                this.rootCondition = new FlatQueryConditionDTO();
            }
            List<GroupBy> groupbys = this.rootCondition.getGroupbys();
            if (groupbys == null) {
                groupbys = new ArrayList<>(4);
                this.rootCondition.setGroupbys(groupbys);
            }
            GroupBy groupBy = new GroupBy();
            groupBy.setProperty(cloumn);
            groupBy.setMethod(groupOperator.getValue());
            groupbys.add(groupBy);
            return this;
        }

        /**
         * 分组
         *
         * @param cloumns
         * @return
         */
        public Builder groupBy(String... cloumns) {
            if (this.rootCondition == null) {
                this.rootCondition = new FlatQueryConditionDTO();
            }
            List<GroupBy> groupbys = this.rootCondition.getGroupbys();
            if (groupbys == null) {
                groupbys = new ArrayList<>(4);
                this.rootCondition.setGroupbys(groupbys);
            }
            for (String cloumn : cloumns) {
                GroupBy groupBy = new GroupBy();
                groupBy.setProperty(cloumn);
                groupbys.add(groupBy);
            }
            return this;
        }

        /**
         * 分页查询
         *
         * @param offset   偏移量
         * @param pageSize 页面大小
         * @return
         */
        public Builder byPage(int offset, int pageSize) {
            if (this.rootCondition == null) {
                this.rootCondition = new FlatQueryConditionDTO();
            }
            Page page = new Page(offset, pageSize);
            this.rootCondition.setPage(page);
            return this;
        }

        public Builder setPage(Page page) {
            if (page == null) {
                return this;
            }
            if (this.rootCondition == null) {
                this.rootCondition = new FlatQueryConditionDTO();
            }
            this.rootCondition.setPage(page);
            return this;
        }

        public Builder setOrderBy(List<Order> orders) {
            if (orders == null) {
                return this;
            }
            if (this.rootCondition == null) {
                this.rootCondition = new FlatQueryConditionDTO();
            }
            this.rootCondition.setOrders(orders);
            return this;
        }

        /**
         * 根据字段排序
         *
         * @param cloumn
         * @param groupOperator
         * @return
         */
        public Builder orderBy(String cloumn, OrderOperator groupOperator) {
            if (this.rootCondition == null) {
                this.rootCondition = new FlatQueryConditionDTO();
            }
            Order order = new Order(cloumn, groupOperator.getValue(), orderPriority++);
            List<Order> orders = this.rootCondition.getOrders();
            if (orders == null) {
                orders = Arrays.asList(order);
                this.rootCondition.setOrders(orders);
            } else {
                orders.add(order);
            }
            return this;
        }

        /**
         * 树形返回
         *
         * @return
         */
        public Builder tree() {
            this.treeReturnEnable = true;
            return this;
        }

        public QueryCondition build() {
            QueryCondition queryCondition = new QueryCondition();
            queryCondition.setGroups(this.groups);
            queryCondition.setRootCondition(this.rootCondition);
            queryCondition.setRootID(this.rootID);
            queryCondition.setRootLabel(this.rootLabel);
            queryCondition.setSubLayerConditions(this.subLayerConditions);
            queryCondition.setTreeReturnEnable(this.treeReturnEnable);
            System.out.println(JsonUtil.toJSONString(queryCondition));
            return queryCondition;
        }
    }

    public List<String> obtainAggregationColumnName() {
        if (this.rootCondition == null) {
            return null;
        }
        List<GroupBy> groupbys = this.rootCondition.getGroupbys();
        List<String> aggregationColumnNameList = new ArrayList<>(groupbys.size());
        for (GroupBy groupby : groupbys) {
            if (StringUtils.isEmpty(groupby.getMethod())) {
                continue;
            }
            aggregationColumnNameList.add(groupby.getMethod().toLowerCase() + "_" + groupby.getProperty());
        }
        return aggregationColumnNameList;
    }

    public void setPage(Page page) {
        if (Objects.nonNull(page)) {
            if (Objects.isNull(rootCondition)) {
                rootCondition = new FlatQueryConditionDTO();
            }
            rootCondition.setPage(page);
        }
    }

}
