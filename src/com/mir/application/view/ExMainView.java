package com.mir.application.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.ST;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.mir.application.Constant;
import com.mir.application.task.TaskCallBack;
import com.mir.application.task.TaskRunner;
import com.mir.application.task.component.FieldTypeConfTask;
import com.mir.application.task.component.FieldTypeLoadTask;
import com.mir.application.task.component.FieldTypeSaveTask;
import com.mir.application.task.component.JsonToJavaGeneratorTask;
import com.mir.application.task.component.SqlToJavaGeneratorTask;
import com.mir.application.task.vo.SourceCode;
import com.mir.application.task.vo.TaskResult;
import com.mir.application.util.ASyncDialogTasker;
import com.mir.application.view.vo.MainViewVo;

public class ExMainView {

	protected Shell shlCodeHelperV;
	private StyledText  dataValue;
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
	private Table table;
	private Text fieldInput;
	private org.eclipse.swt.widgets.List fieldList;
	
	private String[] fieldTypeSelect;
	private String compareSqlValue = "";
	private int dataValueType = 0;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			ExMainView window = new ExMainView();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		TaskRunner fieldTypeLoad = new FieldTypeLoadTask();
		fieldTypeLoad.executeTask(new TaskCallBack() {
			
			public void processResult(TaskResult value) {
				if(value.isSuccess()){
					fieldTypeSelect = (String[]) value.getData();
				}else{
					fieldTypeSelect = Constant.DEFAULT_FIELD_TYPES;
				}
				
			}
		});
		Display display = Display.getDefault();
		createContents();
		shlCodeHelperV.open();
		shlCodeHelperV.layout();
		Monitor primaryMonitor = Display.getDefault().getPrimaryMonitor();
		int x = (primaryMonitor.getBounds().width - shlCodeHelperV.getSize().x) / 2;
		int y = (primaryMonitor.getBounds().height - shlCodeHelperV.getSize().y) / 2;
		shlCodeHelperV.setLocation(x, y);
		
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
		shlCodeHelperV.setSize(804, 734);
		shlCodeHelperV.setText(Constant.APPLICATION_NAME+" ["+Constant.APPLICATION_VERSION+"]");
		shlCodeHelperV.setLayout(new FormLayout());

		Composite composite = new Composite(shlCodeHelperV, SWT.BORDER);
		FormData fd_composite = new FormData();
		fd_composite.bottom = new FormAttachment(0, 696);
		fd_composite.right = new FormAttachment(0, 466);
		fd_composite.top = new FormAttachment(0, 10);
		fd_composite.left = new FormAttachment(0, 10);
		composite.setLayoutData(fd_composite);
		composite.setLayout(null);
		
		dataValue = new StyledText (composite, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		dataValue.setBounds(10, 426, 432, 246);
		dataValue.setKeyBinding('A'| SWT.CONTROL, ST.SELECT_ALL);
		dataValue.setKeyBinding('C' | SWT.CONTROL, ST.COPY);
		dataValue.setKeyBinding('V' | SWT.CONTROL, ST.PASTE);
		dataValue.setKeyBinding('X' | SWT.CONTROL, ST.CUT);
		
		Label lblSql = new Label(composite, SWT.NONE);
		lblSql.setBounds(10, 373, 100, 15);
		lblSql.setText("Data Value Type :");
		
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
				if(StringUtils.isEmpty(dataValue.getText())){
					openErrorDialog("값을 넣어야 합니다");
				}else{
					MainViewVo vo = new MainViewVo();
					vo.setClassName(className.getText());
					vo.setPackageName(packageName.getText());
					vo.setFieldName(fieldNamingValue);
					vo.setSqlValue(dataValue.getText());
					vo.setGenerateType(generateType.getSelectionIndex());
					vo.setToStingType(toStringValue);
					vo.setGetter(getter.getSelection());
					vo.setSetter(setter.getSelection());
					vo.setBaseFrom(baseForm.getSelection());
					vo.setArrayForm(arrayForm.getSelection());
					vo.setCondition(condition.getSelection());
					vo.setDataValueType(dataValueType);
					
					if( dataValueType == Constant.DATA_VALUE_TYPE_SQL ){	//sql
						TableItem[] tableItems = table.getItems();
						Control[] controlItems = table.getChildren();
						String dataTypes = StringUtils.join(Constant.DEFAULT_FIELD_TYPES,"|");
						if(generateType.getSelectionIndex() == Constant.GENERATE_TYPES_DTO){
							for(int i=0; i<tableItems.length; i++){
								CCombo combo = (CCombo) controlItems[i];
								if(!combo.getText().matches("(?i)"+dataTypes)){
									openErrorDialog("DTO 생성시 기본형타입만 설정가능 합니다!");
									return;
								}
							}
						}
						
						boolean fieldTypeUse = false;
						Map<String,String> fieldTypeMap = null;
						if(tableItems.length > 0 && controlItems.length > 0 && compareSqlValue.equals(dataValue.getText())){
							fieldTypeMap = new HashMap<String,String>();
							for(int i=0; i<tableItems.length; i++){
								CCombo combo = (CCombo) controlItems[i];
								fieldTypeMap.put(tableItems[i].getText(2), combo.getText());
							}
							fieldTypeUse = true;
						}else{
							Control [] controls = table.getChildren();
							for(Control control : controls){
								if(control instanceof CCombo){
									control.dispose();
								}
							}
							table.removeAll();
						}
						
						vo.setFieldTypeMap(fieldTypeMap);
						vo.setFieldTypeUse(fieldTypeUse);
						TaskRunner run = new SqlToJavaGeneratorTask();
						
						run.executeTask(vo, new TaskCallBack() {
							public void processResult(final TaskResult result) {
								Display.getDefault().syncExec(new Runnable() {
									public void run() {
										if(result.isSuccess()){
											if(result.getResultType() == Constant.GENERATE_TYPES_VO){
												ResultDialog dialog = new ResultDialog(shlCodeHelperV, shlCodeHelperV.getStyle(), "VO");
												dialog.open(result.getData().toString()); 
											}else if(result.getResultType() == Constant.GENERATE_TYPES_DTO){
												final List<SourceCode> resultDataList = (List<SourceCode>) result.getData();
												ResultDialog[] dialog = new ResultDialog[resultDataList.size()];
												for(int i=0; i<dialog.length; i++){
													dialog[i] = new ResultDialog(shlCodeHelperV, shlCodeHelperV.getStyle(),resultDataList.get(i).getName());
													Thread thread = new ASyncDialogTasker(dialog[i],  resultDataList.get(i).getValue()) ;
													thread.start();
												}
											}else {
												ResultSqlDialog dialog = new ResultSqlDialog(shlCodeHelperV, shlCodeHelperV.getStyle(), "SQL");
												dialog.open(result.getData().toString());
											}
										}else{
											openErrorDialog("SQL 구문 에러");
										}
									}
								});
							}
						});
						
					}else{	//json
						if(vo.getGenerateType() == Constant.GENERATE_TYPES_VO){
							TaskRunner run = new JsonToJavaGeneratorTask();
							run.setLoadDialogUse(shlCodeHelperV);
							run.executeTask(vo, new TaskCallBack() {

								public void processResult(final TaskResult result) {
									Display.getDefault().syncExec(new Runnable() {
										public void run() {
											if(result.isSuccess()){
												final List<SourceCode> resultDataList = (List<SourceCode>) result.getData();
												ResultDialog[] dialog = new ResultDialog[resultDataList.size()];
												for(int i=0; i<dialog.length; i++){
													dialog[i] = new ResultDialog(shlCodeHelperV, shlCodeHelperV.getStyle(),resultDataList.get(i).getName());
													Thread thread = new ASyncDialogTasker(dialog[i],  resultDataList.get(i).getValue()) ;
													thread.start();
												}
											}else{
												openErrorDialog(result.getResultMsg());
											}
										}
									});
								}
								
							});
						}else{
							openErrorDialog("Generate Type > VO 타입만 생성가능합니다");
						}
						
					}
						
				}
			}
		});
		btnNewButton.setBounds(270, 264, 172, 156);
		btnNewButton.setText("코드생성");
		
		Label lblGenerateType = new Label(composite, SWT.NONE);
		lblGenerateType.setBounds(10, 80, 87, 15);
		lblGenerateType.setText("Generate Type :");
		
		generateType = new Combo(composite, SWT.READ_ONLY);
		generateType.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(generateType.getSelectionIndex() == 1 ){
					baseForm.setEnabled(true);
					arrayForm.setEnabled(true);
					condition.setEnabled(true);
				}else{
					baseForm.setSelection(false);
					arrayForm.setSelection(false);
					condition.setSelection(false);
					baseForm.setEnabled(false);
					arrayForm.setEnabled(false);
					condition.setEnabled(false);
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
		toStringNone.setSize(46, 16);
		toStringNone.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				toStringValue = Constant.TOSTRING_NONE;
			}
		});
		toStringNone.setText("None");
		
		Button toStringWrite = new Button(group, SWT.RADIO);
		toStringWrite.setLocation(66, 19);
		toStringWrite.setSize(46, 16);
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
		lowcase.setBounds(149, 20, 64, 16);
		
		Button upcase = new Button(group_1, SWT.RADIO);
		upcase.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				fieldNamingValue = Constant.FIELD_NAMING_UPCASE;
			}
		});
		upcase.setText("Upcase");
		upcase.setBounds(219, 20, 64, 16);
		
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
		lblShoppingOption.setBounds(10, 298, 80, 15);
		lblShoppingOption.setText("milti Option :");
		
		Group shoppingOption = new Group(composite, SWT.NONE);
		shoppingOption.setBounds(105, 259, 159, 89);
		
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
		
		Composite composite_1 = new Composite(shlCodeHelperV, SWT.NONE);
		composite_1.setBounds(475, 10, 64, 64);
		FormData fd_composite_1 = new FormData();
		fd_composite_1.bottom = new FormAttachment(composite, 0, SWT.BOTTOM);
		fd_composite_1.left = new FormAttachment(composite, 12);
		
		Button btnFieldTypeConfing = new Button(composite, SWT.NONE);
		btnFieldTypeConfing.addSelectionListener(new SelectionAdapter() {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public void widgetSelected(SelectionEvent e) {
				if( dataValueType == Constant.DATA_VALUE_TYPE_SQL ){	//sql
					final MainViewVo vo = new MainViewVo();
					vo.setFieldName(fieldNamingValue);
					vo.setSqlValue(dataValue.getText());
					
					TaskRunner taskRunner = new FieldTypeConfTask();
					taskRunner.executeTask(vo, new TaskCallBack() {
						
						public void processResult(TaskResult value) {
							if(value.isSuccess()){
								final List<String> fieldList = (List<String>) value.getData();
								
								Display.getDefault().syncExec(new Runnable() {
									public void run() {
										Control [] controls = table.getChildren();
										for(Control control : controls){
											if(control instanceof CCombo){
												control.dispose();
											}
										}
										
										table.removeAll();
										for(String fieldName : fieldList){
											new TableItem(table, SWT.NONE);
										}
										
										TableItem[] items = table.getItems();
										int i = 0;
										for(TableItem item : items){
											TableEditor editor = new TableEditor(table);
											CCombo combo = new CCombo(table, SWT.NONE);
											
											if(fieldTypeSelect == null){
												fieldTypeSelect = Constant.DEFAULT_FIELD_TYPES;
											}
											for(String fieldType : fieldTypeSelect){
												combo.add(fieldType);
											}
											combo.select(0);
											editor.grabHorizontal = true;
											item.setText(0, String.valueOf(i+1));
											editor.setEditor(combo, item, 1);
											item.setText(2, fieldList.get(i));
											i++;
										}
										compareSqlValue = vo.getSqlValue();
									}
								});
							}else{
								openErrorDialog("SQL 구문 에러");
							}
							
						}
					});
				}else{
					openErrorDialog("Data Value Type > SQL 타입만 설정 가능합니다.");
				}
			}
		});
		btnFieldTypeConfing.setBounds(270, 215, 172, 37);
		btnFieldTypeConfing.setText("변수 타입 설정");
		fd_composite_1.right = new FormAttachment(0, 785);
		composite_1.setLayoutData(fd_composite_1);
		TableColumnLayout tcl_composite_1 = new TableColumnLayout();
		composite_1.setLayout(tcl_composite_1);
		
		TableViewer tableViewer = new TableViewer(composite_1, SWT.BORDER | SWT.FULL_SELECTION);
		table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		TableColumn numberColumn = new TableColumn(table, SWT.NONE);
		tcl_composite_1.setColumnData(numberColumn, new ColumnPixelData(36, true, true));
		numberColumn.setText("번호");
		
		TableViewerColumn tableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn type = tableViewerColumn.getColumn();
		type.setResizable(false);
		tcl_composite_1.setColumnData(type, new ColumnPixelData(97, true, true));
		type.setText("타입");
		
		TableViewerColumn tableViewerColumn_2 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn fieldName = tableViewerColumn_2.getColumn();
		fieldName.setResizable(false);
		fieldName.setText("변수명");
		tcl_composite_1.setColumnData(fieldName, new ColumnPixelData(198, true, true));
		
		Composite composite_2 = new Composite(shlCodeHelperV, SWT.BORDER);
		FormData fd_composite_2 = new FormData();
		fd_composite_2.top = new FormAttachment(0, 10);
		fd_composite_2.right = new FormAttachment(100, -13);
		fd_composite_2.left = new FormAttachment(composite, 12);
		composite_2.setLayoutData(fd_composite_2);
		
		Label label = new Label(composite_2, SWT.NONE);
		label.setText("변수타입 추가 : ");
		label.setBounds(10, 10, 125, 15);
		
		fieldList = new org.eclipse.swt.widgets.List(composite_2, SWT.BORDER | SWT.V_SCROLL);
		fieldList.setBounds(10, 59, 196, 98);
		fieldList.setItems(fieldTypeSelect);
		
		Button btnDefault = new Button(composite_2, SWT.NONE);
		btnDefault.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				fieldList.setItems(Constant.DEFAULT_FIELD_TYPES);
			}
		});
		btnDefault.setBounds(217, 70, 76, 25);
		btnDefault.setText("Default");
		
		Button btnSave = new Button(composite_2, SWT.NONE);
		btnSave.addSelectionListener(new SelectionAdapter() {
			@SuppressWarnings({ "rawtypes", "unchecked" })
			@Override
			public void widgetSelected(SelectionEvent e) {
				String[] fieldListArray = fieldList.getItems();
				TaskRunner fieldConfingSave = new FieldTypeSaveTask();
				fieldConfingSave.executeTask(fieldListArray, new TaskCallBack() {
					
					public void processResult(TaskResult value) {
						if(value.isSuccess()){
							fieldTypeSelect = (String[]) value.getData();
						}
					}
				});
			}
		});
		btnSave.setBounds(217, 132, 76, 25);
		btnSave.setText("Save");
		
		Button btnDelete = new Button(composite_2, SWT.NONE);
		btnDelete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(fieldList.getSelectionIndex() > -1){
					fieldList.remove(fieldList.getSelectionIndex());
				}else{
					openErrorDialog("삭제할 타입을 선택하세요!");
				}
			}
		});
		btnDelete.setBounds(217, 101, 76, 25);
		btnDelete.setText("Delete");
		
		fieldInput = new Text(composite_2, SWT.BORDER);
		fieldInput.setBounds(10, 31, 196, 21);
		
		Button btnAdd = new Button(composite_2, SWT.NONE);
		btnAdd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(StringUtils.isEmpty(fieldInput.getText())){
					openErrorDialog("추가할 타입을 입력하세요!");
				}else{
					fieldList.add(fieldInput.getText());
				}
			}
		});
		btnAdd.setBounds(217, 29, 76, 25);
		btnAdd.setText("Add");
		
		Label lbldefult = new Label(shlCodeHelperV, SWT.NONE);
		fd_composite_2.bottom = new FormAttachment(lbldefult, -20);
		fd_composite_1.top = new FormAttachment(0, 222);
		FormData fd_lbldefult = new FormData();
		fd_lbldefult.bottom = new FormAttachment(composite_1, -6);
		fd_lbldefult.left = new FormAttachment(composite, 12);
		
		Group group_3 = new Group(composite, SWT.NONE);
		group_3.setBounds(116, 354, 148, 66);
		
		Button btnRadioButton = new Button(group_3, SWT.RADIO);
		btnRadioButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dataValueType = Constant.DATA_VALUE_TYPE_SQL;
			}
		});
		btnRadioButton.setSelection(true);
		btnRadioButton.setBounds(10, 19, 44, 16);
		btnRadioButton.setText("SQL");
		
		Button btnRadioButton_1 = new Button(group_3, SWT.RADIO);
		btnRadioButton_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dataValueType = Constant.DATA_VALUE_TYPE_JSON;
			}
		});
		btnRadioButton_1.setBounds(60, 19, 44, 16);
		btnRadioButton_1.setText("JSON");
		
		Button jsonJacksonRadioBtn = new Button(group_3, SWT.RADIO);
		jsonJacksonRadioBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dataValueType = Constant.DATA_VALUE_TYPE_JSONJACKSON;
			}
		});
		jsonJacksonRadioBtn.setBounds(10, 41, 115, 16);
		jsonJacksonRadioBtn.setText("JSON (Jackson@)");
		lbldefult.setLayoutData(fd_lbldefult);
		lbldefult.setText("타입 설정 (Default : String) ");

	}
	
	private void openErrorDialog(final String message){
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				MessageBox dialog = new MessageBox(shlCodeHelperV, SWT.ICON_ERROR | SWT.OK);
				dialog.setText("Error");
				dialog.setMessage(message);
				dialog.open();
			}
		});
	}
}
