package com.mir.application.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ST;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.mir.application.util.CommonUtils;

import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class ResultSqlDialog extends Dialog {

	protected Object result;
	protected Shell shell;
	private StyledText text;
	private String title, value;
	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public ResultSqlDialog(Shell parent, int style, String title) {
		super(parent, style);
		this.title = title;
	}
	


	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open(String value) {
		this.value = value;
		createContents();
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}
	/*
	public void open(final String value) {
		final Display display = Display.getDefault();
		display.getDefault().syncExec(new Runnable() {

			public void run() {
				setText(title);
				createContents(value);
				shell.open();
				shell.layout();
				while (!shell.isDisposed()) {
					if (!display.readAndDispatch()) {
						display.sleep();
					}
				}
			}
			
		});
	}
*/
	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), getStyle());
		shell.setSize(450, 300);
		shell.setText(getText());
		
		shell.setSize(762, 532);
		shell.setText(title);
		
		Composite composite = new Composite(shell, SWT.NONE);
		composite.setBounds(0, 0, 746, 494);
		
		text = new StyledText(composite, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
		text.setBounds(0, 48, 750, 452);
		//text.setText(value);
		text.setText(CommonUtils.replaceFormatSql(value,"{","}")); 
		
		Group group = new Group(composite, SWT.NONE);
		group.setBounds(0, -11, 784, 63);
		
		Button shopNomal = new Button(group, SWT.RADIO);
		shopNomal.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				text.setText(CommonUtils.replaceFormatSql(value,"{","}")); 
			}
		});
		shopNomal.setSelection(true);
		shopNomal.setBounds(122, 26, 67, 16);
		shopNomal.setText("{sample}");
		
		Label lblNewLabel = new Label(group, SWT.NONE);
		lblNewLabel.setBounds(10, 27, 96, 15);
		lblNewLabel.setText("SQL변수 format :");
		
		Button shopPrepared = new Button(group, SWT.RADIO);
		shopPrepared.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				text.setText(CommonUtils.replaceFormatSql(value,"{@","}")); 
			}
		});
		shopPrepared.setBounds(209, 26, 91, 16);
		shopPrepared.setText("{@sample}");
		
		Button shopStatement = new Button(group, SWT.RADIO);
		shopStatement.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				text.setText(CommonUtils.replaceFormatSql(value,"{#","}")); 
			}
		});
		shopStatement.setBounds(306, 26, 74, 16);
		shopStatement.setText("{#sample}");
		
		Button mybatisPrepared = new Button(group, SWT.RADIO);
		mybatisPrepared.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				text.setText(CommonUtils.replaceFormatSql(value,"#{","}")); 
			}
		});
		mybatisPrepared.setBounds(396, 26, 74, 16);
		mybatisPrepared.setText("#{sample}");
		
		Button mybatisStatement = new Button(group, SWT.RADIO);
		mybatisStatement.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				text.setText(CommonUtils.replaceFormatSql(value,"\\${","}")); 
			}
		});
		mybatisStatement.setBounds(484, 26, 91, 16);
		mybatisStatement.setText("${sample}");
		text.setKeyBinding('A'| SWT.CONTROL, ST.SELECT_ALL);
		text.setKeyBinding('C' | SWT.CONTROL, ST.COPY);
		text.setKeyBinding('V' | SWT.CONTROL, ST.PASTE);
		text.setKeyBinding('X' | SWT.CONTROL, ST.CUT);

	}
	
}
