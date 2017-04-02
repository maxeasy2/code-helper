package com.mir.application.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ST;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class ResultDialog extends Dialog implements ASyncDialog {

	protected Object result;
	protected Shell shell;
	private StyledText text;
	private String title;
	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public ResultDialog(Shell parent, int style, String title) {
		super(parent, style);
		this.title = title;
	}
	


	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open(final String value) {
		createContents(value);
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
	private void createContents(String value) {
		shell = new Shell(getParent(), getStyle());
		shell.setSize(450, 300);
		shell.setText(getText());
		
		shell.setSize(762, 532);
		shell.setText(title);
		
		Composite composite = new Composite(shell, SWT.NONE);
		composite.setBounds(0, 0, 746, 494);
		
		text = new StyledText(composite, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
		text.setBounds(0, 0, 750, 500);
		text.setText(value);
		text.setKeyBinding('A'| SWT.CONTROL, ST.SELECT_ALL);
		text.setKeyBinding('C' | SWT.CONTROL, ST.COPY);
		text.setKeyBinding('V' | SWT.CONTROL, ST.PASTE);
		text.setKeyBinding('X' | SWT.CONTROL, ST.CUT);

	}

}
