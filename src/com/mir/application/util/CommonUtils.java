package com.mir.application.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.mir.application.Constant;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;

public class CommonUtils {

	
	/**
	 * SQL 구문을 통한 변수값 추출
	 * @param sql
	 * @return
	 * @throws JSQLParserException
	 */
	public static List<String> getLastQueryField(String sql) throws JSQLParserException {
		if(StringUtils.isEmpty(sql)){
			return null;
		}
		
		String tempSql = sql;
		if(tempSql.contains("BYTE")) tempSql = tempSql.replaceAll("BYTE", "");
		
		Statement statement = CCJSqlParserUtil.parse(tempSql);
		List<String> fieldList = null;
		
		if(statement instanceof CreateTable){
			fieldList = getSqlCreateField(sql);
		}else{
			fieldList = getSqlSelectField(sql);
		}
		return fieldList;
	}
	
	public static String replaceFormatSql(String sqlValue, String prefix, String postfix){
		return sqlValue.replaceAll(Constant.SQL_VAR_EXPRESSION_PREFIX, prefix).replaceAll(Constant.SQL_VAR_EXPRESSION_POSTFIX, postfix);
	}
	
	public static List<String> getSqlSelectField(String sql) throws JSQLParserException{
		String[] errWordArr = {"{","#","\\"};
		boolean validate = false;
		for(String errWord : errWordArr){
			if(sql.contains(errWord)){
				validate = sql.contains(errWord);
				break;
			}
		}
		
		List<String> columnList = null;
		if(!validate){
			columnList = new ArrayList<String>();
			Statement statement = CCJSqlParserUtil.parse(sql);
			
			Select selectStatement = (Select) statement;
			SelectBody selectBody = selectStatement.getSelectBody();
			
			PlainSelect plainSelect = (PlainSelect)selectBody;
			
			List<SelectItem> itemList =  plainSelect.getSelectItems();
			for(SelectItem item : itemList){
				SelectExpressionItem expressItem = (SelectExpressionItem)item;
				String result = null;
				if( expressItem.getAlias() != null){
					result = expressItem.getAlias().toString().trim();
					String temp = result.toUpperCase();
					if(temp.indexOf("AS ") > -1){
						result = result.substring(temp.indexOf("AS ")+3 ,result.length());
					}
				}else{
					result = expressItem.getExpression().toString();
					if( result.indexOf(".") != -1 ){
						result = result.split("\\.")[1];
					}
				}
				columnList.add(result);
			}
		}
		return columnList;
	}
	
	public static List<String> getSqlCreateField(String sql) throws JSQLParserException{
		if(sql.contains("BYTE")) sql = sql.replaceAll("BYTE", "");
		Statement statement = CCJSqlParserUtil.parse(sql);
		CreateTable createTable = (CreateTable) statement;
		List<ColumnDefinition> list = createTable.getColumnDefinitions();
		List<String> columnNameList = new ArrayList<String>();
		for(ColumnDefinition col : list){
			//System.out.println("name :: "+col.getColumnName()+" dataType :: "+col.getColDataType());
			columnNameList.add(col.getColumnName());
		}
		return columnNameList;
	}
	
	public static void main(String[] args) {
		String create = "CREATE TABLE ADM.DISPLAY_CLASS";
		create += "(";
		create += "  SHOP_NO              VARCHAR2(10 BYTE)        NOT NULL,";
		create += "  DISP_NO              VARCHAR2(60 BYTE)        NOT NULL,";
		create += "  DISP_NM              VARCHAR2(500 BYTE),";
		create += "  DISP_TMPL_NO         NUMBER,";
		create += "  HIGH_DISP_NO         VARCHAR2(60 BYTE),";
		create += "  MEM_DISP_NO          VARCHAR2(60 BYTE),";
		create += "  SPECIAL_DISP_TP      VARCHAR2(2 BYTE),";
		create += "  DISP_DEP             NUMBER                   NOT NULL,";
		create += "  MAIN_DISP_YN         VARCHAR2(1 BYTE)         NOT NULL,";
		create += "  DISP_REG_TP          VARCHAR2(2 BYTE),";
		create += "  REL_FILE_NM          VARCHAR2(500 BYTE),";
		create += "  JOB_ROLE_NO          NUMBER,";
		create += "  RESP_DELV_ID         VARCHAR2(20 BYTE)";
		create += ")";


		try {
			getSqlCreateField(create);
		} catch (JSQLParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String getHttpData(String urlStr) throws IOException{
		URL url = new URL(urlStr);
		URLConnection con = url.openConnection();
		con.setConnectTimeout(Constant.HTTP_CONNECTION_TIMEOUT);
		con.setReadTimeout(Constant.HTTP_READ_TIMEOUT);
		InputStream is = con.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		StringBuffer contents = new StringBuffer();
		String line;
		while ((line = br.readLine()) != null){
			contents.append(line + "\n");
	    }
        br.close();
        return contents.toString();
	}
}
