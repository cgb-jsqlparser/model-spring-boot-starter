package com.cet.eem.model.tool;

import com.cet.eem.common.CommonUtils;
import com.cet.eem.common.ParamUtils;
import com.cet.eem.common.definition.ColumnDef;
import com.cet.eem.common.definition.ModelLabelDef;
import com.cet.eem.common.model.*;
import com.cet.eem.common.parse.JsonTransferUtils;
import com.cet.eem.common.service.RedisService;
import com.cet.eem.model.base.*;
import com.cet.eem.model.feign.ModelDataService;
import com.cet.eem.toolkit.CollectionUtils;
import com.cet.electric.modelservice.common.dto.EnumItem;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import javax.validation.ValidationException;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Author srr
 * @Description
 * @Data Created in ${Date}
 */
@Component
public class ModelServiceUtils {
    public static final String ID = "id";
    private static final Logger logger = LoggerFactory.getLogger(ModelServiceUtils.class);

    /**
     * 模型服务
     */
    private final ModelDataService modelService;
    private final RedisService redisService;

    /**
     * 分页查询最大数量
     */
    private final static int PAGE_MAX_VALUE = 999999999;

    /**
     * 单次写入数据量
     */
    @Value("${cet.eem.model.write-count:5000}")
    private int WRITE_BATCH;

    /**
     * 公共线程池
     */
    @Resource(name = "commonExecutor")
    Executor executor;

    public ModelServiceUtils(ModelDataService modelService, RedisService redisService) {
        this.modelService = modelService;
        this.redisService = redisService;
    }

    /**
     * 查询单个模型数据
     *
     * @param modelIds   模型id
     * @param modelLabel 模型label
     * @param filter     过滤条件
     * @param orders     排序条件
     * @param page       分页
     * @param clazz      结果类型
     * @param <T>
     * @return
     */
    public <T> List<T> querySingleModel(List<Long> modelIds, String modelLabel, List<ConditionBlock> filter,
                                        List<Order> orders, Page page, Class<T> clazz) {
        QueryCondition condition = new QueryCondition();
        condition.setRootID(0L);
        condition.setRootLabel(modelLabel);
        FlatQueryConditionDTO rootCondition = new FlatQueryConditionDTO();
        condition.setRootCondition(rootCondition);
        List<ConditionBlock> filters = new ArrayList<>();
        if (modelIds != null && !modelIds.isEmpty()) {
            filters.add(new ConditionBlock(ID, ConditionBlock.OPERATOR_IN, modelIds));
        }
        if (filter != null && !filter.isEmpty()) {
            filters.addAll(filter);
        }
        if (orders != null && !orders.isEmpty()) {
            rootCondition.setOrders(orders);
        }
        if (page != null) {
            rootCondition.setPage(page);
        }
        ConditionBlockCompose filterCondition = new ConditionBlockCompose(filters);
        rootCondition.setFilter(filterCondition);
        ResultWithTotal<List<Map<String, Object>>> result = modelService.query(condition);
        ParamUtils.checkResultGeneric(result);
        return JsonTransferUtils.transferList(result.getData(), clazz);
    }

    /**
     * 查询单个模型数据-或查询（可选）
     *
     * @param modelIds   模型id
     * @param modelLabel 模型label
     * @param filter     过滤条件
     * @param orders     排序条件
     * @param page       分页
     * @param clazz      结果类型
     * @param <T>
     * @return
     */
    public <T> List<T> querySingleModelOr(List<Long> modelIds, String modelLabel, List<ConditionBlock> filter,
                                          List<Order> orders, Page page, boolean composemethod, Class<T> clazz) {
        QueryCondition condition = new QueryCondition();
        condition.setRootID(0L);
        condition.setRootLabel(modelLabel);
        FlatQueryConditionDTO rootCondition = new FlatQueryConditionDTO();
        condition.setRootCondition(rootCondition);
        List<ConditionBlock> filters = new ArrayList<>();
        if (modelIds != null && !modelIds.isEmpty()) {
            filters.add(new ConditionBlock(ID, ConditionBlock.OPERATOR_IN, modelIds));
        }
        if (filter != null && !filter.isEmpty()) {
            filters.addAll(filter);
        }
        if (orders != null && !orders.isEmpty()) {
            rootCondition.setOrders(orders);
        }
        if (page != null) {
            rootCondition.setPage(page);
        }
        ConditionBlockCompose filterCondition = new ConditionBlockCompose(filters);
        filterCondition.setComposemethod(composemethod);
        rootCondition.setFilter(filterCondition);
        ResultWithTotal<List<Map<String, Object>>> result = modelService.query(condition);
        ParamUtils.checkResultGeneric(result);
        return JsonTransferUtils.transferList(result.getData(), clazz);
    }

    /**
     * 返回_model树
     *
     * @param parentIds     父节点的id，可以为空
     * @param parentLabel   父节点的modelLabel，不可为空
     * @param filters       父层级的过滤条件
     * @param subConditions 自层级的过滤条件
     * @return 查询结果
     */
    public List<Map<String, Object>> getRelations(List<Long> parentIds, String parentLabel, List<ConditionBlock> filters,
                                                  List<SingleModelConditionDTO> subConditions) {
        return queryData(parentIds, parentLabel, filters, null, false, subConditions, false);
    }

    /**
     * 返回_model树
     *
     * @param parentIds     父节点的id，可以为空
     * @param parentLabel   父节点的modelLabel，不可为空
     * @param filters       父层级的过滤条件
     * @param subConditions 自层级的过滤条件
     * @return 查询结果
     */
    public List<Map<String, Object>> queryData(List<Long> parentIds, String parentLabel, List<ConditionBlock> filters,
                                               List<Order> orderList, boolean composeMethod, List<SingleModelConditionDTO> subConditions,
                                               boolean queryTree) {
        QueryCondition queryCondition = new QueryCondition();
        queryCondition.setRootLabel(parentLabel);
        queryCondition.setTreeReturnEnable(queryTree);

        // 拼接父级过滤条件
        if (CollectionUtils.isNotEmpty(parentIds)) {
            if (CollectionUtils.isEmpty(filters)) {
                filters = new ArrayList<>();
            }

            if (parentIds.size() == 1) {
                filters.add(new ConditionBlock(ID, ConditionBlock.OPERATOR_EQ, parentIds.get(0)));
            } else {
                filters.add(new ConditionBlock(ID, ConditionBlock.OPERATOR_IN, parentIds));
            }
        }

        FlatQueryConditionDTO rootCondition = queryCondition.getRootCondition();
        if (rootCondition == null) {
            rootCondition = new FlatQueryConditionDTO();
        }
        queryCondition.setRootCondition(rootCondition);
        if (rootCondition.getPage() == null) {
            Page page = new Page(0, PAGE_MAX_VALUE);
            rootCondition.setPage(page);
        }

        if (CollectionUtils.isNotEmpty(filters)) {
            queryCondition.setRootCondition(rootCondition);
            rootCondition.setFilter(new ConditionBlockCompose(filters, composeMethod));
        }

        if (CollectionUtils.isNotEmpty(orderList)) {
            rootCondition.setOrders(orderList);
            queryCondition.setRootCondition(rootCondition);
        }

        // 拼接子层级的过滤条件
        if (CollectionUtils.isNotEmpty(subConditions)) {
            queryCondition.setSubLayerConditions(subConditions);
        }
        ResultWithTotal<List<Map<String, Object>>> result = modelService.query(queryCondition);
        ParamUtils.checkResultGeneric(result, queryCondition);
        return result.getData();
    }

    /**
     * 查询节点，并带上子层级节点的信息
     *
     * @param parentIds   父层级节点id列表，非必传
     * @param parentLabel 父层级的modelLabel，必传
     * @param filters     父层级的过滤条件
     * @param subNodes    子层级节点
     * @return 查询出来的节点列表
     */
    public List<Map<String, Object>> query(List<Long> parentIds, String parentLabel, List<ConditionBlock> filters,
                                           boolean composeMethod, List<String> subNodes) {
        List<SingleModelConditionDTO> subConditions = null;
        if (CollectionUtils.isNotEmpty(subNodes)) {
            subConditions = new ArrayList<>();
            for (String subNode : subNodes) {
                subConditions.add(new SingleModelConditionDTO(subNode));
            }
        }

        return queryData(parentIds, parentLabel, filters, null, composeMethod, subConditions, false);
    }

    public List<Map<String, Object>> query(List<Long> parentIds, String parentLabel, List<ConditionBlock> filters,
                                           boolean composeMethod, List<String> subNodes, boolean queryTree) {
        List<SingleModelConditionDTO> subConditions = null;
        if (CollectionUtils.isNotEmpty(subNodes)) {
            subConditions = new ArrayList<>();
            for (String subNode : subNodes) {
                subConditions.add(new SingleModelConditionDTO(subNode));
            }
        }

        return queryData(parentIds, parentLabel, filters, null, composeMethod, subConditions, queryTree);
    }

    /**
     * 查询单条记录
     *
     * @param id
     * @param modelLabel
     * @return
     */
    public <T> List<T> query(Long id, String modelLabel, Class<T> clazz) {
        QueryCondition queryCondition = new QueryCondition();
        queryCondition.setRootLabel(modelLabel);
        queryCondition.setRootID(id);

        FlatQueryConditionDTO rootCondition = new FlatQueryConditionDTO();
        rootCondition.setPage(new Page(0, 999999999));
        queryCondition.setRootCondition(rootCondition);

        ResultWithTotal<List<Map<String, Object>>> result = modelService.query(queryCondition);
        ParamUtils.checkResultGeneric(result, queryCondition);
        return JsonTransferUtils.transferList(result.getData(), clazz);
    }

    /**
     * 查询单挑记录
     *
     * @param id         记录id
     * @param modelLabel 模型标识
     * @return 记录
     */
    public Map<String, Object> query(Long id, String modelLabel) {
        QueryCondition queryCondition = new QueryCondition();
        queryCondition.setRootLabel(modelLabel);
        queryCondition.setRootID(id);

        FlatQueryConditionDTO rootCondition = new FlatQueryConditionDTO();
        rootCondition.setPage(new Page(0, 999999999));
        queryCondition.setRootCondition(rootCondition);

        ResultWithTotal<List<Map<String, Object>>> result = modelService.query(queryCondition);
        ParamUtils.checkResultGeneric(result, queryCondition);
        List<Map<String, Object>> data = (List<Map<String, Object>>) result.getData();
        if (CollectionUtils.isNotEmpty(data)) {
            return data.get(0);
        }

        return null;
    }

    /**
     * 查询单条记录
     *
     * @param ids
     * @param modelLabel
     * @return
     */
    public <T> List<T> query(List<Long> ids, String modelLabel, Class<T> clazz) {
        QueryCondition queryCondition = new QueryCondition();
        queryCondition.setRootLabel(modelLabel);

        FlatQueryConditionDTO rootQueryCondition = new FlatQueryConditionDTO();
        rootQueryCondition.setPage(new Page(0, 999999999));
        queryCondition.setRootCondition(rootQueryCondition);

        if (CollectionUtils.isNotEmpty(ids)) {
            List<ConditionBlock> filters = new ArrayList<>();
            filters.add(new ConditionBlock("id", ConditionBlock.OPERATOR_IN, ids));
            rootQueryCondition.setFilter(new ConditionBlockCompose(filters));
        }

        ResultWithTotal<List<Map<String, Object>>> result = modelService.query(queryCondition);
        ParamUtils.checkResultGeneric(result, queryCondition);
        return JsonTransferUtils.transferList(result.getData(), clazz);
    }

    /**
     * 查询节点，并带上子层级节点的信息
     *
     * @param parentIds     父层级节点id列表，非必传
     * @param parentLabel   父层级的modelLabel，必传
     * @param filters       父层级的过滤条件
     * @param composeMethod 父层级的过滤条件的组装方式
     * @param subNodes      子层级节点
     * @param clazz         需要转换的数据类型
     * @param <T>           返回值类型
     * @return 查询出来的节点列表
     */
    public <T> List<T> query(List<Long> parentIds, String parentLabel, List<ConditionBlock> filters,
                             Boolean composeMethod, List<String> subNodes, Class<T> clazz) {
        List<Map<String, Object>> list = query(parentIds, parentLabel, filters, composeMethod, subNodes);
        return JsonTransferUtils.transferList(list, clazz);
    }

    /**
     * 查询节点，并带上子层级节点的信息
     *
     * @param parentIds     父层级节点id列表，非必传
     * @param parentLabel   父层级的modelLabel，必传
     * @param filters       父层级的过滤条件
     * @param composeMethod 父层级的过滤条件的组装方式
     * @param subNodes      子层级节点
     * @param clazz         需要转换的数据类型
     * @param <T>           返回值类型
     * @return 查询出来的节点列表
     */
    public <T> List<T> queryData(List<Long> parentIds, String parentLabel, List<ConditionBlock> filters,
                                 Boolean composeMethod, List<SingleModelConditionDTO> subNodes, Class<T> clazz) {
        List<Map<String, Object>> list = queryData(parentIds, parentLabel, filters, null, composeMethod, subNodes, false);
        return JsonTransferUtils.transferList(list, clazz);
    }

    /**
     * 多节点关联查询--返回children树
     *
     * @param parentIds     父id
     * @param parentLabel   父label
     * @param filters       父条件
     * @param subConditions 子查询条件
     * @return
     */
    public List<Map<String, Object>> getRelationTree(List<Long> parentIds, String parentLabel, List<ConditionBlock> filters,
                                                     List<SingleModelConditionDTO> subConditions) {
        QueryCondition queryCondition = new QueryCondition();
        queryCondition.setRootLabel(parentLabel);

        // 拼接父级过滤条件
        if (CollectionUtils.isNotEmpty(parentIds)) {
            if (CollectionUtils.isEmpty(filters)) {
                filters = new ArrayList<>();
            }
            filters.add(new ConditionBlock(ID, ConditionBlock.OPERATOR_IN, parentIds));
        }

        if (CollectionUtils.isNotEmpty(filters)) {
            FlatQueryConditionDTO rootCondition = new FlatQueryConditionDTO();
            queryCondition.setRootCondition(rootCondition);
            rootCondition.setFilter(new ConditionBlockCompose(filters));
        }

        // 拼接子层级的过滤条件
        if (CollectionUtils.isNotEmpty(subConditions)) {
            queryCondition.setSubLayerConditions(subConditions);
        }
        queryCondition.setTreeReturnEnable(true);
        ResultWithTotal<List<Map<String, Object>>> result = modelService.query(queryCondition);
        ParamUtils.checkResultGeneric(result);
        return result.getData();
    }

    /**
     * 多节点关联十项全能
     *
     * @param parentIds        父id
     * @param parentLabel      父label
     * @param filters          父条件
     * @param orders
     * @param page
     * @param composemethod
     * @param subConditions    子查询条件
     * @param treeReturnEnable
     * @return
     */
    public List<Map<String, Object>> getRelationships(List<Long> parentIds, String parentLabel, List<ConditionBlock> filters, List<Order> orders,
                                                      Page page, boolean composemethod, List<SingleModelConditionDTO> subConditions, boolean treeReturnEnable) {
        QueryCondition queryCondition = new QueryCondition();
        queryCondition.setRootLabel(parentLabel);
        FlatQueryConditionDTO rootCondition = new FlatQueryConditionDTO();

        // 拼接父级过滤条件
        if (CollectionUtils.isNotEmpty(parentIds)) {
            if (CollectionUtils.isEmpty(filters)) {
                filters = new ArrayList<>();
            }
            filters.add(new ConditionBlock(ID, ConditionBlock.OPERATOR_IN, parentIds));
        }

        if (CollectionUtils.isNotEmpty(filters)) {
            ConditionBlockCompose filterCondition = new ConditionBlockCompose(filters);
            filterCondition.setComposemethod(composemethod);
            rootCondition.setFilter(filterCondition);
            queryCondition.setRootCondition(rootCondition);
        }

        if (CollectionUtils.isNotEmpty(orders)) {
            rootCondition.setOrders(orders);
        }

        if (page != null) {
            rootCondition.setPage(page);
        }

        queryCondition.setTreeReturnEnable(treeReturnEnable);
        // 拼接子层级的过滤条件
        if (CollectionUtils.isNotEmpty(subConditions)) {
            queryCondition.setSubLayerConditions(subConditions);
        }
        ResultWithTotal<List<Map<String, Object>>> result = modelService.query(queryCondition);
        ParamUtils.checkResultGeneric(result);
        return result.getData();
    }

    /**
     * 查询节点，并带上子层级节点的信息
     *
     * @param parentIds   父层级节点id列表，非必传
     * @param parentLabel 父层级的modelLabel，必传
     * @param filters     父层级的过滤条件
     * @param subNodes    子层级节点
     * @param clazz       需要转换的数据类型
     * @param <T>         返回值类型
     * @return 查询出来的节点列表
     */
    public <T> List<T> queryWithChildren(List<Long> parentIds, String parentLabel, List<ConditionBlock> filters,
                                         List<String> subNodes, Class<T> clazz) {
        List<Map<String, Object>> list = queryWithChildren(parentIds, parentLabel, filters, subNodes);
        return JsonTransferUtils.transferList(list, clazz);
    }

    /**
     * 查询节点，并带上子层级节点的信息
     *
     * @param parentIds   父层级节点id列表，非必传
     * @param parentLabel 父层级的modelLabel，必传
     * @param filters     父层级的过滤条件
     * @param subNodes    子层级节点
     * @return 查询出来的节点列表
     */
    public List<Map<String, Object>> queryWithChildren(List<Long> parentIds, String parentLabel, List<ConditionBlock> filters,
                                                       List<String> subNodes) {
        return query(parentIds, parentLabel, filters, false, subNodes);
    }

    public List<Map<String, Object>> queryWithChildren(List<Long> parentIds, String parentLabel, List<ConditionBlock> filters,
                                                       List<String> subNodes, boolean queryTree) {
        return query(parentIds, parentLabel, filters, false, subNodes, queryTree);
    }

    /**
     * 查询节点，并带上子层级节点的信息-返回_model
     *
     * @param parentIds   父层级节点id列表，非必传
     * @param parentLabel 父层级的modelLabel，必传
     * @param filters     父层级的过滤条件
     * @param subLayers   子层级
     * @param clazz       需要转换的数据类型
     * @param <T>         返回值类型
     * @return 查询出来的节点列表
     */
    public <T> List<T> queryWithChildrenCondition(List<Long> parentIds, String parentLabel, List<ConditionBlock> filters,
                                                  List<SingleModelConditionDTO> subLayers, Class<T> clazz) {
        List<Map<String, Object>> list = getRelations(parentIds, parentLabel, filters, subLayers);
        return JsonTransferUtils.transferList(list, clazz);
    }

    /**
     * 查询节点，并带上子层级节点的信息-返回children
     *
     * @param parentIds   父层级节点id列表，非必传
     * @param parentLabel 父层级的modelLabel，必传
     * @param filters     父层级的过滤条件
     * @param subLayers   子层级
     * @param clazz       需要转换的数据类型
     * @param <T>         返回值类型
     * @return 查询出来的节点列表
     */
    public <T> List<T> queryWithChildrenConditionTree(List<Long> parentIds, String parentLabel, List<ConditionBlock> filters,
                                                      List<SingleModelConditionDTO> subLayers, Class<T> clazz) {
        List<Map<String, Object>> list = getRelationTree(parentIds, parentLabel, filters, subLayers);
        return JsonTransferUtils.transferList(list, clazz);
    }

    /**
     * 查询节点，并带上子层级节点的信息-返回children
     *
     * @param parentIds        父层级节点id列表，非必传
     * @param parentLabel      父层级的modelLabel，必传
     * @param filters          父层级的过滤条件
     * @param orders           排序条件
     * @param page             分页
     * @param composemethod
     * @param subConditions    子层级
     * @param treeReturnEnable
     * @param clazz            需要转换的数据类型
     * @param <T>              返回值类型
     * @return 查询出来的节点列表
     */
    public <T> List<T> queryWithChildrenConditionOrderTree(List<Long> parentIds, String parentLabel, List<ConditionBlock> filters,
                                                           List<Order> orders, Page page, boolean composemethod, List<SingleModelConditionDTO> subConditions, boolean treeReturnEnable, Class<T> clazz) {
        List<Map<String, Object>> list = getRelationships(parentIds, parentLabel, filters, orders, page, composemethod, subConditions, treeReturnEnable);
        return JsonTransferUtils.transferList(list, clazz);
    }

    /**
     * 根据label查询枚举
     *
     * @param label modelLabel
     * @return 枚举数据
     */
    public Map<Integer, String> getEnumByLabel(String label) {
        String redisKey = redisService.getRedisKey("enum", label);
        Map<Object, Object> set = redisService.getHashSet(redisKey);
        if (set != null && set.size() > 0) {
            Map<Integer, String> result = new HashMap<>(set.size());
            for (Object key : set.keySet()) {
                result.put(CommonUtils.parseInteger(key), (String) set.get(key));
            }
            return result;
        }

        Result<List<EnumItem>> tmpResult = modelService.getEnumrationByModel(label);
        ParamUtils.checkResultGeneric(tmpResult);
        List<EnumItem> tmpList = tmpResult.getData();
        Map<Integer, String> result = tmpList.stream().collect(Collectors.toMap(EnumItem::getId, EnumItem::getText));

        Map<String, String> redisResult = new HashMap<>(result.size());
        for (Integer key : result.keySet()) {
            redisResult.put(key.toString(), result.get(key));
        }
        redisService.addHashSet(redisKey, redisResult, 1, TimeUnit.DAYS);
        return result;
    }

    public Map<String, Integer> queryEnumTextAndId(String label) {
        if (StringUtils.isBlank(label)) {
            return Collections.emptyMap();
        }

        Result<List<EnumItem>> result = modelService.getEnumrationByModel(label);
        ParamUtils.checkResultGeneric(result);
        List<EnumItem> tmpList = result.getData();
        return tmpList.stream().collect(Collectors.toMap(EnumItem::getText, EnumItem::getId));
    }

    /**
     * 根据label查询枚举
     *
     * @param label modelLabel
     * @return 枚举数据
     */
    public Map<Integer, String> getEnumPropertyByLabel(String label) {
        Result<List<EnumItem>> result = modelService.getEnumrationByModel(label);
        ParamUtils.checkResultGeneric(result);
        List<EnumItem> tmpList = result.getData();
        return tmpList.stream().collect(Collectors.toMap(EnumItem::getId, EnumItem::getPropertyLabel));
    }

    /**
     * 根据label查询枚举
     *
     * @param label modelLabel
     * @return 枚举数据
     */
    public Map<Integer, EnumItem> getEnumeration(String label) {
        Result<List<EnumItem>> result = modelService.getEnumrationByModel(label);
        ParamUtils.checkResultGeneric(result);
        List<EnumItem> tmpList = result.getData();
        return tmpList.stream().collect(Collectors.toMap(EnumItem::getId, it -> it));
    }

    /**
     * 写入数据
     *
     * @param data  需要写入的数据
     * @param clazz 需要转换的数据类型
     * @param <T>   数据类型
     * @return 写入成功后返回的数据
     */
    public <T> List<T> writeData(T data, Class<T> clazz) {
        if (data == null) {
            return new ArrayList<>();
        }

        Result<Object> result = modelService.write(Stream.of(data).collect(Collectors.toList()));
        ParamUtils.checkResultGeneric(result);
        return JsonTransferUtils.transferList((List) result.getData(), clazz);
    }

    /**
     * 写入数据
     *
     * @param data  需要写入的数据
     * @param clazz 需要转换的数据类型
     * @param <T>   数据类型
     * @return 写入成功后返回的数据
     */
    public <T> List<T> writeData(List<T> data, Class<T> clazz) {
        if (CollectionUtils.isEmpty(data)) {
            return new ArrayList<>();
        }

        Result<Object> result = modelService.write(data);
        ParamUtils.checkResultGeneric(result);
        if (clazz == null) {
            return (List<T>) result.getData();
        }

        return JsonTransferUtils.transferList((List) result.getData(), clazz);
    }

    /**
     * 写入数据
     *
     * @param data 需要写入的数据
     * @param <T>  数据类型
     * @return 写入成功后返回的数据
     */
    public <T> Result<Object> writeData(List<T> data) {
        if (CollectionUtils.isEmpty(data)) {
            return new Result<>();
        }

        Result<Object> result = modelService.write(data);
        ParamUtils.checkResultGeneric(result);
        return result;
    }

    /**
     * 批量写入数据
     *
     * @param data  需要写入的数据
     * @param clazz 需要转换的数据类型
     * @param <T>   数据类型
     */
    public <T> void writeDataBatch(List<T> data, Class<T> clazz) {
        if (CollectionUtils.isEmpty(data)) {
            return;
        }

        int count = data.size();

        int n = 0;
        int total = count / WRITE_BATCH;
        total = (count % WRITE_BATCH) == 0 ? total : (total + 1);
        while (n < total) {
            int end = Math.min((n + 1) * WRITE_BATCH, count);
            writeData(data.subList(n * WRITE_BATCH, end), clazz);

            n++;
        }
    }

    /**
     * 批量写入数据
     *
     * @param modelLabel     模型标识
     * @param insert         新增或者更新
     * @param writeFields    需要写入数据的字段
     * @param writeDataList  与需要写入数据字段对应的数据
     * @param filterFields   需要过滤的字段
     * @param filterDataList 过滤字段对应的数据
     * @return 写入结果
     */
    public ModelSingeWriteVo writeDataBatch(@NotEmpty String modelLabel, boolean insert, @NotNull List<String> writeFields, @NotNull List<List<Object>> writeDataList,
                                            List<String> filterFields, List<List<Object>> filterDataList) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        ModelSingeWriteVo modelSingeWriteVo = new ModelSingeWriteVo();
        modelSingeWriteVo.setModelLabel(modelLabel);
        modelSingeWriteVo.setWriteMethod(insert ? ModelSingeWriteVo.INSERT : ModelSingeWriteVo.UPDATE);

        if (!insert && (CollectionUtils.isEmpty(filterFields) || CollectionUtils.isEmpty(filterDataList))) {
            throw new ValidationException("当更新数据时，过滤字段和过滤字段对应的数据均不得为空！");
        }

        int count = writeDataList.size();
        int total = count / WRITE_BATCH;
        int n = 0;
        total = (count % WRITE_BATCH) == 0 ? total : (total + 1);

        ModelSingeWriteVo result = new ModelSingeWriteVo();
        while (n < total) {
            int end = Math.min((n + 1) * WRITE_BATCH, count);
            modelSingeWriteVo.setWriteProperty(writeFields);
            modelSingeWriteVo.setWriteData(writeDataList.subList(n * WRITE_BATCH, end));

            if (!insert) {
                modelSingeWriteVo.setFilter(filterFields);
                modelSingeWriteVo.setFilterData(filterDataList.subList(n * WRITE_BATCH, end));
            }

            Result<ModelSingeWriteVo> tmpResult = modelService.write(modelSingeWriteVo);
            ParamUtils.checkResultGeneric(tmpResult, modelSingeWriteVo);
            if (n == 0) {
                result = tmpResult.getData();
            } else {
                ModelSingeWriteVo tmpObj = tmpResult.getData();
                result.getFilterData().addAll(tmpObj.getFilterData());
                result.getWriteData().addAll(tmpObj.getWriteData());
            }
            n++;
        }

        stopWatch.stop();
        if (insert) {
            logger.info(String.format("批量写入数据耗时%s，写入数据为：modeLabel=%s, insert=%s, writeFields=%s, writeDataList=%s", stopWatch.getLastTaskTimeMillis(),
                    modelLabel, insert, JsonTransferUtils.toJSONString(writeFields), JsonTransferUtils.toJSONString(writeDataList)));
        } else {
            logger.info(String.format("批量写入数据耗时%s，写入数据为：modeLabel=%s, insert=%s, writeFields=%s, writeDataList=%s, filterFields=%s, filterDataList=%s",
                    stopWatch.getLastTaskTimeMillis(), modelLabel, insert, JsonTransferUtils.toJSONString(writeFields),
                    JsonTransferUtils.toJSONString(writeDataList), JsonTransferUtils.toJSONString(filterFields), JsonTransferUtils.toJSONString(filterDataList)));
        }

        return result;
    }

    /**
     * 转换结果
     *
     * @param modelSingeWriteVo
     * @return
     */
    public List<Map<String, Object>> convertModelSingeWrite(ModelSingeWriteVo modelSingeWriteVo) {
        if (modelSingeWriteVo == null) {
            return Collections.emptyList();
        }

        int n = modelSingeWriteVo.getFilterData().size();
        List<Map<String, Object>> result = new ArrayList<>();
        List<String> filterFields = modelSingeWriteVo.getFilter();
        List<String> writeFields = modelSingeWriteVo.getWriteProperty();
        for (int i = 0; i < n; i++) {
            Map<String, Object> obj = new HashMap<>(CommonUtils.MAP_INIT_SIZE_16);
            obj.put(ColumnDef.MODEL_LABEL, modelSingeWriteVo.getModelLabel());

            List<Object> filterDataList = modelSingeWriteVo.getFilterData().get(i);
            for (int j = 0; j < filterFields.size(); j++) {
                obj.put(filterFields.get(j), filterDataList.get(j));
            }

            List<Object> writeDataList = modelSingeWriteVo.getWriteData().get(i);
            for (int j = 0; j < writeFields.size(); j++) {
                obj.put(writeFields.get(j), writeDataList.get(j));
            }

            result.add(obj);
        }

        return result;
    }

    /**
     * 批量写入数据，如果有更新数据那么根据主键标识ID字段来进行更新
     *
     * @param modelLabel    模型标识
     * @param insert        新增或者更新
     * @param writeFields   需要写入数据的字段
     * @param writeDataList 与需要写入数据字段对应的数据
     * @param filterIds     过滤字段对应的数据，如果当前为更新，那么该数据会被忽略掉
     * @return 写入结果
     */
    public ModelSingeWriteVo writeDataBatch(@NotEmpty String modelLabel, boolean insert, @NotNull List<String> writeFields, @NotNull List<List<Object>> writeDataList,
                                            List<List<Object>> filterIds) {
        if (!insert) {
            return writeDataBatch(modelLabel, insert, writeFields, writeDataList, Collections.singletonList(ColumnDef.ID), filterIds);
        }

        return writeDataBatch(modelLabel, insert, writeFields, writeDataList, null, null);
    }

    public <T> List<T> writeDataBatchWithResult(List<T> data, Class<T> clazz) {
        if (CollectionUtils.isEmpty(data)) {
            return new ArrayList<>();
        }

        int count = data.size();
        List<T> result = new ArrayList<>();
        int n = 0;
        int total = count / WRITE_BATCH;
        total = (count % WRITE_BATCH) == 0 ? total : (total + 1);
        while (n < total) {
            int end = Math.min((n + 1) * WRITE_BATCH, count);
            result.addAll(writeData(data.subList(n * WRITE_BATCH, end), clazz));

            n++;
        }

        return result;
    }

    /**
     * 多线程写入数据
     *
     * @param data       需要写入的数据
     * @param clazz      需要反序列化的类
     * @param max        单次写入最大值
     * @param groupCount 多线程最大分组
     * @param <T>
     * @return
     * @throws InterruptedException
     */
    public <T> List<T> writeDataBatchWithResult(List<T> data, Class<T> clazz, int max, int groupCount) throws Exception {
        if (CollectionUtils.isEmpty(data)) {
            return new ArrayList<>();
        }

        List<T> result = new ArrayList<>();
        List<List<T>> lists = groupData(data, groupCount);
        CountDownLatch latch = new CountDownLatch(lists.size());
        ArrayList<T>[] results = new ArrayList[lists.size()];
        List<Boolean> existException = new ArrayList<>();

        for (int i = 0; i < lists.size(); i++) {
            int finalI = i;
            results[finalI] = new ArrayList<>();
            executor.execute(() -> {
                HystrixRequestContext.initializeContext();
                try {
                    List<T> list = lists.get(finalI);
                    int count = list.size();
                    int n = 0;
                    int total = count / max;
                    total = (count % max) == 0 ? total : (total + 1);
                    while (n < total) {
                        int end = Math.min((n + 1) * max, count);
                        results[finalI].addAll(writeData(list.subList(n * max, end), clazz));

                        n++;
                    }
                } catch (Exception e) {
                    logger.error("批量写入数据失败" + e);
                    existException.add(true);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        if (existException.size() > 0) {
            throw new Exception("多线程批量写入数据失败！");
        }

        for (ArrayList<T> array : results) {
            result.addAll(array);
        }

        return result;
    }

    /**
     * 将批量写入的数据进行分组，方便进一步多线程的处理
     *
     * @param data
     * @param groupCount
     * @param <T>
     * @return
     */
    public <T> List<List<T>> groupData(List<T> data, int groupCount) {
        List<List<T>> result = new ArrayList<>();
        int count = data.size() / groupCount;
        List<T> tmpData = new ArrayList<>();
        if (count == 0) {
            result.add(data);
            return result;
        }
        for (int i = 0; i < data.size(); i++) {
            tmpData.add(data.get(i));
            if (tmpData.size() % count == 0) {
                result.add(tmpData);
                tmpData = new ArrayList<>();
            }
        }
        if (CollectionUtils.isNotEmpty(tmpData)) {
            result.add(tmpData);
        }

        return result;
    }

    /**
     * 删除数据
     *
     * @param modelLabel 表名
     * @param ids        要删除的id集合
     */
    public void delete(String modelLabel, List<Long> ids) {
        if (CollectionUtils.isEmpty(ids) || StringUtils.isBlank(modelLabel)) {
            return;
        }

        Result<Object> result = modelService.delete(modelLabel, ids);
        ParamUtils.checkResultGeneric(result);
    }

    /**
     * 根据modelLabel删除数据
     *
     * @param modelLabel
     */
    public void delete(String modelLabel) {
        delete((List<Long>) null, modelLabel, null);
    }

    public void delete(List<Map<String, Object>> nodes) {
        if (CollectionUtils.isEmpty(nodes)) {
            return;
        }

        List<BaseVo> deleteNodes = JsonTransferUtils.transferList(nodes, BaseVo.class);
        Map<String, List<BaseVo>> map = deleteNodes.stream().collect(Collectors.groupingBy(BaseVo::getModelLabel));
        map.forEach((label, val) -> {
            delete(label, val.stream().map(BaseVo::getId).collect(Collectors.toList()));
        });

    }

    public void delete(Long parentId, String parentLabel, List<String> subNodes) {
        delete(Stream.of(parentId).collect(Collectors.toList()), parentLabel, subNodes);
    }

    public void delete(List<Long> parentIds, String parentLabel, List<String> subNodes) {
        QueryCondition queryCondition = new QueryCondition();
        queryCondition.setRootLabel(parentLabel);
        queryCondition.setTreeReturnEnable(true);

        // 拼接父级过滤条件
        if (CollectionUtils.isNotEmpty(parentIds)) {
            FlatQueryConditionDTO rootCondition = new FlatQueryConditionDTO();
            List<ConditionBlock> filters = new ArrayList<>();
            filters.add(new ConditionBlock(ID, ConditionBlock.OPERATOR_IN, parentIds));
            rootCondition.setFilter(new ConditionBlockCompose(filters));
            queryCondition.setRootCondition(rootCondition);
        }

        // 拼接子层级的过滤条件
        if (CollectionUtils.isNotEmpty(subNodes)) {
            List<SingleModelConditionDTO> subConditions = new ArrayList<>();
            queryCondition.setSubLayerConditions(subConditions);
            subNodes.forEach(it -> {
                subConditions.add(new SingleModelConditionDTO(it));
            });
        }

        ResultWithTotal<List<Map<String, Object>>> result = modelService.query(queryCondition);
        ParamUtils.checkResultGeneric(result, queryCondition);

        List<Map<String, Object>> maps = result.getData();
        Map<String, List<Long>> nodeList = new HashMap<>(CommonUtils.MAP_INIT_SIZE_16);
        resolveDeleteIds(maps, nodeList);
        nodeList.forEach(this::delete);
    }

    private void resolveDeleteIds(List<Map<String, Object>> maps, Map<String, List<Long>> nodeList) {
        if (CollectionUtils.isEmpty(maps)) {
            return;
        }

        long id;
        String modelLabel;
        List<Map<String, Object>> children;
        List<Long> ids;
        for (Map<String, Object> node : maps) {
            id = CommonUtils.parseLong(node.get("id"));
            modelLabel = (String) node.get("modelLabel");
            ids = nodeList.get(modelLabel);
            if (CollectionUtils.isEmpty(ids)) {
                ids = new ArrayList<>();
            }
            ids.add(id);
            nodeList.put(modelLabel, ids);

            children = (List<Map<String, Object>>) node.get("children");
            if (CollectionUtils.isNotEmpty(children)) {
                resolveDeleteIds(children, nodeList);
            }
        }
    }

    /**
     * 写入两条记录的关系
     *
     * @param nodeId1     节点1的id
     * @param modelLabel1 节点2的modelLabel
     * @param nodeId2     节点2的id
     * @param modelLabel2 节点2的modelLabel
     */
    public void writeRelations(long nodeId1, String modelLabel1, long nodeId2, String modelLabel2) {
        Map<String, Object> parentNode = new HashMap<>(CommonUtils.MAP_INIT_SIZE_4);
        parentNode.put(ColumnDef.ID, nodeId1);
        parentNode.put(ColumnDef.MODEL_LABEL, modelLabel1);

        Map<String, Object> childNode = new HashMap<>(CommonUtils.MAP_INIT_SIZE_4);
        childNode.put(ColumnDef.ID, nodeId2);
        childNode.put(ColumnDef.MODEL_LABEL, modelLabel2);
        parentNode.put(modelLabel2 + ModelLabelDef.PREFIX, Stream.of(childNode).collect(Collectors.toList()));
        Result<Object> result = modelService.write(Stream.of(parentNode).collect(Collectors.toList()));
        ParamUtils.checkResultGeneric(result);
    }

    public void writeRelations(BaseVo parentNode, List<BaseVo> childNode) {
        if (CollectionUtils.isEmpty(childNode)) {
            return;
        }

        Map<String, Object> parentNodeWrite = new HashMap<>(CommonUtils.MAP_INIT_SIZE_4);
        parentNodeWrite.put(ColumnDef.ID, parentNode.getId());
        parentNodeWrite.put(ColumnDef.MODEL_LABEL, parentNode.getModelLabel());

        Map<String, List<BaseVo>> map = childNode.stream().collect(Collectors.groupingBy(BaseVo::getModelLabel));

        map.forEach((label, val) -> {
            List<Map<String, Object>> childNodeWriteList = new ArrayList<>();
            val.forEach(it -> {
                Map<String, Object> childNodeWrite = new HashMap<>(CommonUtils.MAP_INIT_SIZE_4);
                childNodeWrite.put(ColumnDef.ID, it.getId());
                childNodeWrite.put(ColumnDef.MODEL_LABEL, it.getModelLabel());
                childNodeWriteList.add(childNodeWrite);
            });
            parentNodeWrite.put(label + ModelLabelDef.PREFIX, childNodeWriteList);
        });

        Result<Object> result = modelService.write(Stream.of(parentNodeWrite).collect(Collectors.toList()));
        ParamUtils.checkResultGeneric(result);
    }

    /**
     * 查询数据
     *
     * @param condition
     * @param clazz
     * @param <T>
     * @return
     */
    public <T extends BaseVo> List<T> query(QueryCondition condition, Class<T> clazz) {
        ResultWithTotal<List<Map<String, Object>>> tmpResult = modelService.query(condition);
        ParamUtils.checkResultGeneric(tmpResult);
        return JsonTransferUtils.transferList(tmpResult.getData(), clazz);
    }

    /**
     * 查询数据
     *
     * @param condition
     * @return
     */
    public List<Map<String, Object>> query(QueryCondition condition) {
        ResultWithTotal<List<Map<String, Object>>> tmpResult = modelService.query(condition);
        ParamUtils.checkResultGeneric(tmpResult);
        return tmpResult.getData();
    }

    /**
     * 查询数据
     *
     * @param condition
     * @return
     */
    public ResultWithTotal<List<Map<String, Object>>> queryWithTotal(QueryCondition condition) {
        ResultWithTotal<List<Map<String, Object>>> tmpResult = modelService.query(condition);
        ParamUtils.checkResultGeneric(tmpResult);
        return tmpResult;
    }

    /**
     * 查询数据，子节点以树的形式返回
     *
     * @param rootIds
     * @param rootLabel
     * @param childLabels
     * @return
     */
    public List<Map<String, Object>> queryAsTree(List<Long> rootIds, String rootLabel, List<String> childLabels) {
        QueryCondition condition = new QueryConditionBuilder<>(rootLabel, rootIds)
                .selectChildByLabels(childLabels)
                .queryAsTree()
                .build();

        return query(condition);
    }
}
