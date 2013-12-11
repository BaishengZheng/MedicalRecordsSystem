package com.example.sslsocket;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

class AESEncryptDecrypt {
	
	String keyValue = "ThisIsASecretKey";
	
	@SuppressWarnings("finally")
	public String Encrypt(String plainText) {
		
	    SecretKeySpec keySpec = null;
		try {
			keySpec = new SecretKeySpec(keyValue.getBytes("UTF-8"), "AES");
		} catch (UnsupportedEncodingException e1) {
			System.out.println("Exception: UnsupportedEncodingException");
			e1.printStackTrace();
		}
	     
	    // Instantiate the cipher
	    Cipher cipher;
	    String result = null;
		try {
			cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, keySpec);
			byte[] encryptedTextBytes = cipher.doFinal(plainText.getBytes("UTF-8"));
			result = new Base64().encodeAsString(encryptedTextBytes);
		} catch (NoSuchAlgorithmException e) {
			System.out.println("Exception: NoSuchAlgorithmException");
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			System.out.println("Exception: NoSuchPaddingException");
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			System.out.println("Exception: InvalidKeyException");
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			System.out.println("Exception: IllegalBlockSizeException");
			e.printStackTrace();
		} catch (BadPaddingException e) {
			System.out.println("Exception: BadPaddingException");
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			System.out.println("Exception: UnsupportedEncodingException");
			e.printStackTrace();
		} finally {
			return result;
		}  
}
 
	@SuppressWarnings("finally")
	public String Decrypt(String encryptedText) {
	     
		    SecretKeySpec keySpec = null;
			try {
				keySpec = new SecretKeySpec(keyValue.getBytes("UTF-8"), "AES");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		 
		    // Instantiate the cipher
		    Cipher cipher = null;
		    String result = null;
		    
		    
			try {
				cipher = Cipher.getInstance("AES");
				cipher.init(Cipher.DECRYPT_MODE, keySpec);		     
			    byte[] encryptedTextBytes = new Base64().decodeBase64(encryptedText);
			    byte[] decryptedTextBytes = cipher.doFinal(encryptedTextBytes);		     
			    result = new String(decryptedTextBytes);
			} catch (NoSuchAlgorithmException e) {
				System.out.println("Exception: NoSuchAlgorithmException");
				e.printStackTrace();
			} catch (NoSuchPaddingException e) {
				System.out.println("Exception: NoSuchPaddingException");
				e.printStackTrace();
			} catch (InvalidKeyException e) {
				System.out.println("Exception: InvalidKeyException");
				e.printStackTrace();
			} catch (IllegalBlockSizeException e) {
				System.out.println("Exception: IllegalBlockSizeException");
				e.printStackTrace();
			} catch (BadPaddingException e) {
				System.out.println("Exception: BadPaddingException");
				e.printStackTrace();
			} finally {
				return result;
			}	    
	}
	
	public String getSecretKey() {
		return keyValue;
	}

}
