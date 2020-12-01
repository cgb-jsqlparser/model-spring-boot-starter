package com.cet.metadata;

import com.cet.annotation.ModelLabel;
import com.cet.eem.common.feign.ModelDataService;
import com.cet.eem.common.model.Result;
import com.cet.exceptions.ModelAdapterException;
import com.cet.model.model.IModel;
import com.cet.toolkit.CollectionUtils;
import com.cet.toolkit.ReflectionKit;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.Assert;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class TableInfoHelper {

    private static final Map<Class<?>, TableInfo> modelInfoMap = new ConcurrentHashMap<>();

    private static final WeakHashMap<String, Map<String, Object>> modelInfoCache = new WeakHashMap<>();

    private static ModelDataService modelDataService;

    public static void init(ModelDataService modelDataService) {
        TableInfoHelper.modelDataService = modelDataService;
    }

    public static TableInfo getModelInfo(Class<?> aClass) {
        TableInfo tableInfo = modelInfoMap.get(aClass);
        if (tableInfo == null) {
            tableInfo = getModelInfoFromClass(aClass);
            modelInfoMap.put(aClass, tableInfo);
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
        Map<String, Object> data = modelInfoCache.get(tableName);
        if (data != null) {
            return data;
        }
        Result<List<Map<String, Object>>> modelInfoResult = modelDataService.searchModel(tableName);
        List<Map<String, Object>> resultData = modelInfoResult.getData();
        if (CollectionUtils.isEmpty(resultData)) {
            return Collections.EMPTY_MAP;
        }
        Map<String, Object> map = resultData.stream().filter(s -> tableName.equals(s.get("label"))).findAny().orElse(null);
        modelInfoCache.put(tableName, map);
        return map;
    }

    public static String getModelLabel(Class<?> aClass) {
        if (IModel.class.isAssignableFrom(aClass)) {
            TableInfo modelInfo = getModelInfo(aClass);
            return modelInfo.getTableName();
        }
        return null;
    }

    private static List<String> initSubModelLabel(Field[] declaredFields, Map<String, Object> modelInfo) {
        List<String> subModelLabelList = Collections.EMPTY_LIST;
        if (CollectionUtils.isEmpty(modelInfo)) {
            return subModelLabelList;
        }
        List<Map<String, Object>> relationList = (List<Map<String, Object>>) modelInfo.get("relationList");
        if (CollectionUtils.isEmpty(relationList)) {
            return subModelLabelList;
        } else {
            subModelLabelList = new ArrayList<>(relationList.size());
        }
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

    private static List<TableFieldInfo> initTableField(Field[] declaredFields, Map<String, Object> modelInfo) {
        List<TableFieldInfo> tableFieldInfoList = null;
        if (CollectionUtils.isEmpty(modelInfo)) {
            return tableFieldInfoList;
        }
        List<Map<String, Object>> propertyListMap = (List<Map<String, Object>>) modelInfo.get("propertyList");
        if (CollectionUtils.isEmpty(propertyListMap)) {
            return tableFieldInfoList;
        } else {
            tableFieldInfoList = new ArrayList<>(propertyListMap.size());
        }
        List<String> propertyLabelList = propertyListMap.stream().map(s -> (String) s.get("propertyLabel")).collect(Collectors.toList());
        for (String propertyLabel : propertyLabelList) {
            List<Field> collect = Arrays.stream(declaredFields).filter(s -> fieldMatching(propertyLabel, s)).collect(Collectors.toList());
            if (collect.size() > 1) {
                throw new ModelAdapterException("define two field with same name");
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
            if (modelFieldName.equals(value)) {
                return true;
            }
        }
        return false;
    }
}
