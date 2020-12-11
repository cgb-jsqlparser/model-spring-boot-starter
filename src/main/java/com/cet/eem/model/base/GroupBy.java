package com.cet.eem.model.base;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName GrougBy
 * @Description 聚合分组
 * @Author zhang zhuang
 * @Date 2020/3/27 8:17
 */
@Data
@NoArgsConstructor
public class GroupBy {
    private String method;
    private String property;

    public GroupBy(String method, String property) {
        this.method = method;
        this.property = property;
    }
}
