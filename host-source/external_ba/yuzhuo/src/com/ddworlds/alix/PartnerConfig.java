package com.ddworlds.alix;

public class PartnerConfig {

	// 合作商户ID。用签约支付宝账号登录ms.alipay.com后，在账户信息页面获取。
	public static final String PARTNER = "2088901463607903";
	// 商户收款的支付宝账号
	public static final String SELLER = "pay@jadegreat.com";
	// 商户（RSA）私钥
	public static final String RSA_PRIVATE = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAOZsjwvZu5UYBUFX4Nmib/NVN5hMJk9aion1BCFvv/rfD8l42pIPb5/kEwh2LIy/LmJneIvvQFYSeqXD/GNOav++ZH4rVkT8pylsvHWSNdXTI0SfiXUf/aPkLXim5P6LMQhvbYOCmGg9hIXSNy+p2K0S0irN56Z9N8lvQrtcm4SHAgMBAAECgYABV9SlnwlG3zL4pIh8/ENmybJfQuJeSArSrwjtIPefgLlY1UmA3HIsHmo1/GpaD6s3i57aQfTAC6+HU9MeyRA6lo6trZv9xKgopsuPZUIDck24nf1ZjEijKQ1cXZYR8gIchbkAjR4KQpN0dcBul7+MP5ho18DUZOZdYK80bKb7YQJBAPOSFMcPYJgByI614Ra09/4x+f1VGRNy58f7xMZqRzoxIbdqIJA332kTLP81qxmXxk61hgnqm0DDjGWR2lMU31kCQQDyLrxMX29Z+PD7Xum2346KcVg7BcIokHf2UOuJESUrbsqu8x6CvBfUoj6Mtjq5tYLREvfBhv9axc/E8c+1e+bfAkBXBc9Em7rgNGrqihMEoN2lxUdS+q994mTrb4mLdndCVEF+nX1K6Zt5mC9Qc2VRPIfOoU5lhpu3lOY5d9XwLxnpAkEAyVZ5TLT6ib4f+ZrKijII+qeIRmlfJQIV+T0E04rhb95HunJ8Jwvgc/H+NlU5NTLlcpNmTgnBOOHC/WRa3fIH2QJBAM/204zk9Gl5mmH8gUxA9eUC06dGSNJrJuAfeadfcxxO43cSjGxMykk5BETrrPvyEaa0UCjbouIuiML0HPeTKrU=";
	// 支付宝（RSA）公钥 用签约支付宝账号登录ms.alipay.com后，在密钥管理页面获取。
	public static final String RSA_ALIPAY_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDL54WZeExmGIn6OaLZAA/HmMtuDlTJMFC9Z/ml ttPSNcMACEGKSbS5uKPBaNkhQoZXNj8x2gliBisqkMtrVeIDEMKi+adQ5HUPR5IggzkSYAKek73L bLJhUMx8BMmhtM8OpC14Y2/QWWaXIh+qOYBJp8V3jzMcI7o3hIV3AbunbwIDAQAB";
	// 支付宝安全支付服务apk的名称，必须与assets目录下的apk名称一致
	public static final String ALIPAY_PLUGIN_NAME = "alipay_plugin_20120428msp.apk";//YlGameWelcome.apk
	// TestAbsCall
}
