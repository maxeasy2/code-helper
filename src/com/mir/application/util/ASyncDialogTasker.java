package com.mir.application.util;

import org.eclipse.swt.widgets.Display;

import com.mir.application.view.ASyncDialog;

public class ASyncDialogTasker extends Thread{

	private ASyncDialog dialog;
	private String value;
	
	public ASyncDialogTasker(ASyncDialog dialog, String value) {
		this.dialog = dialog;
		this.value = value;
	}
	
	@Override
	public void run() {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				dialog.open(value);
			}
		});
	}
}
