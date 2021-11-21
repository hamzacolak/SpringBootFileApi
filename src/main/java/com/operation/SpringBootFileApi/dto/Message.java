package com.operation.SpringBootFileApi.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.operation.SpringBootFileApi.utils.MessageConstant;

import io.swagger.annotations.ApiModel;


@ApiModel(value = "Message ", description = "displaying for error message. code 1 is success,0 failed ")
public class Message implements Serializable,Cloneable{

	private static final long serialVersionUID = 1L;
	private Long   code;
	private String message;
	
	public Long getCode() {
		return code;
	}
	public void setCode(Long code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	public Message() {

	}
	public Message(Long code, String message) {
		this.code = code;
		this.message = message;
	}
	
	
	@JsonIgnore
	public boolean isFailed() {
		return getCode()==null || getCode().intValue()==MessageConstant.getFailed().intValue();
	}
	
	@JsonIgnore
	public boolean isSuccess() {
		return getCode()!=null && getCode().intValue()==MessageConstant.getSuccess().intValue();
	}
}
