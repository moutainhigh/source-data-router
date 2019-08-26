package com.globalegrow.bts.report;

import sun.misc.BASE64Decoder;

import java.io.ByteArrayOutputStream;

public class UrlUtils {


    /**
     * URL 加密  这是把中文转码
     * <p>编码格式：utf-8</p>
     */
    public final static String urlEncode(String s) {
        try {
            return notEmpty(s) ? java.net.URLEncoder.encode(s, "utf-8") : null;
        } catch (Exception e) {
            return null;
        }
    }
    /**
     * URL 解密        这是解码
     */
    public static String urlDecode(String s) {
        try {
            return notEmpty(s) ? java.net.URLDecoder.decode(s, "utf-8") : null;
        } catch (Exception e) {
            return null;
        }
    }
    /**
     * 检查是否为空
     *
     * @param s 需要进行检查的字符串
     * @return 检查结果
     */
    public static Boolean notEmpty(String s) {
        return s != null && s.trim().length() >= 1;
    }

    // 将 s 进行 BASE64 编码
    public static String getBASE64(String s) {
        if (s == null)
            return null;
        return (new sun.misc.BASE64Encoder()).encode(s.getBytes());
    }

    // 将 BASE64 编码的字符串 s 进行解码
    public static String getFromBASE64(String s) {
        if (s == null)
            return null;
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            byte[] b = decoder.decodeBuffer(s);
            return new String(b);
        } catch (Exception e) {
            return null;
        }
    }

    /*
     * 16进制数字字符集
     */
    private static String hexString = "0123456789ABCDEF";
    /*
     * 将字符串编码成16进制数字,适用于所有字符（包括中文）
     */
    public static String encode(String str) {
        // 根据默认编码获取字节数组
        byte[] bytes = str.getBytes();
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        // 将字节数组中每个字节拆解成2位16进制整数
        for (int i = 0; i < bytes.length; i++) {
            sb.append(hexString.charAt((bytes[i] & 0xf0) >> 4));
            sb.append(hexString.charAt((bytes[i] & 0x0f) >> 0));
        }
        return sb.toString();
    }
    /*
     * 将16进制数字解码成字符串,适用于所有字符（包括中文）
     */
    public static String decode(String bytes) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(
                bytes.length() / 2);
        // 将每2位16进制整数组装成一个字节
        for (int i = 0; i < bytes.length(); i += 2)
            baos.write((hexString.indexOf(bytes.charAt(i)) << 4 | hexString
                    .indexOf(bytes.charAt(i + 1))));
        return new String(baos.toByteArray());
    }
    public static void main(String[] args) throws Exception {

        String stest = "http://localhost:8026/loginServlet.do?username='张三'&userpass='123'";
        System.out.println(urlEncode(stest));
        System.out.println(urlDecode(stest));
        System.out.println(UrlUtils.encode("中"));
        System.out.println(UrlUtils.decode("D6D0"));
    }

}
