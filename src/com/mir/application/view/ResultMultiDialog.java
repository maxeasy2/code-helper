package com.mir.application.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ST;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class ResultMultiDialog extends Dialog {

	protected Object result;
	protected Shell shell;
	private StyledText text;
	private String title;
	private StyledText styledText;
	private StyledText styledText_1;
	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public ResultMultiDialog(Shell parent, int style, String title) {
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
		shell.setSize(879, 590);
		shell.setText(getText());
		
		shell.setSize(762, 532);
		shell.setText(title);
		
		Composite composite = new Composite(shell, SWT.NONE);
		composite.setBounds(0, 0, 746, 494);
		
		styledText = new StyledText(composite, SWT.BORDER);
		styledText.setText("<dynamic>");
		styledText.setBounds(225, 10, 335, 494);
		
		text = new StyledText(composite, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
		text.setBounds(339, 10, 335, 494);
		text.setText(value);
		
		styledText_1 = new StyledText(composite, SWT.BORDER);
		styledText_1.setText("<dynamic>");
		styledText_1.setBounds(0, 0, 335, 494);
		text.setKeyBinding('A'| SWT.CONTROL, ST.SELECT_ALL);
		text.setKeyBinding('C' | SWT.CONTROL, ST.COPY);
		text.setKeyBinding('V' | SWT.CONTROL, ST.PASTE);
		text.setKeyBinding('X' | SWT.CONTROL, ST.CUT);

	}

}
