package com.hjt.utils;

import javax.crypto.Cipher;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Java RSA 加密工具类
 */
public class RSAUtils {
    /**
     * 密钥长度 于原文长度对应 以及越长速度越慢
     */
    private final static int KEY_SIZE = 1024;

    //   公私钥文件保存位置
    private final static String PAIR_KEY_DIR = "SSLS/src/main/resources/rsa/";
    private final static String PUBLIC_KEY_FILE_Path = "SSLS/src/main/resources/rsa/public_key.pem";
    private final static String PRIVATE_KEY_FILE_Path = "SSLS/src/main/resources/rsa/private_key.pem";

    /**
     * 公钥字符串
     */
    private static String publicKeyString;

    /**
     * 私钥字符串
     */
    private static String privateKeyString;

    // TODO 读取文件中的公钥和私钥，如果文件不存在则生成公钥和私钥并写入文件
    static {
        try {

            genKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    /**
     * 随机生成密钥对
     */
    public static void genKeyPair() throws NoSuchAlgorithmException {
        // KeyPairGenerator类用于生成公钥和私钥对，基于RSA算法生成对象
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        // 初始化密钥对生成器
        keyPairGen.initialize(KEY_SIZE, new SecureRandom());
        // 生成一个密钥对，保存在keyPair中
        KeyPair keyPair = keyPairGen.generateKeyPair();
        // 得到私钥
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        // 得到公钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        // 得到公钥字符串
        publicKeyString = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        // 得到私钥字符串
        privateKeyString = Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }

    /**
     * RSA公钥加密
     *
     * @param str       加密字符串
     * @param publicKey 公钥
     * @return 密文
     * @throws Exception 加密过程中的异常信息
     */
    public static String encrypt(String str, String publicKey) throws Exception {
        //base64编码的公钥
        byte[] decoded = Base64.getDecoder().decode(publicKey);
        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
        //RSA加密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        String outStr = Base64.getEncoder().encodeToString(cipher.doFinal(str.getBytes("UTF-8")));
        return outStr;
    }

    /**
     * RSA私钥解密
     *
     * @param str        加密字符串
     * @param privateKey 私钥
     * @return 明文
     * @throws Exception 解密过程中的异常信息
     */
    public static String decrypt(String str, String privateKey) throws Exception {
        //64位解码加密后的字符串
        byte[] inputByte = Base64.getDecoder().decode(str);
        //base64编码的私钥
        byte[] decoded = Base64.getDecoder().decode(privateKey);
        RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
        //RSA解密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, priKey);
        String outStr = new String(cipher.doFinal(inputByte));
        return outStr;
    }

    public static String getPublicKeyString() {
        return publicKeyString;
    }

    public static String getPrivateKeyString() {
        return privateKeyString;
    }


    private static void savePublicKey(PublicKey publicKey, String fileName) throws IOException {
        byte[] publicKeyBytes = publicKey.getEncoded();
        String publicKeyBase64 = Base64.getEncoder().encodeToString(publicKeyBytes);
        String publicKeyPEM = formatPEM(publicKeyBase64, "PUBLIC KEY");
        FileWriter fileWriter = new FileWriter(fileName);
        fileWriter.write(publicKeyPEM);
        fileWriter.close();
    }

    private static void savePrivateKey(PrivateKey privateKey, String fileName) throws IOException {
        byte[] privateKeyBytes = privateKey.getEncoded();
        String privateKeyBase64 = Base64.getEncoder().encodeToString(privateKeyBytes);
        String privateKeyPEM = formatPEM(privateKeyBase64, "PRIVATE KEY");
        FileWriter fileWriter = new FileWriter(fileName);
        fileWriter.write(privateKeyPEM);
        fileWriter.close();
    }

    private static String formatPEM(String base64String, String type) {
        StringBuilder pem = new StringBuilder();
        pem.append("-----BEGIN ").append(type).append("-----\n");
        pem.append(base64String).append("\n");
        pem.append("-----END ").append(type).append("-----");
        return pem.toString();
    }

    private static PublicKey readPublicKey(String fileName) throws Exception {
        Path path = Paths.get(fileName);
        byte[] publicKeyBytes = Files.readAllBytes(path);
        String publicKeyPEM = new String(publicKeyBytes);
        publicKeyPEM = publicKeyPEM.replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
        byte[] publicKeyData = Base64.getDecoder().decode(publicKeyPEM);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKeyData);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(spec);
    }

    private static PrivateKey readPrivateKey(String fileName) throws Exception {
        Path path = Paths.get(fileName);
        byte[] privateKeyBytes = Files.readAllBytes(path);
        String privateKeyPEM = new String(privateKeyBytes);
        privateKeyPEM = privateKeyPEM.replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        byte[] privateKeyData = Base64.getDecoder().decode(privateKeyPEM);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(privateKeyData);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(spec);
    }

    public static void main(String[] args) {
//        try {
//
//
//            Files.createDirectories(Paths.get(PAIR_KEY_DIR));
//            // 保存公钥和私钥到.pem文件
//            savePublicKey(, PUBLIC_KEY_FILE_Path);
//            savePrivateKey(, PRIVATE_KEY_FILE_Path);
//            System.out.println("RSA公私钥已生成并保存到.pem文件");
//        } catch (NoSuchAlgorithmException | IOException e) {
//            e.printStackTrace();
//        }
    }
}