package com.globalegrow.burypointcollect.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public final class EncryptUtil {
	
	private EncryptUtil() {

	}

	private static MessageDigest getMessageDigest() {
		try {
			return MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("SHA-1 Algorithm is not available", e);
		}
	}

	/**
	 * 将字节数组转换成16进制字符串
	 * 
	 * @param b
	 * @return
	 */
	private static String byte2hex(byte[] b) {
		StringBuilder sbDes = new StringBuilder();
		String tmp = null;
		for (int i = 0; i < b.length; i++) {
			tmp = (Integer.toHexString(b[i] & 0xFF));
			if (tmp.length() == 1) {
				sbDes.append("0");
			}
			sbDes.append(tmp);
		}
		return sbDes.toString();
	}

	public static String encrypt(String strSrc) {
		String strDes = null;
		byte[] bt = strSrc.getBytes();
		MessageDigest digest = getMessageDigest();
		digest.update(bt);
		strDes = byte2hex(digest.digest());
		return strDes;
	}

	public static String sign(String paramString, String signType) {
		try {
			MessageDigest sha1 = MessageDigest.getInstance(signType);
			return byte2hex(sha1.digest(paramString.getBytes("utf-8")));
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalArgumentException(e);
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * 校验请求的签名是否合法
	 * 
	 * 加密/校验流程： 1. 将token、timestamp、nonce三个参数进行字典序排序 2. 将三个参数字符串拼接成一个字符串进行sha1加密
	 * 3. 开发者获得加密后的字符串可与signature对比，标识该请求来源于微信
	 * 
	 * @param signature
	 * @param timestamp
	 * @param nonce
	 * @return 是否合法
	 */
	public static boolean validate(String signature, String timestamp, String nonce, String token) {
		// 1. 将token、timestamp、nonce三个参数进行字典序排序
		String[] arrTmp = { token, timestamp, nonce };
		Arrays.sort(arrTmp);
		StringBuffer sb = new StringBuffer();
		// 2.将三个参数字符串拼接成一个字符串进行sha1加密
		for (int i = 0; i < arrTmp.length; i++) {
			sb.append(arrTmp[i]);
		}
		String expectedSignature = encrypt(sb.toString());
		// 3. 开发者获得加密后的字符串可与signature对比，标识该请求来源于微信
		return expectedSignature.equals(signature);
	}

}
