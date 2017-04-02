package com.mir.application.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class JsonToJavaUtil {
	
	private List<Map<String,String>> dataTypeMapList = null;
	
	public List<Map<String,String>> getDataTypeMapList(String jsonData) throws Exception{
		if(jsonData == null){
			return null;
		}
		
		dataTypeMapList = new ArrayList<Map<String,String>>();
		Map<String, Object> rootMap = this.jsonToMap(jsonData);
		this.addDataTypeMapList(rootMap);
		return dataTypeMapList;
	}
	
	
	@SuppressWarnings("unchecked")
	private Map<String, Object> jsonToMap(String jsonData) throws Exception {
		/**
        {
             "name" : { "first" : "Joe", "last" : "Sixpack" },
             "gender" : "MALE",
             "verified" : false,
             "userImage" : "Rm9vYmFyIQ=="
        }
        */
        
        
        /*
       StringBuffer sbuf = new StringBuffer();
       sbuf.append("{") ;
       sbuf.append("\"name\" : { \"first\" : \"Joe\", \"last\" : \"Sixpack\" },") ;
       sbuf.append("\"ddd\" : { \"first\" : \"Joe\", \"last\" : \"Sixpack\" },") ;
       sbuf.append("\"gender\" : \"MALE\",") ;
       sbuf.append("\"verified\" : false,") ;
       sbuf.append("\"userImage\" : \"Rm9vYmFyIQ==\"") ;
       sbuf.append("}") ;*/
		HashMap<String, Object> rs = null;
	   	try {
	   		rs = new ObjectMapper().readValue(jsonData, LinkedHashMap.class) ;
		} catch (JsonMappingException e) {
			List<LinkedHashMap<String, Object>> rsList = new ObjectMapper().readValue(jsonData, ArrayList.class) ;
			rs = rsList.get(0);
		}
      
       return rs;

   }
	
	@SuppressWarnings("unchecked")
	private void addDataTypeMapList(Map<String, Object> rootMap){
		if( rootMap == null){
			return ;
		}
		Set<String> keySet = rootMap.keySet();
		Iterator<String> it = keySet.iterator();
		Map<String,String> dataTypeMap = getDataTypeMap(rootMap);
		dataTypeMapList.add(dataTypeMap);
		while(it.hasNext()){
			String key = it.next();
			if(rootMap.get(key) instanceof Map){
				Map<String, Object> map = (Map<String, Object>) rootMap.get(key);
				map.put("@ClassName", key);
				this.addDataTypeMapList(map);
			}else if(rootMap.get(key) instanceof List){
				List<Object> list = (List<Object>) rootMap.get(key);
				if(list.size() > 0){
					Object object = list.get(0);
					if(object instanceof Map){
						Map<String, Object> map = (Map<String, Object>) object;
						map.put("@ClassName", key);
						this.addDataTypeMapList(map);
					}
				}
			}
			
		}
	}
	
	@SuppressWarnings("unchecked")
	private  Map<String,String> getDataTypeMap(Map<String, Object> typeMap){
		if( typeMap == null){
			return null;
		}
		Set<String> keySet = typeMap.keySet();
		Iterator<String> it = keySet.iterator();
		Map<String,String> dataTypeMap = new LinkedHashMap<String, String>();
		while(it.hasNext()){
			String key = it.next();
			String type = null;
			if(typeMap.get(key) instanceof List){
				List<Object> list = (List<Object>) typeMap.get(key);
				if(list.size() > 0){
					Object object = list.get(0);
					if(object instanceof Map){
						type = "List<"+this.getClassName(key)+">";
					}else{
						if( object == null ){
							type = "List<String>";
						}else{
							type = "List<"+object.getClass().getSimpleName()+">";
						}
					}
				}
				
			}else if(typeMap.get(key) instanceof Map){
				type = this.getClassName(key);
			}else if("@ClassName".equals(key)){
				type = this.getClassName(typeMap.get(key).toString());
			}else {
				if( typeMap.get(key) == null ){
					type = "String";
				}else{
					type = typeMap.get(key).getClass().getSimpleName();
				}
			}
			//System.out.print(type+" ");
			//System.out.println(key);
			dataTypeMap.put(key, type);
		}
		
		return dataTypeMap;
	}
	
	private String getClassName(String key){
		return key.substring(0, 1).toUpperCase()+key.substring(1,key.length());
	}
	
	public static void main(String[] args) {
		StringBuffer sbuf = new StringBuffer();
	       sbuf.append("{") ;
	       sbuf.append("\"name\" : { \"first\" : \"Joe\", \"last\" : 11231231312312324 },") ;
	       sbuf.append("\"dddd\" : { \"first\" : \"Joe\", \"last\" : \"Sixpack\" },") ;
	       sbuf.append("\"gender\" : \"MALE\",") ;
	       sbuf.append("\"verified\" : false,") ;
	       sbuf.append("\"age\" : 2511111111111111,") ;
	       sbuf.append("\"userImage\" : \"Rm9vYmFyIQ==\",") ;
	       sbuf.append("\"Resultlist\" : [{\"sss\":1},{\"sss\":2},{\"sss\":3},{\"sss\":4}]") ;
	       sbuf.append("}") ;
	       
	       JsonToJavaUtil test = new JsonToJavaUtil();
	       try {
			List<Map<String,String>> list = test.getDataTypeMapList(sbuf.toString());
			System.out.println(list.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	       
	}
}
