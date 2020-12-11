package com.cet.eem.model.tool;


import com.cet.eem.common.util.JsonUtil;
import com.cet.eem.model.model.IModel;
import lombok.Getter;
import org.springframework.util.Assert;

import java.util.*;

/**
 * @ClassName : RelationModelCud
 * @Description : 关系模型增删改
 * @Author : zhangh
 * @Date: 2020-09-10 10:46
 */
public class ModelRelationCud {

    @Getter
    private final List<Map<String, Object>> objectMapList = new LinkedList<>();

    public void addRelation(Map<String, Object> objectMap) {
        objectMapList.add(objectMap);
    }

    public void addRelation(Builder builder) {
        objectMapList.add(builder.build());
    }

    public static class Builder {

        private Map<String, Object> objectMap;

        public Builder(IModel iModel) {
            objectMap = new HashMap<>();
            objectMap.put("id", iModel.getId());
            objectMap.put("modelLabel", iModel.getModelLabel());
        }

        public Builder(Long id, String modelLabel) {
            objectMap = new HashMap<>();
            objectMap.put("id", id);
            objectMap.put("modelLabel", modelLabel);
        }

        public Builder saveChildModel(Collection<? extends IModel> iModels) {
            List<Map<String, Object>> children = getChildren();
            for (IModel aModel : iModels) {
                children.add(JsonUtil.convert2Map(aModel));
            }
            return this;
        }

        public Builder deleteChildModel(Collection<? extends IModel> iModels) {
            List<Map<String, Object>> children = getChildren();
            for (IModel aModel : iModels) {
                Map<String, Object> stringObjectMap = JsonUtil.convert2Map(aModel);
                stringObjectMap.put("delete_flag", true);
                children.add(stringObjectMap);
            }
            return this;
        }

        public Builder moveChildModel(Collection<? extends IModel> iModels) {
            List<Map<String, Object>> children = getChildren();
            for (IModel aModel : iModels) {
                Map<String, Object> stringObjectMap = JsonUtil.convert2Map(aModel);
                stringObjectMap.put("moveout_flag", true);
                children.add(stringObjectMap);
            }
            return this;
        }

        public Builder replaceChildModel(IModel oldModel, IModel newModel) {
            Assert.notNull(oldModel, "Old Model Cannot Be Null");
            Assert.notNull(newModel, "New Model Cannot Be Null");
            List<Map<String, Object>> children = getChildren();
            Map<String, Object> objectMap1 = JsonUtil.convert2Map(newModel);
            Map<String, Object> objectMap2 = JsonUtil.convert2Map(oldModel);
            objectMap1.put("move_from", objectMap2);
            children.add(objectMap1);
            return this;
        }

        @SuppressWarnings({"unchecked"})
        private List<Map<String, Object>> getChildren() {
            List<Map<String, Object>> children = (List<Map<String, Object>>) objectMap.get("children");
            if (Objects.isNull(children)) {
                children = new ArrayList<>(1);
                objectMap.put("children", children);
            }
            return children;
        }

        public Map<String, Object> build() {
            return objectMap;
        }
    }
}