package com.example.toolkit;


import com.example.metadata.TableInfo;
import com.example.metadata.TableInfoHelper;
import com.example.toolkit.support.ColumnCache;
import com.example.toolkit.support.SFunction;
import com.example.toolkit.support.SerializedLambda;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Locale.ENGLISH;

/**
 * Lambda 解析工具类
 *
 * @author HCL, MieMie
 * @since 2018-05-10
 */
public final class LambdaUtils {


    /**
     * 字段映射
     */
    private static final Map<String, Map<String, ColumnCache>> COLUMN_CACHE_MAP = new ConcurrentHashMap<>();

    /**
     * SerializedLambda 反序列化缓存
     */
    private static final Map<String, WeakReference<SerializedLambda>> FUNC_CACHE = new ConcurrentHashMap<>();

    /**
     * 解析 lambda 表达式, 该方法只是调用了 {@link SerializedLambda#resolve(SFunction)} 中的方法，在此基础上加了缓存。
     * 该缓存可能会在任意不定的时间被清除
     *
     * @param func 需要解析的 lambda 对象
     * @param <T>  类型，被调用的 Function 对象的目标类型
     * @return 返回解析后的结果
     * @see SerializedLambda#resolve(SFunction)
     */
    public static <T> SerializedLambda resolve(SFunction<T, ?> func) {
        Class<?> clazz = func.getClass();
        String canonicalName = clazz.getCanonicalName();
        return Optional.ofNullable(FUNC_CACHE.get(canonicalName))
                .map(WeakReference::get)
                .orElseGet(() -> {
                    SerializedLambda lambda = SerializedLambda.resolve(func);
                    FUNC_CACHE.put(canonicalName, new WeakReference<>(lambda));
                    return lambda;
                });
    }

    /**
     * 格式化 key 将传入的 key 变更为大写格式
     *
     * <pre>
     *     Assert.assertEquals("USERID", formatKey("userId"))
     * </pre>
     *
     * @param key key
     * @return 大写的 key
     */
    public static String formatKey(String key) {
        return key.toUpperCase(ENGLISH);
    }

    /**
     * 缓存实体字段 MAP 信息
     *
     * @param info 表信息
     * @return 缓存 map
     */
    private static Map<String, ColumnCache> createColumnCacheMap(TableInfo info) {
        Map<String, ColumnCache> map = new HashMap<>();
        info.getFieldList().forEach(i ->
                map.put(formatKey(i.getProperty()), new ColumnCache(i.getColumn()))
        );
        return map;
    }

    /**
     * 获取实体对应字段 MAP
     *
     * @param clazz 实体类
     * @return 缓存 map
     */
    public static Map<String, ColumnCache> getColumnMap(Class<?> clazz) {
        return COLUMN_CACHE_MAP.computeIfAbsent(clazz.getName(), key -> {
            TableInfo info = TableInfoHelper.getModelInfo(clazz);
            return info == null ? null : createColumnCacheMap(info);
        });
    }
}