package com.cet.eem.dao;

import com.cet.eem.annotation.FieldStrategy;
import com.cet.eem.common.util.JsonUtil;
import com.cet.eem.conditions.Wrapper;
import com.cet.eem.common.constant.ConditionOperator;
import com.cet.eem.common.model.*;
import com.cet.eem.conditions.query.QueryWrapper;
import com.cet.eem.metadata.TableFieldInfo;
import com.cet.eem.metadata.TableInfo;
import com.cet.eem.metadata.TableInfoHelper;
import com.cet.eem.model.feign.ModelDataServiceSubstitution;
import com.cet.eem.model.model.IModel;
import com.cet.eem.toolkit.CollectionUtils;
import com.cet.eem.toolkit.ReflectionKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public class ModelDaoImpl<T extends IModel> implements BaseModelDao<T> {

    @Autowired
    protected ModelDataServiceSubstitution modelDataService;

    private Class<T> modelEntityClass;


    public ModelDaoImpl() {
        this.modelEntityClass = (Class<T>) ReflectionKit.getClassGenericType(getClass(), 0);
    }

    public ModelDaoImpl(Class modelDaoClass, ModelDataServiceSubstitution modelDataService) {
        if (modelDaoClass.isInterface()) {
            this.modelEntityClass = (Class<T>) ReflectionKit.getInterfaceClassGenericType(modelDaoClass, 0);
        } else {
            this.modelEntityClass = (Class<T>) ReflectionKit.getClassGenericType(modelDaoClass, 0);
        }
        this.modelDataService = modelDataService;
    }

    @Override
    public int insert(T entity) {
        Integer id = 0;
        Assert.notNull(entity, "Insert Entity Cannot Be Null");
        Assert.isNull(entity.getId(), "Insert Entity's Id Must Be Null");
        Result<Object> writeResult = modelDataService.write(Collections.singletonList(entity));
        List<Map<String, Object>> data = (List<Map<String, Object>>) writeResult.getData();
        Optional<Map<String, Object>> any = data.stream().findAny();
        if (any.isPresent()) {
            Map<String, Object> map = any.get();
            id = (Integer) map.get("id");
            entity.setId(Long.valueOf(id.toString()));
        }
        return id;
    }

    @Override
    public int deleteById(Long id) {
        Assert.notNull(id, "Delete ID Cannot Be Null");
        String modelLabel = TableInfoHelper.getModelLabel(modelEntityClass);
        modelDataService.deleteById(modelLabel, Collections.singletonList(id));
        return 1;
    }

    @Override
    public int deleteByMap(Map<String, Object> columnMap) {
        String modelLabel = TableInfoHelper.getModelLabel(modelEntityClass);
        QueryCondition.Builder builder = new QueryCondition.Builder(modelLabel);
        if (CollectionUtils.isNotEmpty(columnMap)) {
            for (Map.Entry<String, Object> entry : columnMap.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                builder.where(key, ConditionOperator.EQ, value);
            }
        }
        ResultWithTotal<List<Map<String, Object>>> query = modelDataService.query(builder.build());
        List<Map<String, Object>> data = query.getData();
        List<Long> ids = data.stream().map(s -> Long.valueOf(s.get("id").toString())).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(ids)) {
            modelDataService.deleteById(modelLabel, ids);
        }
        return ids.size();
    }

    @Override
    public int delete(Wrapper<T> wrapper) {
        String modelLabel = TableInfoHelper.getModelLabel(modelEntityClass);
        List<T> ts = selectList(wrapper);
        List<Long> ids = ts.stream().map(IModel::getId).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(ids)) {
            modelDataService.deleteById(modelLabel, ids);
        }
        return ids.size();
    }

    @Override
    public int deleteBatchIds(Collection<Long> idList) {
        String modelLabel = TableInfoHelper.getModelLabel(modelEntityClass);
        if (CollectionUtils.isNotEmpty(idList)) {
            modelDataService.deleteById(modelLabel, idList);
        }
        return idList.size();
    }

    @Override
    public int updateById(T entity) {
        Assert.notNull(entity, "Update Entity Cannot Be Null");
        Assert.notNull(entity.getId(), "Update Entity Id Cannot Be Null");
        modelDataService.write(Collections.singletonList(entity));
        return 1;
    }

    @Override
    public int update(T entity, Wrapper<T> updateWrapper) {
        List<T> ts = selectList(updateWrapper);
        if (CollectionUtils.isEmpty(ts)) {
            return 0;
        }
        TableInfo modelInfo = TableInfoHelper.getModelInfo(modelEntityClass);
        List<TableFieldInfo> fieldList = modelInfo.getFieldList();
        Map<Field, Object> map = new HashMap<>(fieldList.size());
        try {
            for (TableFieldInfo tableFieldInfo : fieldList) {
                Field field = tableFieldInfo.getField();
                field.setAccessible(true);
                Object obj = field.get(entity);
                if (Objects.isNull(obj) && tableFieldInfo.getUpdateStrategy() == FieldStrategy.NULL_EXCLUDE) {
                    continue;
                }
                map.put(field, obj);
            }
            for (T t : ts) {
                for (Map.Entry<Field, Object> fieldObjectEntry : map.entrySet()) {
                    Field key = fieldObjectEntry.getKey();
                    Object value = fieldObjectEntry.getValue();
                    key.set(t, value);
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return this.saveOrUpdateBatch(ts);
    }

    @Override
    public int saveOrUpdateBatch(Collection<T> entityList) {
        if (CollectionUtils.isEmpty(entityList)) {
            return 0;
        }
        Result<Object> saveResult = modelDataService.write(entityList);
        List<Map<String, Object>> data = (List<Map<String, Object>>) saveResult.getData();
        if (entityList.size() == data.size()) {
            Iterator<T> iterator = entityList.iterator();
            int index = 0;
            while (iterator.hasNext()) {
                T next = iterator.next();
                Map<String, Object> map = data.get(index);
                if (Objects.isNull(next.getId())) {
                    Object id = map.get("id");
                    next.setId(Long.valueOf(id.toString()));
                }
                index++;
            }
        }
        return data.size();
    }

    @Override
    public T selectById(Long id) {
        Assert.notNull(id, "Select Id Cannot Be Null");
        String modelLabel = TableInfoHelper.getModelLabel(modelEntityClass);
        QueryCondition.Builder builder = new QueryCondition.Builder(modelLabel);
        builder.filterByPrimaryKey(id);
        ResultWithTotal<List<Map<String, Object>>> query = modelDataService.query(builder.build());
        List<Map<String, Object>> data = query.getData();
        List<T> ts = JsonUtil.mapList2BeanList(data, modelEntityClass);
        return ts.stream().findFirst().orElse(null);
    }

    @Override
    public List<T> selectBatchIds(Collection<Long> idList) {
        if (idList == null) {
            return null;
        }
        if (idList.size() == 0) {
            return Collections.EMPTY_LIST;
        }
        String modelLabel = TableInfoHelper.getModelLabel(modelEntityClass);
        QueryCondition.Builder builder = new QueryCondition.Builder(modelLabel);
        builder.where("id", ConditionOperator.IN, idList);
        ResultWithTotal<List<Map<String, Object>>> query = modelDataService.query(builder.build());
        List<Map<String, Object>> data = query.getData();
        return JsonUtil.mapList2BeanList(data, modelEntityClass);
    }

    @Override
    public List<T> selectByMap(Map<String, Object> columnMap) {
        String modelLabel = TableInfoHelper.getModelLabel(modelEntityClass);
        QueryCondition.Builder builder = new QueryCondition.Builder(modelLabel);
        if (CollectionUtils.isNotEmpty(columnMap)) {
            for (Map.Entry<String, Object> entry : columnMap.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                builder.where(key, ConditionOperator.EQ, value);
            }
        }
        ResultWithTotal<List<Map<String, Object>>> query = modelDataService.query(builder.build());
        List<Map<String, Object>> data = query.getData();
        return JsonUtil.mapList2BeanList(data, modelEntityClass);
    }

    @Override
    public T selectOne(Wrapper<T> queryWrapper) {
        QueryCondition.Builder queryConditionByWrapperWithoutRelation = getQueryConditionByWrapperWithoutRelation(queryWrapper);
        queryConditionByWrapperWithoutRelation.byPage(0, 1);
        ResultWithTotal<List<Map<String, Object>>> query = modelDataService.query(queryConditionByWrapperWithoutRelation.build());
        List<Map<String, Object>> data = query.getData();
        List<T> ts = JsonUtil.mapList2BeanList(data, modelEntityClass);
        if (CollectionUtils.isEmpty(ts)) {
            return null;
        }
        return ts.stream().findFirst().orElse(null);
    }

    @Override
    public Integer selectCount(Wrapper<T> queryWrapper) {
        QueryCondition.Builder queryConditionByWrapperWithoutRelation = getQueryConditionByWrapperWithoutRelation(queryWrapper);
        ResultWithTotal<List<Map<String, Object>>> query = modelDataService.query(queryConditionByWrapperWithoutRelation.build());
        return (int) query.getTotal();
    }

    @Override
    public List<T> selectAll() {
        return selectList(null);
    }

    @Override
    public List<T> selectList(Wrapper<T> queryWrapper) {
        QueryCondition.Builder queryConditionByWrapperWithoutRelation = getQueryConditionByWrapperWithoutRelation(queryWrapper);
        ResultWithTotal<List<Map<String, Object>>> query = modelDataService.query(queryConditionByWrapperWithoutRelation.build());
        List<Map<String, Object>> data = query.getData();
        return JsonUtil.mapList2BeanList(data, modelEntityClass);
    }

    @Override
    public List<Map<String, Object>> selectMaps(Wrapper<T> queryWrapper) {
        QueryCondition.Builder queryConditionByWrapperWithoutRelation = getQueryConditionByWrapperWithoutRelation(queryWrapper);
        ResultWithTotal<List<Map<String, Object>>> query = modelDataService.query(queryConditionByWrapperWithoutRelation.build());
        return query.getData();
    }

    @Override
    public ResultWithTotal<List<T>> selectPage(Wrapper<T> queryWrapper, Page page) {
        QueryCondition.Builder queryConditionByWrapperWithoutRelation = getQueryConditionByWrapperWithoutRelation(queryWrapper);
        queryConditionByWrapperWithoutRelation.setPage(page);
        ResultWithTotal<List<Map<String, Object>>> query = modelDataService.query(queryConditionByWrapperWithoutRelation.build());
        List<Map<String, Object>> data = query.getData();
        return ResultWithTotal.ok(JsonUtil.mapList2BeanList(data, modelEntityClass), query.getTotal());
    }

    @Override
    public ResultWithTotal<List<Map<String, Object>>> selectMapsPage(Wrapper<T> queryWrapper, Page page) {
        QueryCondition.Builder queryConditionByWrapperWithoutRelation = getQueryConditionByWrapperWithoutRelation(queryWrapper);
        queryConditionByWrapperWithoutRelation.setPage(page);
        return modelDataService.query(queryConditionByWrapperWithoutRelation.build());
    }

    @Override
    public <K extends T> K selectRelatedById(Class<K> aClass, Long id) {
        Assert.notNull(id, "selectRelated Id Cannot Be Null");
        TableInfo modelInfo = TableInfoHelper.getModelInfo(aClass);
        String tableName = modelInfo.getTableName();
        QueryCondition.Builder builder = new QueryCondition.Builder(tableName);
        builder.filterByPrimaryKey(id);
        Set<String> ownModelNameList = modelInfo.getOwnModelNameList();
        for (String modelLabel : ownModelNameList) {
            SingleModelConditionDTO build = new SingleModelConditionDTO.Builder(modelLabel).build();
            builder.own(build);
        }
        ResultWithTotal<List<Map<String, Object>>> query = modelDataService.query(builder.build());
        List<K> ks = JsonUtil.mapList2BeanList(query.getData(), aClass);
        return ks.stream().findAny().orElse(null);
    }

    @Override
    public <K extends T> Map<String, Object> selectRelatedTreeById(Class<K> aClass, Long id) {
        Assert.notNull(id, "selectRelated Id Cannot Be Null");
        TableInfo modelInfo = TableInfoHelper.getModelInfo(aClass);
        String tableName = modelInfo.getTableName();
        QueryCondition.Builder builder = new QueryCondition.Builder(tableName);
        builder.filterByPrimaryKey(id);
        builder.tree();
        Set<String> ownModelNameList = modelInfo.getOwnModelNameList();
        for (String modelLabel : ownModelNameList) {
            SingleModelConditionDTO build = new SingleModelConditionDTO.Builder(modelLabel).build();
            builder.own(build);
        }
        ResultWithTotal<List<Map<String, Object>>> query = modelDataService.query(builder.build());
        return query.getData().stream().findAny().orElse(null);
    }

    @Override
    public <K extends T> List<K> selectRelatedList(Class<K> aClass, Wrapper<T> queryWrapper) {
        QueryCondition.Builder queryConditionByWrapper = getQueryConditionByWrapperWithRelation(queryWrapper, aClass);
        ResultWithTotal<List<Map<String, Object>>> query = modelDataService.query(queryConditionByWrapper.build());
        return JsonUtil.mapList2BeanList(query.getData(), aClass);
    }

    @Override
    public List<Map<String, Object>> selectRelatedTreeList(Wrapper<T> queryWrapper) {
        QueryCondition.Builder queryConditionByWrapper = getQueryConditionByWrapperWithRelation(queryWrapper, null);
        queryConditionByWrapper.tree();
        ResultWithTotal<List<Map<String, Object>>> query = modelDataService.query(queryConditionByWrapper.build());
        return query.getData();
    }

    private <K extends T> QueryCondition.Builder getQueryConditionByWrapperWithRelation(Wrapper<T> queryWrapper, Class<K> kClass) {
        QueryCondition.Builder queryConditionByWrapperWithoutRelation = getQueryConditionByWrapperWithoutRelation(queryWrapper);
        TableInfo modelInfo = TableInfoHelper.getModelInfo(modelEntityClass);
        Set<String> ownModelNameList = modelInfo.getOwnModelNameList();
        for (String modelLabel : ownModelNameList) {
            SingleModelConditionDTO build = new SingleModelConditionDTO.Builder(modelLabel).build();
            queryConditionByWrapperWithoutRelation.own(build);
        }
        return queryConditionByWrapperWithoutRelation;
    }

    private QueryCondition.Builder getQueryConditionByWrapperWithoutRelation(Wrapper<T> queryWrapper) {
        QueryCondition.Builder queryConditionBuilder = null;
        if (queryWrapper == null) {
            TableInfo modelInfo = TableInfoHelper.getModelInfo(modelEntityClass);
            queryConditionBuilder = new QueryCondition.Builder(modelInfo.getTableName());
        } else {
            queryConditionBuilder = (QueryCondition.Builder) queryWrapper.getParam().get("QUERY");
        }
        return queryConditionBuilder;
    }
}
