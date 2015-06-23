package com.ziplinegames.moai;

import java.util.ArrayList;
import java.util.List;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

public class unipayPayCodeConfig {
	public static List<unipayPayCodeConfig> pCodeList=null;
	public String payCode; 
	public int money;
	public String number; 

	 public static void setPayCodeConfig(){
	        if(pCodeList!=null||MoaiBaseSdk.sConfigJsonObject==null) return;
	        
	        //读取计费点
	        JsonValue jsonVal=MoaiBaseSdk.sConfigJsonObject.get("payCodes");
	        if(jsonVal==null) return;
	        JsonArray billingMap=jsonVal.asArray();
	        if(billingMap!=null){
	            pCodeList=new ArrayList<unipayPayCodeConfig>();         
	            JsonObject mapJson=null;
	            for(int i=0;i<billingMap.size();i++){
	            	unipayPayCodeConfig bConfig=new unipayPayCodeConfig();
	                mapJson=billingMap.get(i).asObject(); 
	                bConfig.payCode=MoaiBaseSdk.GetJsonVal(mapJson,"payCode","000");
	                bConfig.money=MoaiBaseSdk.GetJsonValInt(mapJson,"money",0);
	                bConfig.number=MoaiBaseSdk.GetJsonVal(mapJson,"number","0");
	                pCodeList.add(bConfig);
	            }
	            
	            
	        }
	    }
	 ///
	 public static unipayPayCodeConfig getPayCodeConfig(unipayPayCodeConfig bconfig){
	        if(bconfig==null) return bconfig;
	        if(pCodeList==null||MoaiBaseSdk.sConfigJsonObject==null) return null;     
	        if(pCodeList.size()<=0) return null;
	        unipayPayCodeConfig _bconfig;    
	        for(int i=0;i<pCodeList.size();i++){
	            _bconfig=pCodeList.get(i);
	            if(bconfig.number!="0"){
	                if(_bconfig.number.equalsIgnoreCase(bconfig.number)) return _bconfig;
	            }
	            else
	            {
	                if(_bconfig.money==bconfig.money) return _bconfig;
	            }
	        }
	        return null;
	    }
	    
}
