package com.ultralisk.pays.alipay;

public class PartnerConfig {

	// 合作商户ID。用签约支付宝账号登录ms.alipay.com后，在账户信息页面获取。
	public static final String PARTNER = "2088011468163941";
	// 商户收款的支付宝账号
	public static final String SELLER = "ace@3gwin.cn";
	// 商户（RSA）私钥
	public static final String RSA_PRIVATE = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAPTwLvKt34Frca2x"+
"DjnkcrrZs1Quvbz5/RqONOdz1Cj4VN8bgbWN8xoiexH+1jfdrQWdMagWkQjA1aLV"+
"oQzt7MMmHYKk2gpZO3BgPcI6WVnbDK6Qu/OEoQKAHFzl4H1d/JR4WdQes2yVbIVg"+
"6z3x5PMYj19sqHQDR1dnqKyxDFGRAgMBAAECgYEA69s4KgEZvqK6lVlv7Hk5rQki"+
"sIZNuGI0wdR7xv/3JPhzqV7wWOX2WI0pWxu8JZF1IXnLriMEx5wAW8tASJX2q9WP"+
"65BwS+7q3ucyisaZaJsOslyqRUYOsCLcYdOQ4Ew2jybN37Y0kFmMa3NopV108MBj"+
"BkL+gHud1C7za99X550CQQD6sIB0bz3L7NftLLIzEmzZTsfyAS4VZIqjUmfVGDbE"+
"CLKddPJn9l+A2TO99W7T0S1WlVj/Fy/7HoGNU/85nKwLAkEA+iB+DlegQtlPzsCO"+
"LhkV2tqesew/9n3ZAftV5I729CTIebjchWOIJBjSWWSwZ4+0dHnnHnsbTZhZHgZf"+
"rI3eUwJAIJ/xx7VmdHCZVUt40X63XbyL9unCZpRUGpcx3iCtl+uN20IJ/g+pNCqQ"+
"OkuDpdtWLCt5jpEk9Ghu8caZs5S5KwJAVAWcMrcux6X2UC6Q1F1q4QS1cHE7H1Mx"+
"FaBXPxrjTCobCgCWW1yO/Ak2h/7x3yf/52yXvb5+8nbt4M8lTnKtrwJAFWqfiJlp"+
"0GdllBI7Fz9MHtb8d2WajAZ2Mt7iyO7tuKPZKU64igEv0fQ5Ufeyd0V7D3X/yZJh"+
"OaGstRBwSZFf7A==";
	// 支付宝（RSA）公钥 用签约支付宝账号登录ms.alipay.com后，在密钥管理页面获取。
	public static final String RSA_ALIPAY_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC9MOrdU0UNMPX3sISqF1Z6aZ3smKQz7Y0KOwaFx9Ds9Lil89xtQAUsMwXO01R5dj9AvTAo8X93nTCP3DbrR4VvoSpsKG1UBi4nELqEzvKQhWlfY3OQ/zVoBg7x78KYA0RY7o9dZGBcZVjHw4HfvdUdqFPliFQ2Klsy4ZP5JqQpOQIDAQAB";
	// 支付宝安全支付服务apk的名称，必须与assets目录下的apk名称一致
	public static final String ALIPAY_PLUGIN_NAME = "Alipay_msp_online.apk";

}