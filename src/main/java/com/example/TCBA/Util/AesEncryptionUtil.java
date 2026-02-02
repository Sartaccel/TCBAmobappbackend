package com.example.TCBA.Util;

import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
public class AesEncryptionUtil {

    private static final String ALGO = "AES";
    private static final String SECRET_KEY = "1234567890123456"; // 16 chars

    public String encrypt(String data) {
        if (data == null) return null;
        try {
            Cipher cipher = Cipher.getInstance(ALGO);
            SecretKeySpec key = new SecretKeySpec(SECRET_KEY.getBytes(), ALGO);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return Base64.getEncoder()
                    .encodeToString(cipher.doFinal(data.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed");
        }
    }

    public String decrypt(String encryptedData) {
        if (encryptedData == null) return null;
        try {
            Cipher cipher = Cipher.getInstance(ALGO);
            SecretKeySpec key = new SecretKeySpec(SECRET_KEY.getBytes(), ALGO);
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(cipher.doFinal(
                    Base64.getDecoder().decode(encryptedData)));
        } catch (Exception e) {
            throw new RuntimeException("Decryption failed");
        }
    }
}
