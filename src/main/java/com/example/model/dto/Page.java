package com.example.model.dto;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class Page {

    private Integer index;
    private Integer limit;

    public Page() {

    }

    public Page(Integer index, Integer limit) {
        this.index = index;
        this.limit = limit;
    }
}