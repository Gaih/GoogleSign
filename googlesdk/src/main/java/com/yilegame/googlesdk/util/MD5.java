package com.yilegame.googlesdk.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {
	public static String md5Encoder(String sourceStr)
	{
		String signStr = "";
		try 
		{
			byte[] bytes = sourceStr.getBytes("utf-8");
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(bytes);
			byte[] md5Byte = md5.digest();
			if(md5Byte != null)
			{
			
				signStr = HexBin.encode(md5Byte); 
			}
		} 
		catch (NoSuchAlgorithmException e)
		{ 
			e.printStackTrace();
		} 
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		return signStr;
	}
}