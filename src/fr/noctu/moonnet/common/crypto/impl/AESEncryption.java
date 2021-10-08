package fr.noctu.moonnet.common.crypto.impl;

import fr.noctu.moonnet.common.crypto.Encryption;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

public class AESEncryption extends Encryption {
    private SecretKeySpec secretKey;
    private byte[] key;

    public AESEncryption(String kKey){
        MessageDigest sha = null;
        try {
            key = kKey.getBytes(StandardCharsets.UTF_8);
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            secretKey = new SecretKeySpec(key, "AES");
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @Override
    public byte[] encrypt(byte[] original) {
        try{
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return cipher.doFinal(original);
        } catch (Exception e){
            e.printStackTrace();
        }
        return original;
    }

    @Override
    public byte[] decrypt(byte[] encrypted) {
        try{
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return cipher.doFinal(encrypted);
        } catch (Exception e){
            e.printStackTrace();
        }
        return encrypted;
    }
}
