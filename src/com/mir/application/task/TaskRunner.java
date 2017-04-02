package com.mir.application.task;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mir.application.task.vo.TaskResult;
import com.mir.application.util.ASyncDialogTasker;
import com.mir.application.view.LoadDialog;

@SuppressWarnings("hiding")
public abstract class TaskRunner<Params, Value, Process> implements Runnable{
	
	private static final Logger logger = LoggerFactory.getLogger(TaskRunner.class); 
	
	private Value value;
	
	private TaskCallBack taskCallBack = null;

	public abstract Value onPreExecute(Params params);
	
	public abstract Process doInBackground(Value value) throws Exception;
	
	public abstract TaskResult onPostExecute(Process process);
	
	private boolean loadDialogUse = false;
	
	private Shell shell = null;
	
	private LoadDialog loadDialog;

	public void run() {
		String errorMsg = null;
		Process process = null;
		try {
			process = this.doInBackground(value);
		} catch (Exception e) {
			e.printStackTrace();
			errorMsg = e.getMessage();
			logger.error(errorMsg);
		}finally {
			TaskResult result = this.onPostExecute(process);
			if(taskCallBack != null){
				if(result == null ){
					result = new TaskResult();
					result.setSuccess(false);
					result.setResultMsg(errorMsg);
				}
				taskCallBack.processResult(result);
			}
			
			if( loadDialogUse ){
				Display.getDefault().syncExec(new Runnable() {
					public void run() {
						loadDialog.close();
					}
				});
			}
		}
		
	}
	
	public void executeTask(Params params){
		value = this.onPreExecute(params);
		executeTask();
	}
	
	public void executeTask(Params params, TaskCallBack taskCallBack){
		this.taskCallBack = taskCallBack;
		executeTask(params);
	}
	
	public void executeTask(TaskCallBack taskCallBack){
		this.taskCallBack = taskCallBack;
		this.onPreExecute(null);
		executeTask();
	}
	
	public void executeTask(){
		if( loadDialogUse ){
			loadDialog = new LoadDialog(shell, shell.getStyle());
			Thread multiDialogTasker = new ASyncDialogTasker(loadDialog,  "") ;
			multiDialogTasker.start();
		}
		
		Thread thread = new Thread(this);
		thread.start();
	}

	public void setLoadDialogUse(Shell shell) {
		if( shell != null ){
			this.loadDialogUse = true;
			this.shell = shell;
		}
	}

}
