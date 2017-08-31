/***
*These codes and the corresponding Android application for D2D communications that are available at (https://github.com/CRCDAndroidApplicationRealtimeContent/CRCD) have been developed by the GTMD research Lab at Isfahan University of Technology and the NetSciWiS research Lab at Virginia Tech (ECE Department), under the supervision of Dr. Mohammad Hossein Manshaei and Dr. Walid Saad, respectively. 
*For further information, feel free to contact us at: manshaei@cc.iut.ac.ir or walids@vt.edu.
***/
package com.gtmd.crcd.begin;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

import android.app.Activity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;



public class AES_encryption {
	private SecretKeySpec sks ;

	public AES_encryption(){
		sks = generate_secret_key_spec();
	}
	public String getKey_string(){
		return sks.toString();

	}
	public SecretKeySpec getKey(){
		return sks;
	}
	private SecretKeySpec generate_secret_key_spec(){
		SecretKeySpec sks = null ;
		try {
			SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
			sr.setSeed("any data used as random seed".getBytes());
			KeyGenerator kg = KeyGenerator.getInstance("AES");
			kg.init(128, sr);
			sks = new SecretKeySpec((kg.generateKey()).getEncoded(), "AES");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sks;
	}
	public   byte [] encrypt(byte[] plaintext){
		byte []encodedBytes = null;
		try {
			Cipher c = Cipher.getInstance("AES");
			c.init(Cipher.ENCRYPT_MODE, this.sks);
			encodedBytes = c.doFinal(plaintext);
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return encodedBytes;

	}
	public   byte [] decrypt(byte[] ciphertext){
		byte[] decodedBytes = null;
		try {
			Cipher c = Cipher.getInstance("AES");
			c.init(Cipher.DECRYPT_MODE, sks);
			decodedBytes = c.doFinal(ciphertext);
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return decodedBytes;
	}
	public void set_key(SecretKeySpec sk){
		sks = sk;
	}
}
