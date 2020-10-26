package com.example.model.dto;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class IdTextPair implements Comparable<IdTextPair> {

    private Integer id;

    private String text;

    private String propertyLabel;

    public IdTextPair() {
    }

    public IdTextPair(Integer id, String text, String propertyLabel) {
        this.id = id;
        this.text = text;
        this.propertyLabel = propertyLabel;
    }

    @Override
    public int compareTo(IdTextPair o) {
        if (this.id > o.id) {
            return 1;
        } else if (this.id < o.id) {
            return -1;
        }
        return 0;
    }
}
