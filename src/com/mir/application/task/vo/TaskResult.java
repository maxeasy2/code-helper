package com.mir.application.task.vo;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class TaskResult {

	private int resultType;
	private String resultMsg;
	private boolean success;
	private Object data;
	
	
	public int getResultType() {
		return resultType;
	}
	public void setResultType(int resultType) {
		this.resultType = resultType;
	}
	public String getResultMsg() {
		return resultMsg;
	}
	public void setResultMsg(String resultMsg) {
		this.resultMsg = resultMsg;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	
	@Override
	public String toString() {
		return  ToStringBuilder.reflectionToString(this);
	}
}
