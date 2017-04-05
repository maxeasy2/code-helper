package com.mir.application.task.component;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.mir.application.Constant;
import com.mir.application.task.TaskException;
import com.mir.application.task.TaskRunner;
import com.mir.application.task.vo.SourceCode;
import com.mir.application.task.vo.TaskResult;
import com.mir.application.view.vo.MainViewVo;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;

public class SqlParseTask extends TaskRunner<MainViewVo,Void, TaskResult> {
	

	private MainViewVo vo;
	private List<String> camelFieldList;
	private List<String> schemeFieldList;
	private List<String> lowCaseFieldList;
	private List<String> upCaseFieldList;
	
	@Override
	public Void onPreExecute(MainViewVo vo) {
		this.vo = vo;
		return null;
	}


	@Override
	public TaskResult doInBackground(Void value) throws Exception {
		System.out.println(vo.toString());
		TaskResult result = null;
		try{
			List<String> columnList = this.getLastQueryField();
			if(  columnList != null) {
				result = new TaskResult();
				result.setSuccess(true);
				result.setResultType(vo.getGenerateType());
				this.createFieldNameList(columnList);
				
				if(vo.getGenerateType() == Constant.GENERATE_TYPES_VO ){
					result.setData(this.makeVoSourceCode());
				}else{
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
	
	
	private List<String> getLastQueryField() throws JSQLParserException {
		String sql = vo.getSqlValue();
		
		if(StringUtils.isEmpty(sql)){
			return null;
		}
		String[] errWordArr = {"{","!","#","\\"};
		boolean validate = false;
		for(String errWord : errWordArr){
			if(sql.contains(errWord)){
				validate = sql.contains(errWord);
				break;
			}
		}
		
		List<String> columnList = null;
		if(!validate){
			columnList = new ArrayList<String>();
			Statement statement = CCJSqlParserUtil.parse(sql);
			
			Select selectStatement = (Select) statement;
			SelectBody selectBody = selectStatement.getSelectBody();
			
			PlainSelect plainSelect = (PlainSelect)selectBody;
			
			List<SelectItem> itemList =  plainSelect.getSelectItems();
			for(SelectItem item : itemList){
				SelectExpressionItem expressItem = (SelectExpressionItem)item;
				String result = null;
				if( expressItem.getAlias() != null){
					result = expressItem.getAlias().toString().trim();
					String temp = result.toUpperCase();
					if(temp.indexOf("AS ") > -1){
						result = result.substring(temp.indexOf("AS ")+3 ,result.length());
					}
				}else{
					result = expressItem.getExpression().toString();
					if( result.indexOf(".") != -1 ){
						result = result.split("\\.")[1];
					}
				}
				columnList.add(result);
			}
		}
		return columnList;
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
			sb.append("\tprivate String ").append(field).append(";\n");
		}
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
			sb.append("\tprivate String ").append(field).append(";\n");
		}
		sb.append(this.getGetterSetterValue(selectField));
		
		sb.append("\tpublic void doSetFromResultSet(ResultSet rs) throws Exception {\n");
		sb.append("\t\t").append("doSetFromResultSet(rs, \"\");\n");
		sb.append("\t}\n");
		
		sb.append("\tpublic void doSetFromResultSet(ResultSet rs, String prefix) throws SQLException {\n");
		sb.append("\t\tString pre = (prefix == null ? \"\" : prefix);\n");
		for(int i=0; i<schemeFieldList.size(); i++){
			sb.append("\t\tthis.").append(selectField.get(i)).append(" = rs.getString( pre + \"").append(schemeFieldList.get(i)).append("\");\n");
		}
		sb.append("\t}\n");
		
		sb.append(this.getToStringValue(selectField));
		sb.append("}");
		return sb.toString();
	}
	
	private String makeShoppingBaseFormSourceCode(){
		StringBuffer sb = new StringBuffer();
		
		if( !StringUtils.isEmpty(vo.getPackageName()) ){
			sb.append("package ").append(vo.getPackageName()).append(";\n\n");
		}
		//if( vo.getToStingType() == Constant.TOSTRING_COMMONLANG3 ) sb.append("import org.apache.commons.lang3.builder.ToStringBuilder;").append("\n\n");
		sb.append("import javax.servlet.ServletRequest;").append("\n");
		sb.append("import org.apache.struts.action.ActionMapping;").append("\n");
		sb.append("import enet.cnt.jdbc.dto.ManualSettingBean;").append("\n");
		if( !StringUtils.isEmpty(vo.getPackageName()) ){
			sb.append("import ").append(vo.getPackageName()).append(".").append(vo.getClassName()).append("Dto;").append("\n");
		}
		sb.append("\n");
		
		
		String className = "NoNameClass";
		if( !StringUtils.isEmpty(vo.getClassName() ) ){
			className = vo.getClassName();
		}
		
		String formClassName = className+"Form";
		
		String dtoClassName =  className+"Dto";
		String conditionClassName = className+"Condition";
		String dtoClassFieldName = this.getClassFieldName(dtoClassName);
		sb.append("public class ").append(formClassName);
		sb.append(" extends InterparkBaseForm");
		sb.append(" {").append("\n\n");
		
		sb.append("\tprivate ").append(dtoClassName).append(" ").append( dtoClassFieldName ).append(";\n\n");
		sb.append("\tpublic ").append(formClassName).append("(){} ").append("\n\n");
		
		sb.append("\t@Override\n");
		sb.append("\tprotected void init() {").append("\n");
		sb.append("\t\tsuper.init();").append("\n");
		sb.append("\t\tthis.").append(dtoClassFieldName).append(" = new ").append(dtoClassName).append("()").append("\n");
		sb.append("\t}").append("\n\n");
		
		sb.append("\t@Override\n");
		sb.append("\tpublic void setSc(ActionMapping arg0, ServletRequest arg1) {").append("\n");
		if(vo.isCondition()){
			sb.append("\t\tif (this.sc == null) {").append("\n");
			sb.append("\t\t\tthis.sc = new ").append(conditionClassName).append("(\"sc\");").append("\n");
			sb.append("\t\t").append("}").append("\n");
		}
		sb.append("\t").append("}").append("\n");
		
		List<String> selectField = this.getSelectFieldTypeList();
		List<String> setterMethodNameList = this.getSetterMethodNameList(selectField);
		int i = 0;
		for(String setterMethodName : setterMethodNameList){
			sb.append("\tpublic void ").append(setterMethodName).append("(String ").append(selectField.get(i)).append(") {\n");
			sb.append("\t\t").append("this.").append(dtoClassFieldName).append(".").append(setterMethodName).append("(").append(selectField.get(i)).append(")").append(";\n");
			sb.append("\t").append("}").append("\n");
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
		sb.append("public class ").append(className);
		sb.append(" implements Transferable, ManualSettingBean");
		sb.append(" {").append("\n\n");
		
		List<String> selectField = this.getSelectFieldTypeList();
		for(String field : selectField){
			sb.append("\tprivate String ").append(field).append(";\n");
		}
		
		if( vo.isGetter() || vo.isSetter() ){
			for(String field : selectField){
				if( vo.isGetter() ){
					sb.append("\tpublic String ").append("get").append(field.substring(0,1).toUpperCase()).append(field.substring(1, field.length())).append("() {\n");
					sb.append("\t\treturn ").append(field).append(";\n");
					sb.append("\t}\n");
				}
				if( vo.isSetter() ){
					sb.append("\tpublic void ").append("set").append(field.substring(0,1).toUpperCase()).append(field.substring(1, field.length())).append("(String ").append(field).append(") {\n");
					sb.append("\t\tthis.").append(field).append(" = ").append(field).append(";\n");
					sb.append("\t}\n");
				}
			}
		}
		
		sb.append("\tpublic void doSetFromResultSet(ResultSet rs) throws Exception {\n");
		sb.append("\t\t").append("doSetFromResultSet(rs, \"\");\n");
		sb.append("\t}\n");
		
		sb.append("\tpublic void doSetFromResultSet(ResultSet rs, String prefix) throws SQLException {\n");
		sb.append("\t\tString pre = (prefix == null ? \"\" : prefix);\n");
		for(int i=0; i<schemeFieldList.size(); i++){
			sb.append("\t\tthis.").append(selectField.get(i)).append(" = rs.getString( pre + \"").append(schemeFieldList.get(i)).append("\");\n");
		}
		sb.append("\t}\n");
		
		if( vo.getToStingType() == Constant.TOSTRING_WRITE ){
			sb.append("\n");
			sb.append("\t@Override\n");
			sb.append("\tpublic String ").append("toString() {\n");
			sb.append("\t\tStringBuffer sb = new StringBuffer();\n");
			for(String field : selectField){
				sb.append("\t\tsb.append(\"").append(field).append(" = \").append(this.").append(field).append(");\n");
			}
			sb.append("\t\treturn sb.toString();\n");
			sb.append("\t}\n");
		}
	
		if( vo.getToStingType() == Constant.TOSTRING_COMMONLANG3 ){
			sb.append("\t@Override\n");
			sb.append("\tpublic String ").append("toString() {\n");
			sb.append("\t\treturn ToStringBuilder.reflectionToString(this);\n");
			sb.append("\t}\n");
		}
		sb.append("}");
		return sb.toString();
	}
	
	private String makeShoppingConditionSourceCode(){
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
		sb.append("public class ").append(className);
		sb.append(" implements Transferable, ManualSettingBean");
		sb.append(" {").append("\n\n");
		
		List<String> selectField = this.getSelectFieldTypeList();
		for(String field : selectField){
			sb.append("\tprivate String ").append(field).append(";\n");
		}
		
		if( vo.isGetter() || vo.isSetter() ){
			for(String field : selectField){
				if( vo.isGetter() ){
					sb.append("\tpublic String ").append("get").append(field.substring(0,1).toUpperCase()).append(field.substring(1, field.length())).append("() {\n");
					sb.append("\t\treturn ").append(field).append(";\n");
					sb.append("\t}\n");
				}
				if( vo.isSetter() ){
					sb.append("\tpublic void ").append("set").append(field.substring(0,1).toUpperCase()).append(field.substring(1, field.length())).append("(String ").append(field).append(") {\n");
					sb.append("\t\tthis.").append(field).append(" = ").append(field).append(";\n");
					sb.append("\t}\n");
				}
			}
		}
		
		sb.append("\tpublic void doSetFromResultSet(ResultSet rs) throws Exception {\n");
		sb.append("\t\t").append("doSetFromResultSet(rs, \"\");\n");
		sb.append("\t}\n");
		
		sb.append("\tpublic void doSetFromResultSet(ResultSet rs, String prefix) throws SQLException {\n");
		sb.append("\t\tString pre = (prefix == null ? \"\" : prefix);\n");
		for(int i=0; i<schemeFieldList.size(); i++){
			sb.append("\t\tthis.").append(selectField.get(i)).append(" = rs.getString( pre + \"").append(schemeFieldList.get(i)).append("\");\n");
		}
		sb.append("\t}\n");
		
		if( vo.getToStingType() == Constant.TOSTRING_WRITE ){
			sb.append("\n");
			sb.append("\t@Override\n");
			sb.append("\tpublic String ").append("toString() {\n");
			sb.append("\t\tStringBuffer sb = new StringBuffer();\n");
			for(String field : selectField){
				sb.append("\t\tsb.append(\"").append(field).append(" = \").append(this.").append(field).append(");\n");
			}
			sb.append("\t\treturn sb.toString();\n");
			sb.append("\t}\n");
		}
	
		if( vo.getToStingType() == Constant.TOSTRING_COMMONLANG3 ){
			sb.append("\t@Override\n");
			sb.append("\tpublic String ").append("toString() {\n");
			sb.append("\t\treturn ToStringBuilder.reflectionToString(this);\n");
			sb.append("\t}\n");
		}
		sb.append("}");
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


	private void createFieldNameList(List<String> fieldList){
		this.camelFieldList = new ArrayList<String>();
		this.schemeFieldList = new ArrayList<String>();
		this.lowCaseFieldList = new ArrayList<String>(); 
		this.upCaseFieldList = new ArrayList<String>(); 
		for(String field : fieldList){
			schemeFieldList.add(field);
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
			selectField = schemeFieldList;
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
					sb.append("\tpublic String ").append("get").append(field.substring(0,1).toUpperCase()).append(field.substring(1, field.length())).append("() {\n");
					sb.append("\t\treturn ").append(field).append(";\n");
					sb.append("\t}\n");
				}
				if( vo.isSetter() ){
					sb.append("\tpublic void ").append("set").append(field.substring(0,1).toUpperCase()).append(field.substring(1, field.length())).append("(String ").append(field).append(") {\n");
					sb.append("\t\tthis.").append(field).append(" = ").append(field).append(";\n");
					sb.append("\t}\n");
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
}
