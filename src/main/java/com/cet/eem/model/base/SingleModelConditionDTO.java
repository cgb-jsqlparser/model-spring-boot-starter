package com.cet.eem.model.base;

import com.cet.eem.model.constant.ConditionOperator;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

/**
 * 单模条件
 *
 * @author CKai
 */
@Data
public class SingleModelConditionDTO {

    public String modelLabel;
    /**
     * filter的语法逻辑同FlatQueryConditionDTO
     */
    public ConditionBlockCompose filter;
    /**
     * 要查询的属性列，如果为空，则查询所有
     */
    public List<String> props;
    /**
     * 需要查询的数据的深度
     */
    public Integer depth;

    /**
     * 要查询的关联模型数据ID，包括了直接关联和间接关联
     * relations必须显示指定才会进行查询
     * 如果relations为空，则不会查询关联关系
     */
    public List<String> includeRelations;

    public SingleModelConditionDTO() {
    }

    public SingleModelConditionDTO(String modelLabel, List<String> props) {
        this.modelLabel = modelLabel;
        this.props = props;
    }

    public SingleModelConditionDTO(String modelLabel) {
        this.modelLabel = modelLabel;
    }

    public SingleModelConditionDTO(String modelLabel, Integer depth) {
        this.modelLabel = modelLabel;
        this.depth = depth;
    }

    public static class Builder {
        /**
         * modelName
         */
        public String modelLabel;
        /**
         * filter的语法逻辑同FlatQueryConditionDTO
         */
        public ConditionBlockCompose filter;
        /**
         * 要查询的属性列，如果为空，则查询所有
         */
        public List<String> props;

        /**
         * 要查询的关联模型数据ID，包括了直接关联和间接关联
         * relations必须显示指定才会进行查询
         * 如果relations为空，则不会查询关联关系
         */
        public List<String> includeRelations;

        public Builder(String rootLabel) {
            this.modelLabel = rootLabel;
        }


        public Builder select(String... cloumns) {
            this.props = Arrays.asList(cloumns);
            return this;
        }

        public Builder where(String cloumn, ConditionOperator conditionOperator, Object value) {
            ConditionBlockCompose conditionBlockCompose = getFilter();
            ConditionBlock conditionBlock = new ConditionBlock(cloumn, conditionOperator.getValue(), value);
            conditionBlockCompose.getExpressions().add(conditionBlock);
            return this;
        }

        public ConditionBlockCompose getFilter() {
            if (this.filter == null) {
                this.filter = new ConditionBlockCompose();
            }
            return this.filter;
        }

        public SingleModelConditionDTO build() {
            SingleModelConditionDTO singleModelConditionDTO = new SingleModelConditionDTO();
            singleModelConditionDTO.setModelLabel(this.modelLabel);
            singleModelConditionDTO.setFilter(this.filter);
            singleModelConditionDTO.setIncludeRelations(this.includeRelations);
            singleModelConditionDTO.setProps(this.props);
            return singleModelConditionDTO;
        }
    }

    @Override
    public String toString() {
        return "SingleModelConditionDTO{" +
                "modelLabel='" + modelLabel + '\'' +
                ", filter='" + filter + '\'' +
                ", props=" + props +
                ", depth=" + depth +
                ", include_relations=" + includeRelations +
                '}';
    }
}
