package com.mir.application.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ST;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class ResultView {

	protected Shell shlResult;
	private StyledText text;
	private String title;
	
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			ResultView window = new ResultView();
			window.open("");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ResultView() {
		this.title = "Result";
	}
	
	public ResultView(String title) {
		this.title = title;
	}
	
	/**
	 * Open the window.
	 */
	public void open(final String value) {
		final Display display = Display.getDefault();
		display.getDefault().syncExec(new Runnable() {

			public void run() {
				createContents(value);
				shlResult.open();
				shlResult.layout();
				while (!shlResult.isDisposed()) {
					if (!display.readAndDispatch()) {
						display.sleep();
					}
				}
			}
			
		});
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents(String value) {
		shlResult = new Shell(SWT.CLOSE | SWT.MIN | SWT.TITLE);
		shlResult.setSize(762, 532);
		shlResult.setText(title);
		
		Composite composite = new Composite(shlResult, SWT.NONE);
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
