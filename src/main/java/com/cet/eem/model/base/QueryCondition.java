package com.cet.eem.model.base;

import com.cet.eem.common.model.Page;
import com.cet.eem.model.constant.ConditionOperator;
import com.cet.eem.model.constant.GroupOperator;
import com.cet.eem.model.constant.OrderOperator;
import lombok.Setter;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 层次数据请求条件
 *
 * @author CKai
 */
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

    public QueryCondition() {
    }

    public QueryCondition(String rootLabel, Long rootId) {
        rootCondition = new FlatQueryConditionDTO();
        this.rootID = rootId;
        this.rootLabel = rootLabel;
        this.subLayerConditions = new ArrayList<>();
    }

    public List<TreeGroupModel> getGroups() {
        return groups;
    }

    public void setGroups(List<TreeGroupModel> groups) {
        this.groups = groups;
    }

    public List<SingleModelConditionDTO> getSubLayerConditions() {
        return subLayerConditions;
    }

    public void setSubLayerConditions(List<SingleModelConditionDTO> subLayerConditions) {
        this.subLayerConditions = subLayerConditions;
    }

    public FlatQueryConditionDTO getRootCondition() {
        return rootCondition;
    }

    public void setRootCondition(FlatQueryConditionDTO rootCondition) {
        this.rootCondition = rootCondition;
    }

    public String getRootLabel() {
        return rootLabel;
    }

    public void setRootLabel(String rootLabel) {
        this.rootLabel = rootLabel;
    }

    public Long getRootID() {
        return rootID;
    }

    public void setRootID(Long rootID) {
        this.rootID = rootID;
    }

    public Boolean getTreeReturnEnable() {
        return treeReturnEnable;
    }

    public void setTreeReturnEnable(Boolean treeReturnEnable) {
        this.treeReturnEnable = treeReturnEnable;
    }

    @Override
    public String toString() {
        return "QueryCondition{" +
                "groups=" + groups +
                ", subLayerConditions=" + subLayerConditions +
                ", rootCondition=" + rootCondition +
                ", rootLabel='" + rootLabel + '\'' +
                ", rootID=" + rootID +
                ", treeReturnEnable=" + treeReturnEnable +
                '}';
    }


    public static class Builder {
        /**
         * 定义模型组，用于指定查询与根节点直接关联的模型实例，并以其定义的标签返回
         */
        @Setter
        private List<TreeGroupModel> groups;
        /**
         * 子层级模型及筛选条件
         */
        @Setter
        private List<SingleModelConditionDTO> subLayerConditions;

        /**
         * 顶层节点的查询条件
         */
        @Setter
        private FlatQueryConditionDTO rootCondition;

        /**
         * 根节点的模型Label
         */
        private final String rootLabel;
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
            ConditionBlockCompose conditionBlockCompose = getFilter();
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
            ConditionBlockCompose conditionBlockCompose = getFilter();
            ConditionBlock conditionBlock = new ConditionBlock(cloumn, conditionOperator.getValue(), value, tagId);
            conditionBlockCompose.getExpressions().add(conditionBlock);
            return this;
        }

        /**
         * where 多条件用or连接，同组用and
         *
         * @return
         */
        public Builder composeWithOr() {
            ConditionBlockCompose conditionBlockCompose = getFilter();
            conditionBlockCompose.setComposemethod(true);
            return this;
        }

        /**
         * where 多条件用And连接，同组用or
         *
         * @return
         */
        public Builder composeWithAnd() {
            ConditionBlockCompose conditionBlockCompose = getFilter();
            conditionBlockCompose.setComposemethod(false);
            return this;
        }

        /**
         * 进行关联查询
         *
         * @param singleModelConditionDTO
         * @return
         */
        public Builder own(SingleModelConditionDTO singleModelConditionDTO) {
            List<SingleModelConditionDTO> subLayer = getSubLayer();
            subLayer.add(singleModelConditionDTO);
            return this;
        }

        /**
         * @param cloumns 要查询的字段
         * @return
         */
        public Builder select(String... cloumns) {
            FlatQueryConditionDTO rootCondition = getRootCondition();
            rootCondition.setProps(Arrays.asList(cloumns));
            return this;
        }

        @Deprecated
        public Builder join(String modelLabel) {
            if (this.rootCondition == null) {
                this.rootCondition = new FlatQueryConditionDTO();
            }
            if (this.rootCondition.getIncludeRelations() == null) {
                this.rootCondition.setIncludeRelations(new ArrayList<>());
            }
            this.rootCondition.getIncludeRelations().add(modelLabel);
            return this;
        }

        /**
         * 用于分组查询
         * 使用前必须先聚合
         *
         * @param column
         * @param groupOperator
         * @return
         */
        @Deprecated
        public Builder select(String column, GroupOperator groupOperator) {
            if (this.rootCondition == null) {
                this.rootCondition = new FlatQueryConditionDTO();
            }
            List<GroupBy> groupbys = this.rootCondition.getGroupbys();
            if (groupbys == null) {
                groupbys = new ArrayList<>(4);
                this.rootCondition.setGroupbys(groupbys);
            }
            GroupBy groupBy = new GroupBy();
            groupBy.setProperty(column);
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
            List<GroupBy> groupBys = getGroupBys();
            for (String cloumn : cloumns) {
                GroupBy groupBy = new GroupBy();
                groupBy.setProperty(cloumn);
                groupBys.add(groupBy);
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
            Page page = new Page(offset, pageSize);
            setPage(page);
            return this;
        }

        /**
         * 分页查询
         *
         * @param page
         * @return
         */
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
            FlatQueryConditionDTO rootCondition = getRootCondition();
            rootCondition.setOrders(orders);
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
            FlatQueryConditionDTO rootCondition = getRootCondition();
            Order order = new Order(cloumn, groupOperator.getValue(), orderPriority++);
            List<Order> orders = rootCondition.getOrders();
            if (orders == null) {
                orders = Arrays.asList(order);
                rootCondition.setOrders(orders);
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

        /**
         * 获取RootCondition
         *
         * @return
         */
        public FlatQueryConditionDTO getRootCondition() {
            if (this.rootCondition == null) {
                this.rootCondition = new FlatQueryConditionDTO();
            }
            return this.rootCondition;
        }

        /**
         * 获取条件集合
         *
         * @return
         */
        public ConditionBlockCompose getFilter() {
            FlatQueryConditionDTO rootCondition = getRootCondition();
            ConditionBlockCompose filter = rootCondition.getFilter();
            if (Objects.isNull(filter)) {
                filter = new ConditionBlockCompose();
                filter.setComposemethod(true);
                rootCondition.setFilter(filter);
            }
            return filter;
        }


        public List<GroupBy> getGroupBys() {
            FlatQueryConditionDTO rootCondition = getRootCondition();
            List<GroupBy> groupbys = rootCondition.getGroupbys();
            if (Objects.isNull(groupbys)) {
                groupbys = new ArrayList<>(4);
                rootCondition.setGroupbys(groupbys);
            }
            return groupbys;
        }

        public List<Order> getOrders() {
            FlatQueryConditionDTO rootCondition = getRootCondition();
            List<Order> orders = rootCondition.getOrders();
            if (Objects.isNull(orders)) {
                orders = new ArrayList<>(4);
                rootCondition.setOrders(orders);
            }
            return orders;
        }

        public List<SingleModelConditionDTO> getSubLayer() {
            if (Objects.isNull(this.subLayerConditions)) {
                this.subLayerConditions = new ArrayList<>(4);
            }
            return this.subLayerConditions;
        }

        /**
         * 转换为子模型的查询条件
         *
         * @return
         */
        public SingleModelConditionDTO convertToSubModel() {
            SingleModelConditionDTO singleModelConditionDTO = new SingleModelConditionDTO(this.rootLabel);
            if (Objects.nonNull(this.rootCondition) && Objects.nonNull(this.rootCondition.filter)) {
                singleModelConditionDTO.setFilter(this.getRootCondition().filter);
            }
            return singleModelConditionDTO;
        }

        public QueryCondition build() {
            QueryCondition queryCondition = new QueryCondition();
            queryCondition.setRootID(this.rootID);
            queryCondition.setRootLabel(this.rootLabel);
            queryCondition.setRootCondition(this.rootCondition);
            queryCondition.setGroups(this.groups);
            queryCondition.setSubLayerConditions(this.subLayerConditions);
            queryCondition.setTreeReturnEnable(this.treeReturnEnable);
            return queryCondition;
        }
    }

    @Deprecated
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

    @Deprecated
    public List<String> obtainJoinColumnName() {
        if (this.rootCondition == null) {
            return null;
        }
        List<String> includeRelations = this.rootCondition.getIncludeRelations();
        List<String> joinColumnNameList = new ArrayList<>(includeRelations.size());
        for (String includeRelation : includeRelations) {
            if (StringUtils.isEmpty(includeRelation)) {
                continue;
            }
            joinColumnNameList.add(includeRelation + "_model");
        }
        return joinColumnNameList;
    }

    @Deprecated
    public String obtainJoinColumnNameByIndex(int index) {
        if (this.rootCondition == null) {
            return null;
        }
        List<String> includeRelations = this.rootCondition.getIncludeRelations();
        String includeRelation = includeRelations.get(index);
        return includeRelation + "_model";
    }

}