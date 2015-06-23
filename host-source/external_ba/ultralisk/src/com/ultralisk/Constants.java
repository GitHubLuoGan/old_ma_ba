
package com.ultralisk;

public interface Constants 
{
   public static final String PARAM_PRICE 		= "price";
   public static final String PARAM_DESC  		= "desc";
   public static final String PARAM_ROLE_NAME 	= "roleName";
   public static final String PARAM_EXT_INFO 	= "extInfo";
   public static final String PARAM_NOTIFY_URL 	= "notify_url";
   public static final String PARAM_MERCHANT_ID = "merchantId";
   public static final String PARAM_CALLBACK 	= "callback";
   
   public static final String CHARGE_CARD_PARAM_CARD_TYPE = "card_type";
   
   public static final int ACTIVITY_PAYMENT		= 1;
   
   //以下三个值是神州付规定的，不能改变！！
   public static final int CHARGE_CARD_CHINAMOBILE 	= 0;
   public static final int CHARGE_CARD_CHINAUNICOM 	= 1;
   public static final int CHARGE_CARD_CHINATELECOM = 2;
   
   public static final int CHARGE_CARD_YIKATONG = -1;//自定义的
   
   public static final int CHARGE_CARD_JCARD = 3;//骏网一卡通
   public static final int CHARGE_CARD_SDO = 7;//盛大一卡通
   public static final int CHARGE_CARD_WY163 = 8;//网易一卡通
   
   public static final int CHARGE_CARD_ZHENGTU = 9;//征途一卡通
   public static final int CHARGE_CARD_WABMEI = 10;//完美一卡通
   public static final int CHARGE_CARD_SHOUHU = 12;//搜狐一卡通
   
   public static final int CHARGE_CARD_JIUYOU = 13;//久游一卡通
   public static final int CHARGE_CARD_QQ = 19;//Q币充值卡
   public static final int CHARGE_CARD_TIANHONG = 20;//天宏一卡通
   
   public static final int CHARGE_CARD_GUANGYU = 23;//光宇一卡通
   public static final int CHARGE_CARD_SHENGFUTONG = 26;//盛付通一卡通
   
   
   
   public static final String CARD_NAME_CHINAMOBILE 	= "中国移动";
   public static final String CARD_NAME_CHINAUNICOM 	= "中国联通";
   public static final String CARD_NAME_CHINATELECOM 	= "中国电信";
   

   public static final String CARD_NAME_JCARD = "骏网一卡通";//骏网一卡通
   public static final String CARD_NAME_SDO = "盛大一卡通";//盛大一卡通
   public static final String CARD_NAME_WY163 = "网易一卡通";//网易一卡通
   
   public static final String CARD_NAME_ZHENGTU = "征途一卡通";//征途一卡通
   public static final String CARD_NAME_WABMEI = "完美一卡通";//完美一卡通
   public static final String CARD_NAME_SHOUHU = "搜狐一卡通";//搜狐一卡通
   
   public static final String CARD_NAME_JIUYOU = "久游一卡通";//久游一卡通
   public static final String CARD_NAME_QQ = "Q币充值卡";//Q币充值卡
   public static final String CARD_NAME_TIANHONG = "天宏一卡通";//天宏一卡通
   
   public static final String CARD_NAME_GUANGYU = "光宇一卡通";//光宇一卡通
   public static final String CARD_NAME_SHENGFUTONG = "盛付通一卡通";//盛付通一卡通
   
   
   
   public static final int[] CHINAMOBILE_PRICES 	= {10, 20, 30, 50, 100, 300, 500};
   public static final int[] CHINAUNICOM_PRICES 	= {20, 30, 50, 100, 300, 500};
   public static final int[] CHINATELECOM_PRICES 	= {10, 20, 30, 50, 100, 200, 300, 500};
  
   public static final int[] JCARD_PRICES 	= {5,6,10,15,20,30,50,100,120,200,300,500};
   public static final int[] SDO_PRICES 	= {1,2,3,5,9,10,15,25,30,35,45,50,100,300,350,1000};
   public static final int[] WY163_PRICES 	= {5,10,15,20,30,50};
  
   public static final int[] ZHENGTU_PRICES 	= {10,15,20,25,30,50,60,100,300,500,1000};
   public static final int[] WABMEI_PRICES 	= {15,30,50,100};
   public static final int[] SHOUHU_PRICES 	= {5,10,15,30,40,100};
  
   public static final int[] JIUYOU_PRICES 	= {5,10,20,25,30,50,100};
   public static final int[] QQ_PRICES 	= {5,10,15,30,60,100,200};
   public static final int[] TIANHONG_PRICES 	= {5,10,15,30,50,100};
  
   
   public static final int[] GUANGYU_PRICES 	= {10,20,30,50,100};
   public static final int[] SHENGFUTONG_PRICES 	= {10,30,50,100}; 
   
   
   
}