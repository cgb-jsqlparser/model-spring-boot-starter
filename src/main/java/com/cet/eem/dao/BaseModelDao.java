package com.cet.eem.dao;


import com.cet.eem.common.model.BaseVo;
import com.cet.eem.conditions.Wrapper;
import com.cet.eem.common.model.Page;
import com.cet.eem.common.model.ResultWithTotal;
import com.cet.eem.model.model.IModel;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface BaseModelDao<T extends IModel> {

    /**
     * 插入一条记录
     *
     * @param entity 实体对象
     */
    int insert(T entity);

    /**
     * 根据 ID 删除
     *
     * @param id 主键ID
     */
    int deleteById(Long id);

    /**
     * 根据 columnMap 条件，删除记录
     *
     * @param columnMap 表字段 map 对象
     */
    int deleteByMap(Map<String, Object> columnMap);

    /**
     * 根据 entity 条件，删除记录
     *
     * @param wrapper 实体对象封装操作类（可以为 null）
     */
    int delete(Wrapper<T> wrapper);

    /**
     * 删除（根据ID 批量删除）
     *
     * @param idList 主键ID列表(不能为 null 以及 empty)
     */
    int deleteBatchIds(Collection<Long> idList);

    /**
     * 根据 ID 修改
     *
     * @param entity 实体对象
     */
    int updateById(T entity);

    /**
     * 根据 whereEntity 条件，更新记录
     *
     * @param entity        实体对象 (set 条件值,可以为 null)
     * @param updateWrapper 实体对象封装操作类（可以为 null,里面的 entity 用于生成 where 语句）
     */
    int update(T entity, Wrapper<T> updateWrapper);

    /**
     * 批量保存或更新
     *
     * @param entityList
     */
    int saveOrUpdateBatch(Collection<T> entityList);

    /**
     * 根据 ID 查询
     *
     * @param id 主键ID
     */
    T selectById(Long id);

    /**
     * 查询（根据ID 批量查询）
     *
     * @param idList 主键ID列表(不能为 null 以及 empty)
     */
    List<T> selectBatchIds(Collection<Long> idList);

    /**
     * 查询（根据 columnMap 条件）
     *
     * @param columnMap 表字段 map 对象
     */
    List<T> selectByMap(Map<String, Object> columnMap);

    /**
     * 根据 entity 条件，查询一条记录
     *
     * @param queryWrapper 实体对象封装操作类（可以为 null）
     */
    T selectOne(Wrapper<T> queryWrapper);

    /**
     * 根据 Wrapper 条件，查询总记录数
     *
     * @param queryWrapper 实体对象封装操作类（可以为 null）
     */
    Integer selectCount(Wrapper<T> queryWrapper);

    /**
     * 查询所有记录
     *
     * @return
     */
    List<T> selectAll();

    /**
     * 根据 entity 条件，查询全部记录
     *
     * @param queryWrapper 实体对象封装操作类（可以为 null）
     */
    List<T> selectList(Wrapper<T> queryWrapper);

    /**
     * 根据 Wrapper 条件，查询全部记录
     *
     * @param queryWrapper 实体对象封装操作类（可以为 null）
     */
    List<Map<String, Object>> selectMaps(Wrapper<T> queryWrapper);


    /**
     * 根据 entity 条件，查询全部记录（并翻页）
     *
     * @param queryWrapper 实体对象封装操作类（可以为 null）
     * @param page         分页参数
     */
    ResultWithTotal<List<T>> selectPage(Wrapper<T> queryWrapper, Page page);

    /**
     * 根据 Wrapper 条件，查询全部记录（并翻页）
     *
     * @param queryWrapper 实体对象封装操作类
     * @param page         分页参数
     */
    ResultWithTotal<List<Map<String, Object>>> selectMapsPage(Wrapper<T> queryWrapper, Page page);

    /**
     * 根据id连表查询
     *
     * @param aClass 使用组合拼凑的具有关联关系的实体Class
     * @param id     主模型的id
     * @return 关联的实体
     */
    <K extends T> K selectRelatedById(Class<K> aClass, Long id);


    /**
     * 根据id连表查询
     *
     * @param aClass 使用组合拼凑的具有关联关系的实体Class
     * @param id     主模型的id
     * @return
     */
    <K extends T> Map<String, Object> selectRelatedTreeById(Class<K> aClass, Long id);


    /**
     * 根据id连表查询
     *
     * @param aClass          使用组合拼凑的具有关联关系的实体Class
     * @param id              主模型的id
     * @param subModelWrapper 子模型的查询条件
     * @return
     */
    <K extends T> Map<String, Object> selectRelatedTreeById(Class<K> aClass, Long id, Wrapper<? extends IModel>... subModelWrapper);

    /**
     * 根据条件连表查询
     *
     * @param aClass       使用组合拼凑的具有关联关系的实体Class
     * @param queryWrapper 主模型查询条件
     * @return
     */
    <K extends T> List<K> selectRelatedList(Class<K> aClass, Wrapper<T> queryWrapper);


    /**
     * 根据条件连表查询
     *
     * @param aClass          使用组合拼凑的具有关联关系的实体Class
     * @param queryWrapper    主模型查询条件
     * @param subModelWrapper
     * @return
     */
    <K extends T> List<K> selectRelatedList(Class<K> aClass, Wrapper<T> queryWrapper, Wrapper<? extends IModel>... subModelWrapper);

    /**
     * 根据条件连表查询
     * 会查询所有带关系的模型
     *
     * @param queryWrapper
     * @return
     */
    List<Map<String, Object>> selectRelatedTreeList(Wrapper<T> queryWrapper);

    /**
     * 插入子层级
     *
     * @param id       父模型id
     * @param children 子模型
     * @return
     */
    List<BaseVo> insertChild(Long id, Collection<IModel> children);

    /**
     * 删除子层级
     *
     * @param id       父模型id
     * @param children 子模型
     * @return
     */
    List<Map<String, Object>> deleteChild(Long id, Collection<IModel> children);

    /**
     * 删除子层级
     *
     * @param id       父模型id
     * @param children 子模型
     * @return
     */
    List<Map<String, Object>> moveChild(Long id, Collection<IModel> children);

    /**
     * 删除子层级
     *
     * @param id       父模型id
     * @param oldChild 旧模型
     * @param newChild 新模型
     * @return
     */
    List<Map<String, Object>> replaceChild(Long id, IModel oldChild, IModel newChild);
}