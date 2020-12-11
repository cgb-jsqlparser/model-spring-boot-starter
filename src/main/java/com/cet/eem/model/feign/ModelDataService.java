package com.cet.eem.model.feign;

import com.cet.eem.common.model.Result;
import com.cet.eem.common.model.ResultWithTotal;
import com.cet.eem.model.base.*;
import com.cet.electric.modelservice.common.dto.EnumItem;
import com.cet.electric.modelservice.common.dto.EnumTypeModelDTO;
import com.cet.electric.modelservice.common.dto.meta.ModelDetail;
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
@FeignClient(value = "model-service", url = "${cet.eem.service.url.model-service:''}")
public interface ModelDataService {

    @GetMapping("/model-meta/v1/models/{modelLabel}/search")
    Result<List<Map<String, Object>>> searchModel(@PathVariable("modelLabel") String modelLabel);

    /**
     * 通用查询接口，支持单模型列表、层次结构及树型结构返回
     * 接口对查询数据的条数有做限制，每一层级返回条数不超过1万条，最多不超过5层
     *
     * @param condition
     * @return
     */
    @PostMapping("/model/v1/query")
    ResultWithTotal<List<Map<String, Object>>> query(@RequestBody QueryCondition condition);

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
     *
     * @param data
     * @return
     */
    @PostMapping("model/v1/write/hierachy")
    @Deprecated
    Result<Object> write(@RequestBody Object data);

    /**
     * 批量插入/更新单模型数据
     *
     * @param data
     * @return
     */
    @PostMapping("model/v1/write/flat")
    Result<ModelSingeWriteVo> write(@RequestBody ModelSingeWriteVo data);

    /**
     * 查询所有枚举类型
     *
     * @return
     */
    @GetMapping("/model/v1/enumerations")
    Result<List<EnumTypeModelDTO>> getEnumerations();

    /**
     * 查询指定枚举类型
     *
     * @param modelLabel
     * @return
     */
    @GetMapping("/model/v1/enumerations/{modelLabel}")
    Result<List<EnumItem>> getEnumrationByModel(@PathVariable(value = "modelLabel") String modelLabel);

    /**
     * 根据记录的id和模型数据删除记录
     *
     * @param modelLabel 需要删除的模型的名称
     * @param ids        需要删除的id列表
     * @return
     */
    @DeleteMapping("/model/v1/{modelLabel}")
    Result<Object> delete(@PathVariable(value = "modelLabel") String modelLabel, Collection<Long> ids);

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
     * 根据模型的标识查询模型的定义信息，包含模型属性、直接关联的模型以及间接关联的模型信息
     *
     * @param modelLabel 模型标识
     * @return 模型信息
     */
    @GetMapping("/model-meta/v1/models/label/{modelLabel}")
    ResultWithTotal<ModelDetail> queryModelByLabel(@PathVariable String modelLabel);

    /**
     * 获取指定模型的实例id的锁，可以一次性获取多个id的锁
     * 返回为true为获取锁成功，false为获取锁失败；如果获取多个对象的锁是，其中一个获取失败则整体失败
     *
     * @param ids        id列表
     * @param modelLabel 模型标识
     * @return 是否成功
     */
    @PostMapping("/model-lock/v1/locks/{modelLabel}")
    Result<Boolean> lock(Collection<Long> ids, @PathVariable(value = "modelLabel") String modelLabel);

    /**
     * 释放模型的实例的锁资源，可以一次性释放多个实例
     *
     * @param ids        id列表
     * @param modelLabel 模型标识
     * @return 是否成功
     */
    @DeleteMapping("/model-lock/v1/locks/{modelLabel}")
    Result<Boolean> unlock(Collection<Long> ids, @PathVariable(value = "modelLabel") String modelLabel);
}
