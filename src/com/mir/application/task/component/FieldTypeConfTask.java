package com.mir.application.task.component;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.mir.application.Constant;
import com.mir.application.task.TaskRunner;
import com.mir.application.task.vo.TaskResult;
import com.mir.application.util.CommonUtils;
import com.mir.application.view.vo.MainViewVo;

public class FieldTypeConfTask extends TaskRunner<MainViewVo, Void, TaskResult> {
	

	private MainViewVo vo;
	
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
			List<String> columnList = CommonUtils.getLastQueryField(vo.getSqlValue());
			if(  columnList != null) {
				result = new TaskResult();
				result.setSuccess(true);
				result.setResultType(vo.getGenerateType());
				result.setData(this.getSelectFieldTypeList(columnList));
			}else{
				throw new Exception();
			}
		}catch(Exception e){
			throw new Exception("[Sql 구문 에러] log :: \n"+vo.toString());
		}
		
		return result;
	}

	@Override
	public TaskResult onPostExecute(TaskResult result) {
		return result;
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

	
	private List<String> getSelectFieldTypeList(List<String> fieldList){
		List<String> selectField = new ArrayList<String>();
		if( fieldList != null ){
			for(String field : fieldList){
				if( vo.getFieldName() == Constant.FIELD_NAMING_CAMEL ){
					selectField.add(this.convertCamelValue(field));
				}else if( vo.getFieldName() == Constant.FIELD_NAMING_RESULTSET ){
					selectField.add(field);
				}else if( vo.getFieldName() == Constant.FIELD_NAMING_LOWCASE ){
					selectField.add(field.toLowerCase());
				}else if( vo.getFieldName() == Constant.FIELD_NAMING_UPCASE ){
					selectField.add(field.toUpperCase());
				}
			}
		}
		return selectField;
				
	}
	

}
