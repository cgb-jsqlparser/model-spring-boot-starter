package com.example.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class IdTextPairWithModel {
	
	private String modelLabel;
	
	private List<IdTextPair> enums;

}
