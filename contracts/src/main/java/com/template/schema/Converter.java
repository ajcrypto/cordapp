package com.template.schema;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import javax.persistence.AttributeConverter;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class Converter implements AttributeConverter<String, String> {

    public byte[] encrypt(String plainText) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        SecureRandom secureRandom = new SecureRandom();
        int keyBitSize = 256;
        keyGenerator.init(keyBitSize, secureRandom);
        SecretKey keyBytes = keyGenerator.generateKey();

        byte[] bytes = plainText.getBytes();
        cipher.init(Cipher.ENCRYPT_MODE, keyBytes);
        return cipher.doFinal(bytes);
    }



    @Override
    public String convertToDatabaseColumn(String attribute) {
        /* perform encryption here */
        String bytes = null;
        try {

            bytes =  Base64.getEncoder().encodeToString(encrypt(attribute));
//             bytes = encrypt(attribute).toString();

        }catch (Exception e){
            e.printStackTrace();
        }

            return bytes;

    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        /* perform decryption here */

        String message = null;

        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            SecureRandom secureRandom = new SecureRandom();
            int keyBitSize = 256;
            keyGenerator.init(keyBitSize, secureRandom);
            SecretKey secretKey = keyGenerator.generateKey    ();

            byte[] bytes = dbData.getBytes();
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            message = cipher.doFinal(bytes).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return message;
    }
}