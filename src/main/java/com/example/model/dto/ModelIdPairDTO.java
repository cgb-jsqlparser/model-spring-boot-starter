package com.example.model.dto;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ModelIdPairDTO {

    private String modelLabel;

    private Long id;

    public ModelIdPairDTO() {
    }

    public ModelIdPairDTO(Long id, String modelLabel) {
        this.id = id;
        this.modelLabel = modelLabel;
    }
}
