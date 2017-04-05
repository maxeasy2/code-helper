package com.mir.application.util;

import org.eclipse.swt.widgets.Display;

import com.mir.application.view.ResultDialog;

public class MultilDialogTasker extends Thread{

	private ResultDialog resultDialog;
	private String resultValue;
	
	public MultilDialogTasker(ResultDialog resultDialog, String resultValue) {
		this.resultDialog = resultDialog;
		this.resultValue = resultValue;
	}
	
	@Override
	public void run() {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				resultDialog.open(resultValue);
			}
		});
	}
}
