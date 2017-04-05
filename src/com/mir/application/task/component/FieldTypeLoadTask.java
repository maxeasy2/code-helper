package com.mir.application.task.component;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

import com.mir.application.task.TaskException;
import com.mir.application.task.TaskRunner;
import com.mir.application.task.vo.TaskResult;

public class FieldTypeLoadTask extends TaskRunner<Void, Void, TaskResult> {
	

	
	@Override
	public Void onPreExecute(Void v) {
		return null;
	}


	@Override
	public TaskResult doInBackground(Void v) throws Exception {
		TaskResult result = null;
		ObjectInputStream ois = null;
		FileInputStream fis = null;
		try{
			result = new TaskResult();
			
			File file = new File("config.dat");
			if( file.exists() ){
				fis = new FileInputStream(file);
				ois = new ObjectInputStream(fis);
				String[] fieldTypeList = (String[]) ois.readObject();
				
				result.setSuccess(true);
				result.setData(fieldTypeList);
			}else{
				result.setSuccess(false);
			}
				
		}catch(Exception e){
			throw new TaskException("[변수타입 설정 로드 에러] log !!!");
		}finally {
			if(ois != null )ois.close();
			if(fis != null )fis.close();
		}
		
		return result;
	}

	@Override
	public TaskResult onPostExecute(TaskResult result) {
		return result;
	}
	

}
