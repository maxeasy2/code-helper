package com.mir.application.view.vo;

import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class MainViewVo {

	private String packageName;
	private String className;
	private int fieldName;
	private int generateType;
	private int toStingType;
	private boolean getter;
	private boolean setter;
	private boolean baseFrom;
	private boolean arrayForm;
	private boolean condition;
	private String sqlValue;
	private boolean fieldTypeUse;
	private Map<String,String> fieldTypeMap;
	private int dataValueType;
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public int getFieldName() {
		return fieldName;
	}
	public void setFieldName(int fieldName) {
		this.fieldName = fieldName;
	}
	public String getSqlValue() {
		return sqlValue;
	}
	public void setSqlValue(String sqlValue) {
		this.sqlValue = sqlValue;
	}
	public int getGenerateType() {
		return generateType;
	}
	public void setGenerateType(int generateType) {
		this.generateType = generateType;
	}
	
	public int getToStingType() {
		return toStingType;
	}
	public void setToStingType(int toStingType) {
		this.toStingType = toStingType;
	}
	
	public boolean isGetter() {
		return getter;
	}
	public void setGetter(boolean getter) {
		this.getter = getter;
	}
	public boolean isSetter() {
		return setter;
	}
	public void setSetter(boolean setter) {
		this.setter = setter;
	}
	
	public boolean isBaseFrom() {
		return baseFrom;
	}
	public void setBaseFrom(boolean baseFrom) {
		this.baseFrom = baseFrom;
	}
	public boolean isArrayForm() {
		return arrayForm;
	}
	public void setArrayForm(boolean arrayForm) {
		this.arrayForm = arrayForm;
	}
	public boolean isCondition() {
		return condition;
	}
	public void setCondition(boolean condition) {
		this.condition = condition;
	}
	
	public boolean isFieldTypeUse() {
		return fieldTypeUse;
	}
	public void setFieldTypeUse(boolean fieldTypeUse) {
		this.fieldTypeUse = fieldTypeUse;
	}
	public Map<String, String> getFieldTypeMap() {
		return fieldTypeMap;
	}
	public void setFieldTypeMap(Map<String, String> fieldTypeMap) {
		this.fieldTypeMap = fieldTypeMap;
	}
	
	public int getDataValueType() {
		return dataValueType;
	}
	public void setDataValueType(int dataValueType) {
		this.dataValueType = dataValueType;
	}
	@Override
	public String toString() {
		return  ToStringBuilder.reflectionToString(this);
	}
}
