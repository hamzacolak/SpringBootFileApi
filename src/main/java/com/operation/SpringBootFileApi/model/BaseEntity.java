package com.operation.SpringBootFileApi.model;

import java.io.Serializable;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import com.operation.SpringBootFileApi.dto.Message;

@MappedSuperclass
public abstract class BaseEntity implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	
	@Transient
	private Message message;

	public Message getMessage() {
		if(message==null) {
			message=new Message();
		}
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}
	
}
