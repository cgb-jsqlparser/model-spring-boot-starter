package com.cet.eem.metadata;

import com.cet.eem.annotation.ModelLabel;
import com.cet.eem.common.model.Result;
import com.cet.eem.exceptions.ModelServiceCallException;
import com.cet.eem.model.feign.ModelDataService;
import com.cet.eem.model.model.IModel;
import com.cet.eem.toolkit.CollectionUtils;
import com.cet.eem.toolkit.ReflectionKit;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.Assert;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class TableInfoHelper {

    private static final Map<Class<?>, TableInfo> MODEL_INFO_MAP = new ConcurrentHashMap<>();

    private static final WeakHashMap<String, Map<String, Object>> MODEL_INFO_CACHE = new WeakHashMap<>();

    private static ModelDataService modelDataService;

    public static void init(ModelDataService modelDataService) {
        TableInfoHelper.modelDataService = modelDataService;
    }

    public static TableInfo getModelInfo(Class<?> aClass) {
        TableInfo tableInfo = MODEL_INFO_MAP.get(aClass);
        if (tableInfo == null) {
            tableInfo = getModelInfoFromClass(aClass);
            MODEL_INFO_MAP.put(aClass, tableInfo);
        }
        return tableInfo;
    }

    private static TableInfo getModelInfoFromClass(Class<?> aClass) {
        ModelLabel annotation = aClass.getAnnotation(ModelLabel.class);
        Assert.notNull(annotation, aClass.getName() + " lack of Annotation ModelLabel");
        String tableName = annotation.value();
        Assert.hasLength(tableName, aClass.getName() + " Annotation ModelLabel Value Is Empty");
        Map<String, Object> modelInfo = queryModelInfo(tableName);
        Assert.notEmpty(modelInfo, aClass.getName() + " can't find model from database");
        Field[] declaredFields = aClass.getDeclaredFields();
        TableInfo tableInfo = new TableInfo(aClass);
        tableInfo.setTableName(tableName);
        tableInfo.setFieldList(initTableField(declaredFields, modelInfo));
        tableInfo.setOwnModelNameList(initSubModelLabel(declaredFields, modelInfo));
        return tableInfo;
    }


    private static Map<String, Object> queryModelInfo(String tableName) {
        Map<String, Object> data = MODEL_INFO_CACHE.get(tableName);
        if (data != null) {
            return data;
        }
        Result<List<Map<String, Object>>> modelInfoResult = modelDataService.searchModel(tableName);
        List<Map<String, Object>> resultData = modelInfoResult.getData();
        if (CollectionUtils.isEmpty(resultData)) {
            return Collections.emptyMap();
        }
        Map<String, Object> map = resultData.stream().filter(s -> tableName.equals(s.get("label"))).findAny().orElse(null);
        MODEL_INFO_CACHE.put(tableName, map);
        return map;
    }

    public static String getModelLabel(Class<?> aClass) {
        if (IModel.class.isAssignableFrom(aClass)) {
            TableInfo modelInfo = getModelInfo(aClass);
            return modelInfo.getTableName();
        }
        return null;
    }

    /**
     * 初始化关联模型
     *
     * @param declaredFields class定义的字段
     * @param modelInfo      模型信息
     * @return 此模型所有关联模型
     */
    @SuppressWarnings("unchecked")
    private static Set<String> initSubModelLabel(Field[] declaredFields, Map<String, Object> modelInfo) {
        Set<String> subModelLabelList;
        if (CollectionUtils.isEmpty(modelInfo)) {
            return Collections.emptySet();
        }
        List<Map<String, Object>> relationList = (List<Map<String, Object>>) modelInfo.get("relationList");
        if (CollectionUtils.isEmpty(relationList)) {
            return Collections.emptySet();
        }
        subModelLabelList = new HashSet<>(relationList.size());
        List<Map<String, Object>> relationMapList = (List<Map<String, Object>>) modelInfo.get("relationList");
        List<String> subModel = relationMapList.stream().filter(s -> "own".equals(s.get("relationShip"))).map(s -> (String) s.get("bType")).collect(Collectors.toList());
        for (Field declaredField : declaredFields) {
            Class<?> type = declaredField.getType();
            if (List.class.isAssignableFrom(type)) {
                type = ReflectionKit.getFieldGenericType(declaredField, 0);
            }
            if (IModel.class.isAssignableFrom(type)) {
                TableInfo tableInfo = getModelInfo(type);
                String tableName = tableInfo.getTableName();
                if (CollectionUtils.isNotEmpty(subModel) && subModel.contains(tableName)) {
                    subModelLabelList.add(tableName);
                    subModelLabelList.addAll(tableInfo.getOwnModelNameList());
                }
            }
        }
        return subModelLabelList;
    }

    /**
     * 初始化表的字段
     *
     * @param declaredFields class中定义的字段
     * @param modelInfo      模型服务中查询的字段
     * @return 表字段信息
     */
    @SuppressWarnings("unchecked")
    private static List<TableFieldInfo> initTableField(Field[] declaredFields, Map<String, Object> modelInfo) {
        List<TableFieldInfo> tableFieldInfoList;
        if (CollectionUtils.isEmpty(modelInfo)) {
            return Collections.emptyList();
        }
        List<Map<String, Object>> propertyListMap = (List<Map<String, Object>>) modelInfo.get("propertyList");
        if (CollectionUtils.isEmpty(propertyListMap)) {
            return Collections.emptyList();
        }
        tableFieldInfoList = new ArrayList<>(propertyListMap.size());
        List<String> propertyLabelList = propertyListMap.stream().map(s -> (String) s.get("propertyLabel")).collect(Collectors.toList());
        for (String propertyLabel : propertyLabelList) {
            List<Field> collect = Arrays.stream(declaredFields).filter(s -> fieldMatching(propertyLabel, s)).collect(Collectors.toList());
            if (collect.size() > 1) {
                throw new ModelServiceCallException("define two field with same name");
            }
            if (CollectionUtils.isEmpty(collect)) {
                continue;
            }
            TableFieldInfo tableFieldInfo = new TableFieldInfo(propertyLabel, collect.get(0));
            tableFieldInfoList.add(tableFieldInfo);
        }
        return tableFieldInfoList;
    }


    private static boolean fieldMatching(String modelFieldName, Field field) {
        String entityFieldName = field.getName();
        if (modelFieldName.equalsIgnoreCase(entityFieldName)) {
            return true;
        }
        modelFieldName = modelFieldName.replaceAll("-", "");
        if (modelFieldName.equalsIgnoreCase(entityFieldName)) {
            return true;
        }
        if (field.isAnnotationPresent(JsonProperty.class)) {
            JsonProperty annotation = field.getAnnotation(JsonProperty.class);
            String value = annotation.value();
            return modelFieldName.equals(value);
        }
        return false;
    }
}
