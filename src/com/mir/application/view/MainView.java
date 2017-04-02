package com.mir.application.view;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ST;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.mir.application.Constant;
import com.mir.application.task.TaskCallBack;
import com.mir.application.task.TaskRunner;
import com.mir.application.task.component.SqlParseTask;
import com.mir.application.task.vo.SourceCode;
import com.mir.application.task.vo.TaskResult;
import com.mir.application.util.ASyncDialogTasker;
import com.mir.application.view.vo.MainViewVo;

public class MainView {

	protected Shell shlCodeHelperV;
	private StyledText  sqlValue;
	private Label lblFieldType;
	private Label lblPackage;
	private Text packageName;
	private Label lblClassName;
	private Text className;
	private Combo generateType;
	private Button getter,setter;
	
	private int toStringValue = 0;
	
	private int fieldNamingValue = 0;
	private Label lblTostring;
	private Button baseForm,arrayForm,condition;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			MainView window = new MainView();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shlCodeHelperV.open();
		shlCodeHelperV.layout();
		while (!shlCodeHelperV.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shlCodeHelperV = new Shell(SWT.CLOSE | SWT.MIN | SWT.TITLE);
		shlCodeHelperV.setSize(483, 734);
		shlCodeHelperV.setText(Constant.APPLICATION_NAME+" ["+Constant.APPLICATION_VERSION+"]");
		shlCodeHelperV.setLayout(null);
		
		Composite composite = new Composite(shlCodeHelperV, SWT.BORDER);
		composite.setBounds(10, 10, 456, 686);
		composite.setLayout(null);
		
		sqlValue = new StyledText (composite, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		sqlValue.setBounds(10, 373, 432, 299);
		sqlValue.setKeyBinding('A'| SWT.CONTROL, ST.SELECT_ALL);
		sqlValue.setKeyBinding('C' | SWT.CONTROL, ST.COPY);
		sqlValue.setKeyBinding('V' | SWT.CONTROL, ST.PASTE);
		sqlValue.setKeyBinding('X' | SWT.CONTROL, ST.CUT);
		
		Label lblSql = new Label(composite, SWT.NONE);
		lblSql.setBounds(10, 352, 74, 15);
		lblSql.setText("SQL Value :");
		
		lblPackage = new Label(composite, SWT.NONE);
		lblPackage.setBounds(10, 9, 87, 15);
		lblPackage.setText("Package name :");
		
		packageName = new Text(composite, SWT.BORDER);
		packageName.setBounds(105, 6, 337, 21);
		
		lblClassName = new Label(composite, SWT.NONE);
		lblClassName.setBounds(10, 43, 87, 15);
		lblClassName.setText("Class name : ");
		
		className = new Text(composite, SWT.BORDER);
		className.setBounds(105, 40, 337, 21);
		
		lblFieldType = new Label(composite, SWT.NONE);
		lblFieldType.setBounds(10, 177, 87, 15);
		lblFieldType.setText("Field Naming : ");
		
		Button btnNewButton = new Button(composite, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(StringUtils.isEmpty(sqlValue.getText())){
					MessageBox dialog = new MessageBox(shlCodeHelperV, SWT.ICON_ERROR | SWT.OK);
					dialog.setText("Error");
					dialog.setMessage("SQL 값을 넣어야 합니다");
					dialog.open();
				}else{
					MainViewVo vo = new MainViewVo();
					vo.setClassName(className.getText());
					vo.setPackageName(packageName.getText());
					vo.setFieldName(fieldNamingValue);
					vo.setSqlValue(sqlValue.getText());
					vo.setGenerateType(generateType.getSelectionIndex());
					vo.setToStingType(toStringValue);
					vo.setGetter(getter.getSelection());
					vo.setSetter(setter.getSelection());
					vo.setBaseFrom(baseForm.getSelection());
					vo.setArrayForm(arrayForm.getSelection());
					vo.setCondition(condition.getSelection());
					TaskRunner run = new SqlParseTask();
					
					run.executeTask(vo, new TaskCallBack() {
						public void processResult(final TaskResult result) {
							Display.getDefault().syncExec(new Runnable() {
								public void run() {
									if(result != null){
										if(result.getResultType() == Constant.GENERATE_TYPES_VO){
											ResultDialog dialog = new ResultDialog(shlCodeHelperV, shlCodeHelperV.getStyle(), "VO");
											dialog.open(result.getData().toString());
										}else{
											final List<SourceCode> resultDataList = (List<SourceCode>) result.getData();
											ResultDialog[] dialog = new ResultDialog[resultDataList.size()];
											for(int i=0; i<dialog.length; i++){
												dialog[i] = new ResultDialog(shlCodeHelperV, shlCodeHelperV.getStyle(),resultDataList.get(i).getName());
												Thread thread = new ASyncDialogTasker(dialog[i],  resultDataList.get(i).getValue()) ;
												thread.start();
											}
										}
									}else{
											MessageBox dialog = new MessageBox(shlCodeHelperV, SWT.ICON_ERROR | SWT.OK);
											dialog.setText("Error");
											dialog.setMessage("SQL 구문 에러");
											dialog.open();
									}
								}
							});
						}
					});
					
				}
			}
		});
		btnNewButton.setBounds(270, 215, 172, 133);
		btnNewButton.setText("코드생성");
		
		Label lblGenerateType = new Label(composite, SWT.NONE);
		lblGenerateType.setBounds(10, 80, 87, 15);
		lblGenerateType.setText("Generate Type :");
		
		generateType = new Combo(composite, SWT.READ_ONLY);
		generateType.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(generateType.getSelectionIndex() == 0 ){
					baseForm.setSelection(false);
					arrayForm.setSelection(false);
					condition.setSelection(false);
					baseForm.setEnabled(false);
					arrayForm.setEnabled(false);
					condition.setEnabled(false);
				}else{
					baseForm.setEnabled(true);
					arrayForm.setEnabled(true);
					condition.setEnabled(true);
				}
			}
		});
		generateType.setBounds(105, 77, 337, 23);
		generateType.setItems(Constant.GENERATE_TYPES);
		generateType.select(0);
		
		Group group = new Group(composite, SWT.NONE);
		group.setBounds(105, 106, 337, 45);
		
		
		Button toStringNone = new Button(group, SWT.RADIO);
		toStringNone.setSelection(true);
		toStringNone.setLocation(10, 19);
		toStringNone.setSize(55, 16);
		toStringNone.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				toStringValue = Constant.TOSTRING_NONE;
			}
		});
		toStringNone.setText("None");
		
		Button toStringWrite = new Button(group, SWT.RADIO);
		toStringWrite.setLocation(66, 19);
		toStringWrite.setSize(51, 16);
		toStringWrite.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				toStringValue = Constant.TOSTRING_WRITE;
			}
		});
		toStringWrite.setText("Write");
		
		Button toStringCommons3 = new Button(group, SWT.RADIO);
		toStringCommons3.setLocation(118, 19);
		toStringCommons3.setSize(200, 16);
		toStringCommons3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				toStringValue = Constant.TOSTRING_COMMONLANG3;
			}
		});
		toStringCommons3.setText("commons-lang (ToStringBuilder)");
		
		lblTostring = new Label(composite, SWT.NONE);
		lblTostring.setBounds(10, 125, 74, 15);
		lblTostring.setText("toString() :");
		
		Group group_1 = new Group(composite, SWT.NONE);
		group_1.setBounds(105, 157, 337, 45);
		
		Button camel = new Button(group_1, SWT.RADIO);
		camel.setBounds(10, 20, 55, 16);
		camel.setSelection(true);
		camel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				fieldNamingValue = Constant.FIELD_NAMING_CAMEL;
			}
		});
		camel.setText("Camel");
		
		Button scheme = new Button(group_1, SWT.RADIO);
		scheme.setBounds(73, 20, 64, 16);
		scheme.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				fieldNamingValue = Constant.FIELD_NAMING_RESULTSET;
			}
		});
		scheme.setText("ResultSet");
		
		Button lowcase = new Button(group_1, SWT.RADIO);
		lowcase.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				fieldNamingValue = Constant.FIELD_NAMING_LOWCASE;
			}
		});
		lowcase.setText("Lowcase");
		lowcase.setBounds(152, 20, 64, 16);
		
		Button upcase = new Button(group_1, SWT.RADIO);
		upcase.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				fieldNamingValue = Constant.FIELD_NAMING_UPCASE;
			}
		});
		upcase.setText("Upcase");
		upcase.setBounds(222, 20, 64, 16);
		
		Label lblGetterSetter = new Label(composite, SWT.NONE);
		lblGetterSetter.setBounds(10, 225, 87, 15);
		lblGetterSetter.setText("getter / setter :");
		
		Group group_2 = new Group(composite, SWT.NONE);
		group_2.setBounds(105, 208, 159, 45);
		
		getter = new Button(group_2, SWT.CHECK);
		getter.setBounds(10, 19, 52, 16);
		getter.setText("getter");
		
		setter = new Button(group_2, SWT.CHECK);
		setter.setBounds(76, 19, 59, 16);
		setter.setText("setter");
		
		Label lblShoppingOption = new Label(composite, SWT.NONE);
		lblShoppingOption.setBounds(10, 298, 100, 15);
		lblShoppingOption.setText("Shopping Option :");
		
		Group shoppingOption = new Group(composite, SWT.NONE);
		shoppingOption.setBounds(115, 259, 149, 89);
		
		baseForm = new Button(shoppingOption, SWT.CHECK);
		baseForm.setBounds(10, 19, 74, 16);
		baseForm.setText("Base Form");
		
		arrayForm = new Button(shoppingOption, SWT.CHECK);
		arrayForm.setBounds(10, 41, 81, 16);
		arrayForm.setText("Array Form");
		
		condition = new Button(shoppingOption, SWT.CHECK);
		condition.setBounds(10, 63, 74, 16);
		condition.setText("Condition");
		baseForm.setEnabled(false);
		arrayForm.setEnabled(false);
		condition.setEnabled(false);

	}
}
