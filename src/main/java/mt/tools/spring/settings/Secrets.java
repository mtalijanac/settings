package mt.tools.spring.settings;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import lombok.Setter;

/*
 * https://crypto.stackexchange.com/a/41438
 */
class Secrets {

    @Setter String cipherAlgorithm = "AES/CTR/NoPadding";
    @Setter String randomAlgorithm = "SHA1PRNG";

    @Setter String password;
    @Setter String salt;

    SecretKey key = null;
    IvParameterSpec iv = null;

    public String encrypt(String input) {
        try {
            Cipher cipher = Cipher.getInstance(cipherAlgorithm);
            cipher.init(Cipher.ENCRYPT_MODE, key(), iv());
            byte[] cipherText = cipher.doFinal(input.getBytes(StandardCharsets.UTF_8));
            String base64Text = Base64.getEncoder().encodeToString(cipherText);
            return base64Text;
        }
        catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    public String decrypt(String cipherText) {
        try {
            Cipher cipher = Cipher.getInstance(cipherAlgorithm);
            cipher.init(Cipher.DECRYPT_MODE, key(), iv());
            byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(cipherText));
            return new String(plainText);
        }
        catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    SecretKey key() {
        if (key != null) {
            return key;
        }

        try {
            byte[] salted = spiceUp("Key", salt, password);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salted, 65536, 256);
            SecretKey secret = factory.generateSecret(spec);
            key = new SecretKeySpec(secret.getEncoded(), "AES");
            return key;
        }
        catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    IvParameterSpec iv() {
        if (iv != null) {
            return iv;
        }

        try {
            byte[] salted = spiceUp("IV", salt, password);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salted, 65536, 128);
            SecretKey secret = factory.generateSecret(spec);
            byte[] ivBytes = secret.getEncoded();
            iv = new IvParameterSpec(ivBytes);
            return iv;
        }
        catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Either use salt provided by user or try to generate one from password
     * As we are not storing salt value, can't be randomized. Only obfuscated.
     **/
    byte[] spiceUp(String usage, String salt, String password) {
        if (salt != null) {
            String base = usage + salt;
            return base.getBytes(StandardCharsets.UTF_8);
        }

        String seedword = usage + password;
        byte[] passbytes = seedword.getBytes(StandardCharsets.UTF_8);
        SecureRandom rng;
        try {
            rng = SecureRandom.getInstance(randomAlgorithm);
        }
        catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("Unknown SecureRandom algorithm: '" + randomAlgorithm + "'.", e);
        }
        rng.setSeed(passbytes);

        byte[] saltBytes = new byte[64];
        rng.nextBytes(saltBytes);
        return saltBytes;
    }

}
