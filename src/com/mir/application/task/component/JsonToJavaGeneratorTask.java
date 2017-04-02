package com.mir.application.task.component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.mir.application.Constant;
import com.mir.application.task.TaskRunner;
import com.mir.application.task.vo.SourceCode;
import com.mir.application.task.vo.TaskResult;
import com.mir.application.util.CommonUtils;
import com.mir.application.util.JsonToJavaUtil;
import com.mir.application.view.vo.MainViewVo;

public class JsonToJavaGeneratorTask extends TaskRunner<MainViewVo,Void, TaskResult> {
	

	private MainViewVo vo;
	
	@Override
	public Void onPreExecute(MainViewVo vo) {
		this.vo = vo;
		return null;
	}


	@Override
	public TaskResult doInBackground(Void value) throws Exception {
		TaskResult result = new TaskResult();
		result.setResultType(vo.getGenerateType());
		JsonToJavaUtil jsonToJavaUtil = new JsonToJavaUtil();
		List<SourceCode> sourceList = null;
		try{
			String jsonData = vo.getSqlValue();
			if( jsonData.startsWith("http") ){
				String contents = CommonUtils.getHttpData( jsonData );
		        if( !StringUtils.isEmpty( contents ) ) jsonData = contents;
			}
			
			List<Map<String, String>> dataList = jsonToJavaUtil.getDataTypeMapList(jsonData);
			sourceList = new ArrayList<SourceCode>();
			for(Map<String, String> dataMap : dataList){
				sourceList.add( this.makeVoSourceCode(dataMap) );
			}
			result.setData(sourceList);
			result.setSuccess(true);
		}catch(Exception e){
			e.printStackTrace();
			result.setSuccess(false);
			throw new Exception("[Json 에러] log :: \n"+vo.toString());
		}
		
		return result;
	}

	@Override
	public TaskResult onPostExecute(TaskResult result) {
		return result;
	}
	
	
	private SourceCode makeVoSourceCode(Map<String, String> fieldMap){
		StringBuffer sb = new StringBuffer();
		
		if( !StringUtils.isEmpty(vo.getPackageName()) ){
			sb.append("package ").append(vo.getPackageName()).append(";\n\n");
		}
		//if( vo.getToStingType() == Constant.TOSTRING_COMMONLANG3 ) sb.append("import org.apache.commons.lang3.builder.ToStringBuilder;").append("\n\n");
		
		String className = "NoNameClass";
		
		if( !StringUtils.isEmpty(vo.getClassName() )){
			className = vo.getClassName();
		}
		
		if(fieldMap.containsKey("@ClassName")){
			className = fieldMap.remove("@ClassName");
		}
		
		if( vo.getDataValueType() == Constant.DATA_VALUE_TYPE_JSONJACKSON ){
			sb.append("@JsonIgnoreProperties(ignoreUnknown = true)").append("\n");
			sb.append("@JsonInclude(JsonInclude.Include.NON_NULL)").append("\n");
		}
		
		sb.append("public class ").append(className);
		sb.append(" {").append("\n\n");
		
		Set<String> keySet = fieldMap.keySet();
		Iterator<String> it  = keySet.iterator();
		Map<String, String> cvtFieldMap = new LinkedHashMap<String, String>();
		while(it.hasNext()){
			String fieldName = it.next();
			String fieldType = fieldMap.get(fieldName);
			if(fieldType != null){
				String cvtFieldName = this.getFieldName(fieldName);
				cvtFieldMap.put(cvtFieldName,fieldType);
				if( vo.getDataValueType() == Constant.DATA_VALUE_TYPE_JSONJACKSON ){
					sb.append("\t@JsonProperty(\""+fieldName+"\")").append("\n");
				}
				
				sb.append("\tprivate ").append(fieldType).append(" ").append(cvtFieldName).append(";\n");
				
				if( vo.getDataValueType() == Constant.DATA_VALUE_TYPE_JSONJACKSON ){
					sb.append("\n");
				}
			}
		}
		sb.append("\n");
		sb.append(this.getGetterSetterValue(cvtFieldMap));
		sb.append(this.getToStringValue(cvtFieldMap));
		sb.append("}");
		SourceCode sourceCode = new SourceCode();
		sourceCode.setName(className);
		sourceCode.setValue(sb.toString());
		
		return sourceCode;
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

	private String getFieldName(String fieldName){
		String convertFieldName = fieldName;
		if( vo.getFieldName() == Constant.FIELD_NAMING_CAMEL ){
			convertFieldName = this.convertCamelValue(fieldName);
		}else if( vo.getFieldName() == Constant.FIELD_NAMING_LOWCASE ){
			convertFieldName = fieldName.toLowerCase();
		}else if( vo.getFieldName() == Constant.FIELD_NAMING_UPCASE ){
			convertFieldName = fieldName.toUpperCase();
		}
		return convertFieldName;
	}
	
	private String getToStringValue(Map<String, String> cvtFieldMap){
		if( vo.getToStingType() == Constant.TOSTRING_WRITE ){
			StringBuffer sb = new StringBuffer();
			Set<String> keySet = cvtFieldMap.keySet();
			Iterator<String> it = keySet.iterator();
			sb.append("\n");
			sb.append("\t@Override\n");
			sb.append("\tpublic String ").append("toString() {\n");
			sb.append("\t\tStringBuffer sb = new StringBuffer();\n");
			while(it.hasNext()){
				String fieldName = it.next();
				sb.append("\t\tsb.append(\"").append(fieldName).append(" = \").append(this.").append(fieldName).append(");\n");
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
	
	private String getGetterSetterValue(Map<String, String> cvtFieldMap){
		if( vo.isGetter() || vo.isSetter() ){
			StringBuffer sb = new StringBuffer();
			Set<String> keySet = cvtFieldMap.keySet();
			Iterator<String> it = keySet.iterator();
			while(it.hasNext()){
				String fieldName = it.next();
				String fieldType = cvtFieldMap.get(fieldName);
				if( vo.isGetter() ){
					sb.append("\tpublic ").append(fieldType).append(" ").append("get").append(fieldName.substring(0,1).toUpperCase()).append(fieldName.substring(1, fieldName.length())).append("() {\n");
					sb.append("\t\treturn ").append(fieldName).append(";\n");
					sb.append("\t}\n");
					sb.append("\n");
				}
				if( vo.isSetter() ){
					sb.append("\tpublic void ").append("set").append(fieldName.substring(0,1).toUpperCase()).append(fieldName.substring(1, fieldName.length())).append("(").append(fieldType).append(" ").append(fieldName).append(") {\n");
					sb.append("\t\tthis.").append(fieldName).append(" = ").append(fieldName).append(";\n");
					sb.append("\t}\n");
					sb.append("\n");
				}
			}
			return sb.toString();
		}
		return "";
	}
	
}
