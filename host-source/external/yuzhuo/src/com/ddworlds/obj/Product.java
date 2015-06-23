package com.ddworlds.obj;

public class Product {
	public String name, des;//产品名称跟描述
	public float price;//产品价格
	public String CpOrderID;//商户orderid
	public String CpUserID;//商户userid
	public String CpOrderDesc;//商户desc
	public String CpUserName;
	
	public Product(String name, String des, float price, String CpOrderID,String CpUserID, String CpOrderDesc, String CpUserName){
		this.name=name;
		this.des=des;
		this.price=price;
		this.CpOrderID=CpOrderID;
		this.CpUserID=CpUserID;
		this.CpOrderDesc=CpOrderDesc;
		this.CpUserName=CpUserName;
	}
}
