package com.example.model.dto;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class MoveOutDTO {

    private ModelIdPairDTO parent;

    private ModelIdPairDTO toBeOut;
}
