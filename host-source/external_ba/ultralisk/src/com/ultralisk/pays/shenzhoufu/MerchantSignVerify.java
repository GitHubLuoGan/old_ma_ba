package com.ultralisk.pays.shenzhoufu;


import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.PublicKey;
import java.security.Signature;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class MerchantSignVerify {

    /**
     * 验签
     *
     * @param szfPublicPath 神州付公钥地址（物理路径）
     * @param md5String     数据的md5摘要
     * @param sign          签名字符串
     * @return 签名校验是否成功
     * @throws java.io.FileNotFoundException 神州付公钥证书不存在
     */
    public static Boolean verify(String szfPublicPath, String md5String, String sign) throws FileNotFoundException {
        //打开神州付公钥证书，如果证书不存在，在抛出 FileNotFoundException 异常 
        FileInputStream fin = new FileInputStream(szfPublicPath);
        try {
            CertificateFactory certificatefactory = CertificateFactory.getInstance("X.509");
            X509Certificate certificate = (X509Certificate) certificatefactory.generateCertificate(fin);
            fin.close();
            byte[] sigedText =  Base64.decode(sign);//需要对接受到的参数进行base64解密
            byte[] updateData = md5String.getBytes("UTF-8");
            PublicKey pub = certificate.getPublicKey();
            Signature signature = Signature.getInstance("MD5withRSA");
            signature.initVerify(pub);
            signature.update(updateData);

            return signature.verify(sigedText);
        } catch (Exception e) {
            return false;
        }
    }
}
