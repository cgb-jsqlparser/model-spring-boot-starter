package com.example.model.dto;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class GroupBy {

    private String method;

    private String property;

    public GroupBy() {
    }

    public GroupBy(String method, String property) {
        this.method = method;
        this.property = property;
    }
}
