package com.ziplinegames.moai;

import java.util.ArrayList;
import java.util.List;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

public class billingConfig {

	public String billingIndex;
	public boolean isRepeated;
	public int money;
	public int number;
	public boolean useSms;  
	 public static List<billingConfig> bListConfig=null;
	 ///
    public static billingConfig getBillingConfig(billingConfig bconfig){
        if(bconfig==null) return bconfig;
        if(bListConfig==null||MoaiBaseSdk.sConfigJsonObject==null) return null;     
        if(bListConfig.size()<=0) return null;
        billingConfig _bconfig;    
        for(int i=0;i<bListConfig.size();i++){
            _bconfig=bListConfig.get(i);
            if(bconfig.number>0){
                if(_bconfig.number==bconfig.number) return _bconfig;
            }
            else
            {
                if(_bconfig.money==bconfig.money) return _bconfig;
            }
        }
        return null;
    }

    public static void setBillingConfig(){
        if(bListConfig!=null||MoaiBaseSdk.sConfigJsonObject==null) return;
        
        //读取计费点
        JsonValue jsonVal=MoaiBaseSdk.sConfigJsonObject.get("billing");
        if(jsonVal==null) return;
        JsonArray billingMap=jsonVal.asArray();
        if(billingMap!=null){
            bListConfig=new ArrayList<billingConfig>();         
            JsonObject mapJson=null;
            for(int i=0;i<billingMap.size();i++){
                billingConfig bConfig=new billingConfig();
                mapJson=billingMap.get(i).asObject(); 
                bConfig.billingIndex=MoaiBaseSdk.GetJsonVal(mapJson,"billingIndex","000");
                bConfig.isRepeated=MoaiBaseSdk.GetJsonVal(mapJson,"isRepeated","false").equalsIgnoreCase("true");
                bConfig.money=MoaiBaseSdk.GetJsonValInt(mapJson,"money",0);
                bConfig.number=MoaiBaseSdk.GetJsonValInt(mapJson,"number",0);
                bConfig.useSms=MoaiBaseSdk.GetJsonVal(mapJson,"useSms","true").equalsIgnoreCase("true");
                bListConfig.add(bConfig);
            }
            
            
        }
    }
    
}
