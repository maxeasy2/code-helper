package com.mir.application.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class LoadDialog extends Dialog implements ASyncDialog{

	protected Object result;
	protected Shell shell;
	Display display;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public LoadDialog(Shell parent, int style) {
		super(parent, style);
		setText("SWT Dialog");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open(String value) {
		createContents();
		shell.open();
		shell.layout();
		display = getParent().getDisplay();
		int x = (display.getBounds().width - shell.getSize().x) / 2;
		int y = (display.getBounds().height - shell.getSize().y) / 2;
		shell.setLocation(x, y);
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}
	
	public void close(){
		shell.dispose();
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), SWT.NONE);
		shell.setSize(228, 91);
		shell.setText(getText());
		
		Composite composite = new Composite(shell, SWT.BORDER);
		composite.setBounds(10, 10, 206, 70);
		
		Label lblNewLabel = new Label(composite, SWT.NONE);
		lblNewLabel.setBounds(70, 26, 122, 15);
		lblNewLabel.setText("잠시만 기다려주세요...");
		
		
		ImageLoader loader = new ImageLoader();
		final ImageData[] frames = loader.load("./image/loading.gif");
		
		final Label lblNewLabel_1 = new Label(composite, SWT.NONE);
		lblNewLabel_1.setBounds(13, 12, 44, 44);
		//final Label label = new Label(shell, SWT.NORMAL);
		lblNewLabel_1.setImage(new Image(display, frames[0]));
	      
		Thread animation = new Thread() {
	         int currentFrame = 0;
	         boolean isDisposed = false;
	 
	         @Override
	         public void run() {
	            while (!isDisposed) {
	               try {
	                  sleep(frames[currentFrame].delayTime * 10);
	               } catch (InterruptedException e) {
	                  e.printStackTrace();
	               }
	 
	               currentFrame = (currentFrame + 1) % frames.length;
	               if (display.isDisposed()) {
	                  return;
	               }
	               display.asyncExec(new Runnable() {
	                  public void run() {
	                     try {
	                        Image newImage = new Image(display, frames[currentFrame]);
	                        lblNewLabel_1.getImage().dispose();
	                        lblNewLabel_1.setImage(newImage);
	                     } catch (SWTException e) {
	                        isDisposed = true;
	                     }
	 
	                  }
	               });
	            }
	 
	         }
	      };
	      animation.start();

	}
	
	public static void main(String[] args) {
		try {
			LoadDialog window = new LoadDialog(new Shell(),0);
			window.open("");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
