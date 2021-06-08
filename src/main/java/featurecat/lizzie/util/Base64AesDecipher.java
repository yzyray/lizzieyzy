package featurecat.lizzie.util;

import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

// import org.apache.commons.codec.binary.Base64;
// import sun.misc.BASE64Decoder;

public class Base64AesDecipher {
  /**
   * base64����
   *
   * @param content ��AES���ܹ����ַ���
   * @return
   */
  public static String DecipherBase64(String content) {
    byte[] b = null;
    String result = null;
    if (content != null) {
      Base64.Decoder decoder = Base64.getMimeDecoder();
      //    BASE64Decoder decoder = new BASE64Decoder();
      try {
        b = decoder.decode(content);
        result = new String(b, "utf-8");
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return result;
  }

  /**
   * @param content ��Base64+AES���ܹ����ַ���
   * @param aesKey ��Կ
   * @param ivParameter ƫ����
   * @return
   */
  public static String decryptAES(String content, String aesKey, String ivParameter) {
    try {
      byte[] raw = aesKey.getBytes("ASCII");
      SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
      IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes("utf-8"));
      cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
      Base64.Decoder decoder = Base64.getMimeDecoder();
      byte[] encrypted1 = decoder.decode(content); // ����base64����
      byte[] original = cipher.doFinal(encrypted1);
      String originalString = new String(original, "utf-8");
      return originalString;
    } catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }
}
