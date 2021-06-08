package featurecat.lizzie.util;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

// import sun.misc.BASE64Encoder;

public class Base64AesEncrypt {

  /**
   * BASE64加密
   *
   * @param base64Content 被加密的字符串
   * @return
   */
  public static String encryptBASE64(String base64Content) {
    byte[] bt = null;
    try {
      bt = base64Content.getBytes("utf-8");
    } catch (UnsupportedEncodingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    //  String aesContent = (new BASE64Encoder()).encodeBuffer(bt);

    String res = Base64.getEncoder().encodeToString(bt) + "\r\n";
    return res;
  }

  /**
   * AES加密
   *
   * @param aesContent 被Base64加密过的字符串
   * @param key 秘钥
   * @param ivParameter 偏移量
   * @return
   */
  public static String encryptAES(String aesContent, String key, String ivParameter) {
    try {
      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
      byte[] raw = key.getBytes("utf-8");
      SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
      IvParameterSpec iv =
          new IvParameterSpec(ivParameter.getBytes("utf-8")); // 使用CBC模式，需要一个向量iv，可增加加密算法的强度
      cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
      byte[] encrypted = cipher.doFinal(aesContent.getBytes("utf-8"));
      return Base64.getEncoder().encodeToString(encrypted) + "\r\n";
      // new BASE64Encoder().encode(encrypted); // 此处使用BASE64做转码。
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}
