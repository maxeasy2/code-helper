package com.mir.application.task;

public class TaskException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6529210335488902391L;
	private int errorCod;
	private String simpleMsg;
	
	public TaskException(String msg) {
		super(msg);
	}
	
	public TaskException() {
		super();
	}

	public int getErrorCod() {
		return errorCod;
	}

	public void setErrorCod(int errorCod) {
		this.errorCod = errorCod;
	}

	public String getSimpleMsg() {
		return simpleMsg;
	}

	public void setSimpleMsg(String simpleMsg) {
		this.simpleMsg = simpleMsg;
	}

}
