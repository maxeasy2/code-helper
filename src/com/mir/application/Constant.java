package com.mir.application;

public class Constant {
	public static final String APPLICATION_NAME =  "Code-Helper";
	public static final String APPLICATION_VERSION =  "v0.8";
	public static final String[] GENERATE_TYPES = {"VO","Shopping-DTO","SQL-Insert","SQL-Update"}; 
	public static final int GENERATE_TYPES_VO = 0;
	public static final int GENERATE_TYPES_DTO = 1;
	public static final int GENERATE_TYPES_SQL_INSERT = 2;
	public static final int GENERATE_TYPES_SQL_UPDATE = 3;
	
	public static final int TOSTRING_NONE = 0;
	public static final int TOSTRING_WRITE = 1;
	public static final int TOSTRING_COMMONLANG3 = 2;
	
	public static final int FIELD_NAMING_CAMEL = 0;
	public static final int FIELD_NAMING_RESULTSET = 1;
	public static final int FIELD_NAMING_LOWCASE = 2;
	public static final int FIELD_NAMING_UPCASE = 3;
	
	public static final String[] DEFAULT_FIELD_TYPES = {"String","byte","short","int","long","float","double","boolean","Date"}; 
	public static final String SQL_VAR_EXPRESSION_PREFIX = "!#prefix#";
	public static final String SQL_VAR_EXPRESSION_POSTFIX = "!#postfix#";
	
	public static final int DATA_VALUE_TYPE_SQL = 0;
	public static final int DATA_VALUE_TYPE_JSON = 1;
	public static final int DATA_VALUE_TYPE_JSONJACKSON = 2;
	
	public static final int HTTP_CONNECTION_TIMEOUT = 5000;
	public static final int HTTP_READ_TIMEOUT = 5000;
}