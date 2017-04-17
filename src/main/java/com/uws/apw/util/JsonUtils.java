package com.uws.apw.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;

import com.uws.apw.model.ApproveResult;
import com.uws.apw.model.Approver;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONFunction;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import net.sf.json.JsonConfig;

/**
 * json解析工具类
 * @version1.0
 */
@SuppressWarnings("all")
public class JsonUtils {
	
	/**
	 * 静态方法,通过一个pojo对象创建一个JSONObject对象
	 * @param obj
	 * @return JSONObject
	 */
	public static JSONObject getJsonObject(Object obj){
		return JSONObject.fromObject(obj);
	}
	
	/**
	 *  Java对象转化成JsonArray字符格式
	 * @param obj
	 * @return JSONArray
	 */
	public static JSONArray getJsonArray(Object obj){
		
		return JSONArray.fromObject(obj);
	}
	
	/**
	 *  Java对象序列化成JSON对象 
	 * @param obj
	 * @return JSONObject
	 */
	public static JSONObject getJSONSerializerObj(Object obj){
		
		return (JSONObject) JSONSerializer.toJSON(obj);
	}
	
	/**
	 * 使用JsonBeanProcessor处理bean对象到JSON接口对象的转换
	 * @param obj
	 * @return JSONObject
	 */
	public static JSONObject javaObj2JsonObj(Object obj,JsonConfig jsconfig){
		return  JSONObject.fromObject(obj, jsconfig);
	}
	
	/**
	 * json反序列化
	 * JSONObject.toBean将json对象转换成Java对象
	 * @param jsonObject 【json对象】
	 * @param obj 【java对象】
	 * @return Object
	 */
	public static Object jsonObj2JavaObj(JSONObject jsonObject,Class beanClass){
		
		return JSONObject.toBean(jsonObject, beanClass);
	}
	
	/**
	 * list转换为jsonArray
	 * @param listVos
	 * @return JSONArray
	 */
	public static JSONArray list2JsonArray(List<Object> listVos){
		
		return JSONArray.fromObject(listVos);
	}
	
	/**
	 * list序列化为jsonArray
	 * @param listVos
	 * @return JSONArray
	 */
	public static JSONArray listSerializerJsonArray(List<Object> listVos){
		
		return (JSONArray) JSONSerializer.toJSON(listVos);
	}
	
	/**
	 * map转换为JSONObject
	 * @param listVos
	 * @return JSONObject
	 */
	public static JSONObject map2JsonObject(Map<String, Object> map){
		
		return JSONObject.fromObject(map);
	}
	
	/**
	 * map转换为jsonArray
	 * @param listVos
	 * @return JSONArray
	 */
	public static JSONArray map2JsonArray(Map<String, Object> map){
		
		return JSONArray.fromObject(map);
	}
	
	/**
	 * map序列化为jsonObject
	 * @param listVos
	 * @return JSONObject
	 */
	public static JSONObject mapSerializerJsonObject(Map<String, Object> map){
		
		return (JSONObject) JSONSerializer.toJSON(map);
	}
	
	/**
	 * java数组转换为json数组
	 * @param objs
	 * @return JSONArray
	 */
	public static JSONArray array2JsonArray(Object [] objs){
		
		return JSONArray.fromObject(objs);
	}
	
	/**
	 * java数组序列化为json数组
	 * @param objs
	 * @return JSONArray
	 */
	public static JSONArray arraySerializerJsonArray(Object [] objs){
		
		return (JSONArray) JSONSerializer.toJSON(objs);
	}
	
	/**
	 * java数组转换为json数组
	 * @param objs
	 * @return JSONArray
	 */
	public static JSONArray array2JsonArray(boolean [] objs){
		
		return JSONArray.fromObject(objs);
	}
	
	/**
	 * java数组序列化为json数组
	 * @param objs
	 * @return JSONArray
	 */
	public static JSONArray arraySerializerJsonArray(boolean [] objs){
		
		return (JSONArray) JSONSerializer.toJSON(objs);
	}
	
	/**
	 * 判断json字符串是否可转换为jsonArray
	 * 如果是JSONArray则返回 JSONArray
	 * @param str
	 */
	public static JSON judgeJsonType(String str){
		try {
			JSON json = string2Json(str);
			if(json instanceof JSONArray){
				return string2JsonArray(str);
			}else if(json instanceof JSONObject){
				return string2JsonObject(str);
			}else{
				return string2Json(str);
			}
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * 字符串转换为json对象
	 * @param objs
	 * @return JSONObject
	 */
	public static JSONObject string2JsonObject(String str){
		
		return JSONObject.fromObject(str);
	}
	
	/**
	 * 字符串转换为json数组
	 * str：["羊村",{"birthday":"2010-01-01"},"lazysheep@yc.com",1,"懒洋洋"]
	 * @param objs
	 * @return JSONArray
	 */
	public static JSONArray string2JsonArray(String str){
		
		return JSONArray.fromObject(str);
	}
	
	/**
	 * 字符串序列化为json对象
	 * str：["羊村",{"birthday":"2010-01-01"},"lazysheep@yc.com",1,"懒洋洋"]
	 * @param objs
	 * @return  JSONArray
	 */
	public static JSONArray stringSerializerJsonObject(String str){
		
		return (JSONArray) JSONSerializer.toJSON(str);
	}
	
	/**
	 * 字符串序列化为json
	 */
	public static JSON string2Json(String jsonStr){
		
		return JSONSerializer.toJSON(jsonStr);
	}
	
	/**
	 * jsonObject转换为json字符串
	 * @param str
	 * @return String
	 */
	public static String jsonObject2Json(JSONObject  jsonObj){

		return  JSONSerializer.toJSON(jsonObj).toString();
	}
	
	/**
	 * jsonArray转换为json字符串
	 * @param str
	 * @return String
	 */
	public static String jsonArray2Json(JSONArray  jsonArray){
		
		return  JSONSerializer.toJSON(jsonArray).toString();
	}
	
	/**
	 * jsonArray反序列化为Array
	 * @param str
	 * @return Object[]
	 */
	public static Object[] jsonArray2JavaArray(List input){
        JSONArray jsonArray = (JSONArray) JSONSerializer.toJSON( input );   
        JsonConfig jsonConfig = new JsonConfig();   
        jsonConfig.setArrayMode( JsonConfig.MODE_OBJECT_ARRAY );   
        Object[] output = (Object[]) JSONSerializer.toJava(jsonArray, jsonConfig);
		return output;
	}
	
	/**
	 * jsonArray反序列化为Array
	 * @param str
	 * @return Object[]
	 */
	public static Object[] jsonArray2JavaArray(String str){
		JSONArray jsonArray = JSONArray.fromObject(str);
        Object[] objs = jsonArray.toArray();
		return objs;
	}
	
	/**
	 * javaJsonFunction转换为Json
	 * @param str
	 * @return JSONFunction
	 */
	public static JSONFunction javaJsonFunction2Json(String str){
        JSONObject jsonObject = (JSONObject) JSONSerializer.toJSON(str);   
        JSONFunction func = (JSONFunction) jsonObject.get("func");   
		return func;
	}
	
	/**
	 * json转换为java对象 MorphDynaBean
	 * @param str
	 * @return MorphDynaBean
	 */
	public static Object json2MorphDynaBean(String str){
        JSON json = JSONSerializer.toJSON(str);
        Object obj = JSONSerializer.toJava(json);
        return obj;
	}
	
	/**
	 * 获取json转换后MorphDynaBean的属性值
	 * @param str
	 * @param propertyKey
	 * @return
	 */
	public static String getMorphDynaBeanValue(String str,String propertyKey){
		String returnValue="";
		JSON json = JSONSerializer.toJSON(str);
		Object obj = JSONSerializer.toJava(json);
		try {
			returnValue =  PropertyUtils.getProperty(obj,propertyKey).toString();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		
		return returnValue;
	}
	
	/**
	 *  json对象转换为Map
	 * @param jsonObject
	 * @return
	 */
	public static Map<String, ?> jsonObject2Map(JSONObject jsonObject){
		Map<String, ?> mapBean = (Map) JSONObject.toBean(jsonObject, Map.class);
		return mapBean;
	}
	
	/**
	 * json对象追加到Map中
	 * @param jsonObject
	 * @param clazzMap
	 * @return
	 */
	public static Map<String, ?> jsonObject2Map(JSONObject jsonObject,Map clazzMap){
        Map<String, ?> mapBean = (Map) JSONObject.toBean(jsonObject, Map.class, clazzMap);
		return mapBean;
	}
	
	/**
	 * json字符串转换为Map对象
	 * 只支持最多两层嵌套解析
	 * "{\"arr\":[{\"name\":\"懒洋洋\"},{\"name\":\"喜洋洋\"}],\"id\":1,\"ismale\":true,\"name\":\"懒洋洋\",\"happysheep\":{\"id\":2,\"name\":\"喜洋洋\"}}";
	 * @param jsonStr
	 * @return Map<String, Object>
	 */
	public static Map<String, Object> parseJSON2Map(String jsonStr){
        Map<String, Object> map = new HashMap<String, Object>();
        
        JSONObject json = JSONObject.fromObject(jsonStr);
        for(Object outerObj : json.keySet()){//最外层解析
            Object innerObj = json.get(outerObj); 
            
            if(innerObj instanceof JSONArray){//如果内层还是数组的话，继续解析
                List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
                Iterator<JSONObject> it = ((JSONArray)innerObj).iterator();
                while(it.hasNext()){//内层解析
                    JSONObject json2 = it.next();
                    list.add(parseJSON2Map(json2.toString()));
                }
                map.put(outerObj.toString(), list);
            }else {
                map.put(outerObj.toString(), innerObj);
            }
        }
        return map;
    }
	
	/**
	 * json字符串转换为list对象
	 * @param jsonStr
	 * @return
	 */
    public static List<Map<String, Object>> parseJSON2List(String jsonStr){
        JSONArray jsonArr = JSONArray.fromObject(jsonStr);
        List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
        Iterator<JSONObject> it = jsonArr.iterator();
        while(it.hasNext()){
            JSONObject json2 = it.next();
            list.add(parseJSON2Map(json2.toString()));
        }
        return list;
    }
    
    /**
     * 通过url获取服务器返回的json字符串
     * @param urlPath
     * @return String
     * @throws Exception
     */
    public static String getJsonStringByRUL(String urlPath) throws Exception {
		URL url = new URL(urlPath);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.connect();
		InputStream inputStream = connection.getInputStream();
		//对应的字符编码转换
		Reader reader = new InputStreamReader(inputStream, "UTF-8");
		BufferedReader bufferedReader = new BufferedReader(reader);
		String str = null;
		StringBuffer sb = new StringBuffer();
		while ((str = bufferedReader.readLine()) != null) {
			sb.append(str);
		}
		reader.close();
		connection.disconnect();
		return sb.toString();
	}
    
	/**
	 * json反序列化
	 * JSONObject.toBean将json对象转换成Java对象
	 * @param jsonObject 【json对象】
	 * @param obj 【java对象】
	 * @return Object
	 */
	public static ApproveResult jsonObj2ApproveResult(JSONObject jsonObject,Class beanClass){
		ApproveResult  approveResult = (ApproveResult)JSONObject.toBean(jsonObject, beanClass);
		for(Object outerObj : jsonObject.keySet()){//最外层解析
			Object innerObj = jsonObject.get(outerObj); 
			
			if(innerObj instanceof JSONArray){//如果内层还是数组的话，继续解析
				List<Approver> list = new ArrayList<Approver>();
				Iterator<JSONObject> it = ((JSONArray)innerObj).iterator();
				while(it.hasNext()){//内层解析
					JSONObject json2 = it.next();
					list.add((Approver)JSONObject.toBean(json2,Approver.class));
				}
				approveResult.setNextApproverList(list);
			}
		}
		return approveResult;
	}
}
