package com.mir.application.task.component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.mir.application.Constant;
import com.mir.application.task.TaskException;
import com.mir.application.task.TaskRunner;
import com.mir.application.task.vo.SourceCode;
import com.mir.application.task.vo.TaskResult;
import com.mir.application.util.CommonUtils;
import com.mir.application.view.vo.MainViewVo;

public class SqlToJavaGeneratorTask extends TaskRunner<MainViewVo,Void, TaskResult> {
	

	private MainViewVo vo;
	private List<String> camelFieldList;
	private List<String> resultSetFieldList;
	private List<String> lowCaseFieldList;
	private List<String> upCaseFieldList;
	
	@Override
	public Void onPreExecute(MainViewVo vo) {
		this.vo = vo;
		return null;
	}


	@Override
	public TaskResult doInBackground(Void value) throws Exception {
		//System.out.println(vo.toString());
		TaskResult result = null;
		try{
			List<String> columnList = CommonUtils.getLastQueryField(vo.getSqlValue());
			if(  columnList != null) {
				result = new TaskResult();
				result.setSuccess(true);
				result.setResultType(vo.getGenerateType());
				this.createFieldNameList(columnList);
				
				if(vo.getGenerateType() == Constant.GENERATE_TYPES_VO ){
					result.setData(this.makeVoSourceCode());
				}else if(vo.getGenerateType() == Constant.GENERATE_TYPES_DTO ){
					List<SourceCode> shoppringCode = new ArrayList<SourceCode>();
					
					SourceCode dto = new SourceCode();
					dto.setName(vo.getClassName()+" <DTO>");
					dto.setValue(this.makeShoppingDtoSourceCode());
					shoppringCode.add( dto );
					
					if(vo.isBaseFrom()){
						SourceCode shop = new SourceCode();
						shop.setName(vo.getClassName()+" <Base Form>");
						shop.setValue(this.makeShoppingBaseFormSourceCode());
						shoppringCode.add( shop );
					}
					if(vo.isArrayForm()){
						SourceCode shop = new SourceCode();
						shop.setName(vo.getClassName()+" <Array Form>");
						shop.setValue(this.makeShoppingArrayFormSourceCode());
						shoppringCode.add( shop );
					}
					if(vo.isCondition()){
						SourceCode shop = new SourceCode();
						shop.setName(vo.getClassName()+" <Condition>");
						shop.setValue(this.makeShoppingConditionSourceCode());
						shoppringCode.add( shop );
					}
					result.setData(shoppringCode);
				}else if(vo.getGenerateType() == Constant.GENERATE_TYPES_SQL_INSERT ){
					result.setData(this.makeSqlInsertQuery());
				}else if(vo.getGenerateType() == Constant.GENERATE_TYPES_SQL_UPDATE ){
					result.setData(this.makeSqlUpdateQuery());
				}
			}else{
				throw new TaskException();
			}
		}catch(Exception e){
			throw new TaskException("[Sql 구문 에러] log :: \n"+vo.toString());
		}
		
		return result;
	}

	@Override
	public TaskResult onPostExecute(TaskResult result) {
		return result;
	}
	
	
	private String makeVoSourceCode(){
		StringBuffer sb = new StringBuffer();
		
		if( !StringUtils.isEmpty(vo.getPackageName()) ){
			sb.append("package ").append(vo.getPackageName()).append(";\n\n");
		}
		//if( vo.getToStingType() == Constant.TOSTRING_COMMONLANG3 ) sb.append("import org.apache.commons.lang3.builder.ToStringBuilder;").append("\n\n");
		
		String className = "NoNameClass";
		if( !StringUtils.isEmpty(vo.getClassName() ) ){
			className = vo.getClassName();
		}
		sb.append("public class ").append(className);
		sb.append(" {").append("\n\n");
		
		
		List<String> selectField = this.getSelectFieldTypeList();
		for(String field : selectField){
			sb.append("\tprivate ").append(this.getFieldType(field)).append(" ").append(field).append(";\n");
		}
		sb.append("\n");
		sb.append(this.getGetterSetterValue(selectField));
		sb.append(this.getToStringValue(selectField));
		sb.append("}");
		return sb.toString();
	}
	
	private String makeShoppingDtoSourceCode(){
		StringBuffer sb = new StringBuffer();
		
		if( !StringUtils.isEmpty(vo.getPackageName()) ){
			sb.append("package ").append(vo.getPackageName()).append(";\n\n");
		}
		//if( vo.getToStingType() == Constant.TOSTRING_COMMONLANG3 ) sb.append("import org.apache.commons.lang3.builder.ToStringBuilder;").append("\n\n");
		sb.append("import java.sql.ResultSet;").append("\n");
		sb.append("import java.sql.SQLException;").append("\n");
		sb.append("import enet.cnt.jdbc.dto.ManualSettingBean;").append("\n");
		sb.append("import enet.cnt.jdbc.dto.Transferable;").append("\n");
		sb.append("\n");
		
		String className = "NoNameClass";
		if( !StringUtils.isEmpty(vo.getClassName() ) ){
			className = vo.getClassName();
		}
		className = className+"Dto";
		sb.append("public class ").append(className);
		sb.append(" implements Transferable, ManualSettingBean");
		sb.append(" {").append("\n\n");
		
		List<String> selectField = this.getSelectFieldTypeList();
		for(String field : selectField){
			sb.append("\tprivate ").append(this.getFieldType(field)).append(" ").append(field).append(";\n");
		}
		sb.append("\n");
		sb.append(this.getGetterSetterValue(selectField));
		
		sb.append("\tpublic void doSetFromResultSet(ResultSet rs) throws Exception {\n");
		sb.append("\t\t").append("doSetFromResultSet(rs, \"\");\n");
		sb.append("\t}\n");
		sb.append("\n");
		
		sb.append("\tpublic void doSetFromResultSet(ResultSet rs, String prefix) throws SQLException {\n");
		sb.append("\t\tString pre = (prefix == null ? \"\" : prefix);\n");
		for(int i=0; i<resultSetFieldList.size(); i++){
			sb.append("\t\tthis.").append(selectField.get(i)).append(" = rs.").append("get").append(this.getFirstWordUpperCase(this.getFieldType(selectField.get(i)))).append("( pre + \"").append(resultSetFieldList.get(i)).append("\");\n");
		}
		sb.append("\t}\n");
		
		sb.append(this.getToStringValue(selectField));
		sb.append("}");
		return sb.toString();
	}
	
	private String makeShoppingBaseFormSourceCode(){
		StringBuffer sb = new StringBuffer();
		
		//if( !StringUtils.isEmpty(vo.getPackageName()) ){
		//	sb.append("package ").append(vo.getPackageName()).append(";\n\n");
		//}
		//if( vo.getToStingType() == Constant.TOSTRING_COMMONLANG3 ) sb.append("import org.apache.commons.lang3.builder.ToStringBuilder;").append("\n\n");
		sb.append("import javax.servlet.ServletRequest;").append("\n");
		sb.append("import org.apache.struts.action.ActionMapping;").append("\n");
		sb.append("import com.interpark.form.common.InterparkBaseForm;").append("\n");
		String className = "NoNameClass";
		if( !StringUtils.isEmpty(vo.getClassName() ) ){
			className = vo.getClassName();
		}
		
		String formClassName = className+"Form";
		String dtoClassName =  className+"Dto";
		String conditionClassName = className+"Condition";
		
		
		if( !StringUtils.isEmpty(vo.getPackageName()) ){
			sb.append("import ").append(vo.getPackageName()).append(".").append(dtoClassName).append(";").append("\n");
		}
		sb.append("\n");
		String dtoClassFieldName = this.getClassFieldName(dtoClassName);
		
		sb.append("public class ").append(formClassName);
		sb.append(" extends InterparkBaseForm");
		sb.append(" {").append("\n\n");
		
		sb.append("\tprivate ").append(dtoClassName).append(" ").append( dtoClassFieldName ).append(";\n\n");
		sb.append("\tpublic ").append(formClassName).append("(){} ").append("\n\n");
		
		sb.append("\t@Override\n");
		sb.append("\tprotected void init() {").append("\n");
		sb.append("\t\tsuper.init();").append("\n");
		sb.append("\t\tthis.").append(dtoClassFieldName).append(" = new ").append(dtoClassName).append("();").append("\n");
		sb.append("\t}").append("\n\n");
		
		sb.append("\t@Override\n");
		sb.append("\tpublic void setSc(ActionMapping arg0, ServletRequest arg1) {").append("\n");
		if(vo.isCondition()){
			sb.append("\t\tif (this.sc == null) {").append("\n");
			sb.append("\t\t\tthis.sc = new ").append(conditionClassName).append("(\"sc\");").append("\n");
			sb.append("\t\t").append("}").append("\n");
		}
		sb.append("\t").append("}").append("\n\n");
		
		
		List<String> selectField = this.getSelectFieldTypeList();
		List<String> setterMethodNameList = this.getSetterMethodNameList(selectField);
		int i = 0;
		
		for(String setterMethodName : setterMethodNameList){
			sb.append("\tpublic void ").append(setterMethodName).append("(").append(this.getFieldType(selectField.get(i))).append(" ").append(selectField.get(i)).append(") {\n");
			sb.append("\t\t").append("this.").append(dtoClassFieldName).append(".").append(setterMethodName).append("(").append(selectField.get(i)).append(")").append(";\n");
			sb.append("\t").append("}").append("\n\n");
			i++;
		}
		
		sb.append("\tpublic ").append(dtoClassName).append(" ").append("get").append(dtoClassName).append("() {").append("\n");
		sb.append("\t\t").append("return ").append(dtoClassFieldName).append(";").append("\n");
		sb.append("\t").append("}").append("\n");
		
		sb.append("}");
		return sb.toString();
	}
	
	private String makeShoppingArrayFormSourceCode(){
		StringBuffer sb = new StringBuffer();
		
		sb.append("import javax.servlet.ServletRequest;").append("\n");
		sb.append("import org.apache.struts.action.ActionMapping;").append("\n");
		sb.append("import com.interpark.form.common.InterparkArrayForm;").append("\n");
		
		String className = "NoNameClass";
		if( !StringUtils.isEmpty(vo.getClassName() ) ){
			className = vo.getClassName();
		}
		
		String formClassName = className+"ArrayForm";
		String dtoClassName =  className+"Dto";
		String conditionClassName = className+"Condition";
		
		
		if( !StringUtils.isEmpty(vo.getPackageName()) ){
			sb.append("import ").append(vo.getPackageName()).append(".").append(dtoClassName).append(";").append("\n");
		}
		sb.append("\n");
		
		String dtoClassFieldName = this.getClassFieldName(dtoClassName);
		String dtosClassFieldName = dtoClassFieldName +"s";
		
		sb.append("public class ").append(formClassName);
		sb.append(" extends InterparkArrayForm");
		sb.append(" {").append("\n\n");
		
		sb.append("\tprivate ").append(dtoClassName).append("[] ").append( dtosClassFieldName ).append(";\n\n");
		sb.append("\tpublic ").append(formClassName).append("(){} ").append("\n\n");
		
		sb.append("\t@Override\n");
		sb.append("\tprotected void init() {").append("\n");
		sb.append("\t\tsuper.init();").append("\n");
		sb.append("\t}").append("\n\n");
		
		sb.append("\t@Override\n");
		sb.append("\tpublic void setSc(ActionMapping arg0, ServletRequest arg1) {").append("\n");
		if(vo.isCondition()){
			sb.append("\t\tif (this.sc == null) {").append("\n");
			sb.append("\t\t\tthis.sc = new ").append(conditionClassName).append("(\"sc\");").append("\n");
			sb.append("\t\t").append("}").append("\n");
		}
		sb.append("\t").append("}").append("\n");
		sb.append("\n");
		
		sb.append("\t").append("private void prepareDtos(int length) {").append("\n");
		sb.append("\t\t").append("if (this.").append(dtosClassFieldName).append(" == null) {").append("\n");
		sb.append("\t\t\t").append("this.").append(dtosClassFieldName).append(" = new ").append(dtoClassName).append("[length];").append("\n");
		sb.append("\t\t\t").append("for(int i=0; i<length; i++) {").append("\n");
		sb.append("\t\t\t\t").append("this.").append(dtosClassFieldName).append("[i] = new ").append(dtoClassName).append("();").append("\n");
		sb.append("\t\t\t").append("}").append("\n");
		sb.append("\t\t").append("}").append("\n");
		sb.append("\t").append("}").append("\n");
		sb.append("\n");
		
		List<String> selectField = this.getSelectFieldTypeList();
		List<String> setterMethodNameList = this.getSetterMethodNameList(selectField);
		int i = 0;
		
		for(String setterMethodName : setterMethodNameList){
			sb.append("\tpublic void ").append(setterMethodName).append("(").append(this.getFieldType(selectField.get(i))).append("[] ").append(selectField.get(i)).append(") {\n");
			sb.append("\t\t").append("this.prepareDtos(").append(selectField.get(i)).append(".length);").append("\n");
			sb.append("\t\t").append("for (int i=0; i<").append(selectField.get(i)).append(".length; i++) {").append("\n");
			sb.append("\t\t\t").append("this.").append(dtosClassFieldName).append("[i]").append(".").append(setterMethodName).append("(").append(selectField.get(i)).append("[i]").append(")").append(";\n");
			sb.append("\t\t").append("}").append("\n");
			sb.append("\t").append("}").append("\n");
			i++;
			sb.append("\n");
		}
		
		sb.append("\tpublic ").append(dtoClassName).append("[] ").append("get").append(dtoClassName).append("s() {").append("\n");
		sb.append("\t\t").append("return this.").append(dtosClassFieldName).append(";").append("\n");
		sb.append("\t").append("}").append("\n");
		
		sb.append("}");
		return sb.toString();
	}
	
	private String makeShoppingConditionSourceCode(){
		StringBuffer sb = new StringBuffer();
		
		//if( !StringUtils.isEmpty(vo.getPackageName()) ){
		//	sb.append("package ").append(vo.getPackageName()).append(";\n\n");
		//}
		sb.append("\n");
		sb.append("import enet.cnt.jdbc.dto.PaginatedCondition;").append("\n");
		sb.append("\n");
		
		String className = "NoNameClass";
		if( !StringUtils.isEmpty(vo.getClassName() ) ){
			className = vo.getClassName();
		}
		
		className = className+"Condition";
		sb.append("public class ").append(className);
		sb.append(" extends PaginatedCondition");
		sb.append(" {").append("\n\n");
		
		List<String> selectField = this.getSelectFieldTypeList();
		for(String field : selectField){
			sb.append("\tprivate ").append(this.getFieldType(field)).append(" ").append(field).append(";\n");
		}
		sb.append("\n");
		sb.append("\tpublic ").append(className).append("(String propName){").append("\n");
		sb.append("\t\t").append("super(propName);").append("\n");
		sb.append("\t").append("}").append("\n\n");
		
		sb.append(this.getGetterSetterValue(selectField));
		sb.append(this.getToStringValue(selectField));
		sb.append("}");
		return sb.toString();
	}
	
	public String makeSqlInsertQuery(){
		StringBuffer sb = new StringBuffer();
		String tableName = "insert_table";
		
		sb.append("INSERT INTO ").append(tableName).append(" ( ").append("\n");
		String rsField = StringUtils.join(resultSetFieldList,"\n\t\t\t,");
		sb.append("\t\t\t").append(rsField).append("\n");
		sb.append(")VALUES ( ").append("\n");
		List<String> sqlVariableList = this.getSqlVariableList(this.getSelectFieldTypeList());
		String seletField = StringUtils.join(sqlVariableList,"\n\t\t\t,");
		sb.append("\t\t\t").append(seletField).append("\n");
		sb.append(")");
		
		return sb.toString();
	}
	
	public String makeSqlUpdateQuery(){
		StringBuffer sb = new StringBuffer();
		String tableName = "update_table";
		List<String> sqlVariableList = this.getSqlVariableList(this.getSelectFieldTypeList());
		sb.append("UPDATE ").append(tableName).append(" SET ").append("\n");
		for(int i=0; i<resultSetFieldList.size(); i++){
			String comma = "";
			if( i != 0 ) comma = ",";
			sb.append(comma).append(resultSetFieldList.get(i)).append(" = ").append(sqlVariableList.get(i)).append("\n");
		}
		
		return sb.toString();
	}
	
	private String convertCamelValue(String field){
		if( StringUtils.isEmpty(field) ){
			return "";
		}
		String[] fieldArray = field.split("_");
		String result = "";
		if( fieldArray.length > 0 ){
			for(int i=0; i<fieldArray.length; i++){
				if( i == 0 ){
					result = fieldArray[i].toLowerCase(); 
				}else{
					String first = fieldArray[i].substring(0,1).toUpperCase();
					String second = fieldArray[i].substring(1,fieldArray[i].length()).toLowerCase();
					result += first+second;
				}
			}
		}else{
			result = field;
		}
		return result;
	}

	private String getClassFieldName(String className){
		return className.substring(0,1).toLowerCase()+className.substring(1,className.length());
	}

	private String getFirstWordUpperCase(String str){
		return str.substring(0,1).toUpperCase()+str.substring(1,str.length());
	}

	private void createFieldNameList(List<String> fieldList){
		this.camelFieldList = new ArrayList<String>();
		this.resultSetFieldList = new ArrayList<String>();
		this.lowCaseFieldList = new ArrayList<String>(); 
		this.upCaseFieldList = new ArrayList<String>(); 
		for(String field : fieldList){
			resultSetFieldList.add(field);
			lowCaseFieldList.add(field.toLowerCase());
			upCaseFieldList.add(field.toUpperCase());
			camelFieldList.add(this.convertCamelValue(field));
		}
	}
	
	private List<String> getSelectFieldTypeList(){
		List<String> selectField = null;
		if( vo.getFieldName() == Constant.FIELD_NAMING_CAMEL ){
			selectField = camelFieldList;
		}else if( vo.getFieldName() == Constant.FIELD_NAMING_RESULTSET ){
			selectField = resultSetFieldList;
		}else if( vo.getFieldName() == Constant.FIELD_NAMING_LOWCASE ){
			selectField = lowCaseFieldList;
		}else if( vo.getFieldName() == Constant.FIELD_NAMING_UPCASE ){
			selectField = upCaseFieldList;
		}
		return selectField;
				
	}
	
	private String getToStringValue(List<String> selectField){
		if( vo.getToStingType() == Constant.TOSTRING_WRITE ){
			StringBuffer sb = new StringBuffer();
			sb.append("\n");
			sb.append("\t@Override\n");
			sb.append("\tpublic String ").append("toString() {\n");
			sb.append("\t\tStringBuffer sb = new StringBuffer();\n");
			for(String field : selectField){
				sb.append("\t\tsb.append(\"").append(field).append(" = \").append(this.").append(field).append(");\n");
			}
			sb.append("\t\treturn sb.toString();\n");
			sb.append("\t}\n");
			return sb.toString();
		}
	
		if( vo.getToStingType() == Constant.TOSTRING_COMMONLANG3 ){
			StringBuffer sb = new StringBuffer();
			sb.append("\t@Override\n");
			sb.append("\tpublic String ").append("toString() {\n");
			sb.append("\t\treturn ToStringBuilder.reflectionToString(this);\n");
			sb.append("\t}\n");
			return sb.toString();
		}
		return "";
	}
	
	private String getGetterSetterValue(List<String> selectField){
		if( vo.isGetter() || vo.isSetter() ){
			StringBuffer sb = new StringBuffer();
			for(String field : selectField){
				if( vo.isGetter() ){
					sb.append("\tpublic ").append(this.getFieldType(field)).append(" ").append("get").append(field.substring(0,1).toUpperCase()).append(field.substring(1, field.length())).append("() {\n");
					sb.append("\t\treturn ").append(field).append(";\n");
					sb.append("\t}\n");
					sb.append("\n");
				}
				if( vo.isSetter() ){
					sb.append("\tpublic void ").append("set").append(field.substring(0,1).toUpperCase()).append(field.substring(1, field.length())).append("(").append(this.getFieldType(field)).append(" ").append(field).append(") {\n");
					sb.append("\t\tthis.").append(field).append(" = ").append(field).append(";\n");
					sb.append("\t}\n");
					sb.append("\n");
				}
			}
			return sb.toString();
		}
		return "";
	}
	
	private List<String> getSetterMethodNameList(List<String> selectField){
		List<String> setterMethodName = new ArrayList<String>();
		for(String field : selectField){
			setterMethodName.add("set"+field.substring(0,1).toUpperCase()+field.substring(1, field.length()));
		}
		return setterMethodName;
	}
	
	private String getFieldType(String fieldName){
		if(StringUtils.isEmpty(fieldName))return "String";
		
		if(vo.isFieldTypeUse()){
			Map<String,String> map = vo.getFieldTypeMap();
			if(map == null) return "String";
			if(!map.containsKey(fieldName)) return "String";
			
			return map.get(fieldName);
		}
		return "String";
	}
	
	private String getSqlVariable(String fieldName){
		return Constant.SQL_VAR_EXPRESSION_PREFIX+fieldName+Constant.SQL_VAR_EXPRESSION_POSTFIX;
	}
	
	private List<String> getSqlVariableList(List<String> list){
		List<String> sqlVarList = new ArrayList<String>();
		for(String sqlVar : list){
			sqlVarList.add( this.getSqlVariable( sqlVar ));
		}
		return sqlVarList;
	}
}
