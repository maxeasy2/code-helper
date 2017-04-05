package com.mir.application.task.component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import com.mir.application.task.TaskException;
import com.mir.application.task.TaskRunner;
import com.mir.application.task.vo.TaskResult;

public class FieldTypeSaveTask extends TaskRunner<String[], String[], TaskResult> {
	

	
	@Override
	public String[] onPreExecute(String[] fieldTypeList) {
		return fieldTypeList;
	}


	@Override
	public TaskResult doInBackground(String[] fieldTypeList) throws Exception {
		TaskResult result = null;
		ObjectOutputStream oos = null;
		FileOutputStream fos = null;
		try{
			if( fieldTypeList != null) {
				result = new TaskResult();
				
				File file = new File("config.dat");
				fos = new FileOutputStream(file);
				oos = new ObjectOutputStream(fos);
				oos.writeObject(fieldTypeList);
				
				result.setSuccess(true);
				result.setData(fieldTypeList);
				
			}else{
				throw new TaskException();
			}
		}catch(Exception e){
			throw new TaskException("[변수타입 설정 저장 에러] log !!!");
		}finally {
			if(oos != null )oos.close();
			if(fos != null )fos.close();
		}
		
		return result;
	}

	@Override
	public TaskResult onPostExecute(TaskResult result) {
		return result;
	}
	

}
