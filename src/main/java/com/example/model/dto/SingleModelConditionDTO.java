package com.example.model.dto;


import com.example.model.constant.ConditionOperator;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

/**
 * 单模条件
 *
 * @author CKai
 */
@Getter
@Setter
public class SingleModelConditionDTO {

    /**
     * modelName
     */
    private String modelLabel;
    /**
     * filter的语法逻辑同FlatQueryConditionDTO
     */
    private ConditionBlockCompose filter;
    /**
     * 要查询的属性列，如果为空，则查询所有
     */
    private List<String> props;

    /**
     * 要查询的关联模型数据ID，包括了直接关联和间接关联
     * relations必须显示指定才会进行查询
     * 如果relations为空，则不会查询关联关系
     */
    private List<String> includeRelations;

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
            if (this.filter == null) {
                this.filter = new ConditionBlockCompose();
            }
            ConditionBlockCompose conditionBlockCompose = this.filter;
            ConditionBlock conditionBlock = new ConditionBlock(cloumn, conditionOperator.getValue(), value);
            conditionBlockCompose.getExpressions().add(conditionBlock);
            return this;
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


}
