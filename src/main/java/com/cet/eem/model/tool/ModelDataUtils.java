package com.cet.eem.model.tool;

import com.cet.eem.model.base.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 公共类
 *
 * @author CKai
 * @see ModelServiceUtils
 */
@Deprecated
public final class ModelDataUtils {

    public static final int SMS_START_INDEX = 0;

    public static final int SMS_MAX_INDEX = 20;


    /**
     * 获取单个模型条件
     *
     * @param modelLables 模型label
     * @return 条件
     */
    public static List<SingleModelConditionDTO> getSingleModelConditionsByLabels(List<String> modelLables) {
        List<SingleModelConditionDTO> result = new ArrayList<>();
        modelLables.forEach(label -> result.add(getSingleModelConditionByLabel(label)));

        return result;
    }

    /**
     * 根据模型获取单个模型条件
     *
     * @param modelLable 模型label
     * @return 条件
     */
    public static SingleModelConditionDTO getSingleModelConditionByLabel(String modelLable) {
        SingleModelConditionDTO conditionDTO = new SingleModelConditionDTO();
        conditionDTO.modelLabel = modelLable;

        return conditionDTO;
    }

    /**
     * 从map中获取数据
     *
     * @param key   键
     * @param datas 数据列表
     * @param <T>   类型
     * @return 目标数据列表
     */
    public static <T> T getValueFromMap(String key, Map<String, Object> datas) {
        Object value = datas.get(key);

        return value == null ? null : (T) value;
    }

    /**
     * 将时间戳转换成时间
     *
     * @param timestamp 时间戳
     * @return 时间
     */
    public static Date getDateFrom(Long timestamp) {
        return new Date(timestamp);
    }

    /**
     * 从数据字典中获取时间
     *
     * @param key   键
     * @param datas 数据字典
     * @return 时间
     */
    public static Date getDateBy(String key, Map<String, Object> datas) {
        Object value = datas.get(key);
        if (value instanceof Integer) {
            return null;
        }
        return value == null ? null : getDateFrom((Long) value);
    }

    /**
     * 获取按照id查询的条件
     *
     * @param modelLabel 模型label
     * @param ids        id列表
     * @return 条件
     */
    public static SingleModelConditionDTO getSingleModelConditionDTOForIds(
            String modelLabel, List<Integer> ids) {
        SingleModelConditionDTO idCondition = new SingleModelConditionDTO();
        List<ConditionBlock> expressions = new ArrayList<>();
        ConditionBlock conditionBlock = new ConditionBlock();
        conditionBlock.setProp("id");
        conditionBlock.setLimit(ids);
        conditionBlock.setOperator("IN");
        expressions.add(conditionBlock);
        idCondition.filter = new ConditionBlockCompose();
        idCondition.filter.setExpressions(expressions);
        idCondition.modelLabel = modelLabel;

        return idCondition;
    }

    /**
     * 根据id获取扁平查询条件
     *
     * @param ids 模型id
     * @return 条件
     */
    public static FlatQueryConditionDTO getFlatQueryConditionDTOForRoot(List<Integer> ids) {
        FlatQueryConditionDTO rootCondition = new FlatQueryConditionDTO();
        rootCondition.filter = new ConditionBlockCompose();
        List<ConditionBlock> expressions = new ArrayList<>();
        ConditionBlock conditionBlock = new ConditionBlock();
        conditionBlock.setProp("id");
        conditionBlock.setLimit(ids);
        conditionBlock.setOperator("IN");
        expressions.add(conditionBlock);
        rootCondition.filter.setExpressions(expressions);

        return rootCondition;
    }

    /**
     * 构造移动对象
     *
     * @param previousId    要移动的父id
     * @param previousLabel 要移动的父label
     * @param followId      要移动到的父id
     * @param followLabel   要移动到的父label
     * @param tobeId        要移动的子id
     * @param tobeLabel     要移动的子label
     * @return
     */
    public static MoveToOtherDTO setMoveToOtherDTO(Long previousId, String previousLabel, Long followId, String followLabel,
                                                   Long tobeId, String tobeLabel) {
        MoveToOtherDTO moveToOtherDTO = new MoveToOtherDTO();
        moveToOtherDTO.setPrevious(new ModelIdPairDTO(previousId, previousLabel));
        moveToOtherDTO.setFollowing(new ModelIdPairDTO(followId, followLabel));
        moveToOtherDTO.setToBeOut(new ModelIdPairDTO(tobeId, tobeLabel));
        return moveToOtherDTO;
    }
}
