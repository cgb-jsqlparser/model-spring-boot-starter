package com.example.model.dto;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class MoveToOtherDTO {

    private ModelIdPairDTO toBeOut;

    private ModelIdPairDTO previous;

    private ModelIdPairDTO following;
}
