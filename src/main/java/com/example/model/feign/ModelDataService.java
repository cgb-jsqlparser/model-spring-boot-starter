package com.example.model.feign;


import com.example.common.dto.Result;
import com.example.common.dto.ResultWithTotal;
import com.example.model.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 模型服务的数据接口
 *
 * @author CKai
 */
@FeignClient(contextId = "model-service-adapter", value = "model-service")
public interface ModelDataService {

    @GetMapping("/model-meta/v1/models/{modelLabel}/search")
    Result<List<Map<String, Object>>> searchModel(@PathVariable("modelLabel") String modelLabel);


    /**
     * 通用查询接口，支持单模型列表、层次结构及树型结构返回
     * 接口对查询数据的条数有做限制，每一层级返回条数不超过1万条，最多不超过5层
     *
     * @return
     */
    @PostMapping("/model/v1/query")
    ResultWithTotal<List<Map<String, Object>>> query(@RequestBody QueryCondition queryCondition);

    /**
     * 根据Id删除数据
     *
     * @param modelLabel
     * @param idRange
     * @return
     */
    @DeleteMapping("/model/v1/{modelLabel}")
    Result<Object> deleteById(@PathVariable("modelLabel") String modelLabel, @RequestBody Collection<Long> idRange);

    /**
     * 批量写入层次模型数据
     * 更新
     *
     * @param data
     * @return
     */
    @PostMapping("/model/v1/write/hierachy")
    Result<List<Map<String, Object>>> write(@RequestBody Object data);

    /**
     * 查询所有枚举类型
     *
     * @return
     */
    @GetMapping("/model/v1/enumerations")
    Result<List<IdTextPairWithModel>> getEnumerations();

    /**
     * 查询指定枚举类型
     *
     * @param modelLabel
     * @return
     */
    @GetMapping("/model/v1/enumerations/{modelLabel}")
    Result<List<IdTextPair>> getEnumrationByModel(@PathVariable(value = "modelLabel") String modelLabel);


    /**
     * 将实例从一个层次下面移动到另一个层次节点下面
     * 移动受模型关系的约束，如果两个模型之间没有关系，此时存储时应该出错.
     *
     * @param movetoOtherParams 待被转移的记录
     * @return void
     */
    @PutMapping("/model/v1/moveto")
    Result<Object> moveToOther(@RequestBody List<MoveToOtherDTO> movetoOtherParams);

    /**
     * 将实例从某个层次结构移出，模型数据本身不被删除
     *
     * @param moveOutParams 待被移出的数据
     * @return void
     */
    @DeleteMapping("/model/v1/moveout")
    Result<Object> moveOut(@RequestBody List<MoveOutDTO> moveOutParams);


    /**
     * 批量插入/更新单模型数据
     *
     * @param condition modelLabel指定模型标签, filter指定查询字段, writeProperty指定插入/更新字段, filterData和writeData以二维数组形式传入数据, operator指定更新类型(PLUS为累加, MULTIPLY为累乘, null或其他为更新), writeMethod指定写入方法: INSERT-插入, UPDATE-更新
     * @return
     */
    @PostMapping("/model/v1/write/flat")
    Result<Object> writeFlatData(@RequestBody WriteFlatDataCondition condition);
}
