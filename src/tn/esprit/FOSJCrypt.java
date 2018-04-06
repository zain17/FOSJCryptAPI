/*
 * Copyright (C) 2017 Dominik Schadow, dominikschadow@gmail.com
 *
 * This file is part of the Java Security project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tn.esprit;


import com.google.common.io.BaseEncoding;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import com.google.common.primitives.Bytes;


import java.security.SecureRandom;
import java.util.Base64;


/**
 * FOSJCrypt fait le hachage et le cryptage comme le FOSUser(default encoder:Sha512) via application desktop Java.
 * La génération de salt et comme de FOSUser
 * La génération de hash et celle que FOSUser 5000 itération sur digester (une avant le boucle pour fusionner le salt avec le password) and ajout les accolades
 * Conversion
 * Google Guava utiliser pour convertir le hash en base64 (password column FOSUser contient hash convertit en base64 par cette API
 * Cette API utilise le même Algorithm par défaut utiliser par FOSUser(voir security.yml encoders{algorithm})
 * @author Zain ELabidine membre de " The Optimists"
 * @Email: zainelabidine.bensaleh@esprit.tn
 *Api à télécharger:
 * 1) guava-19.0.jar
 * 2) commons-codec-1.7.jar
 *
 */
public class FOSJCrypt {
    private static final String ALGORITHM = "SHA-512";
    private static final int ITERATIONS = 5000;
    private static final int SALT_SIZE = 32;


    /**
     * Private constructor.
     */
    private FOSJCrypt() {
    }
    public static Sha512 crypt(String clairPass) {
        String gs=generateSalt();
        try {
            byte[] hash = encodePassword(clairPass, gs);
            return passwordToPersist(hash, clairPass, gs);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }
    private static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_SIZE];
        random.nextBytes(salt);
        String saltBase64_encoded=BaseEncoding.base64().encode(salt);
        String saltPlusDot= saltBase64_encoded.replace("+",".");
        return saltPlusDot.substring(0,saltPlusDot.length()-1);
    }
    private static byte[] encodePassword(String password,String salt) throws NoSuchAlgorithmException,
            UnsupportedEncodingException {
        String mergedPasswordAndSalt =mergePasswordAndSalt(password, salt);
        MessageDigest digester = MessageDigest.getInstance(ALGORITHM);
        byte[] hash = digester.digest(mergedPasswordAndSalt .getBytes("UTF-8"));
        for (int i = 1; i < ITERATIONS; ++i) {
            hash = digester.digest(Bytes.concat(hash, mergedPasswordAndSalt.getBytes("UTF-8")));
        }
        return hash;
    }
    private static String mergePasswordAndSalt(String pass, String salt) {
        if (salt == null) {
            return salt;
        }
        String cg="{";String cd="}";
        return pass+cg+salt+cd;
    }
    /*
     *  paramétre: originalHash el hash ala mel base de donnés mais en byte donc lazem el hash ala on String tetbadel bytes
     */
    public static   boolean verifyPassword(byte[] originalHash, String passwordClair, String salt) throws
            NoSuchAlgorithmException, UnsupportedEncodingException {
        byte[] comparisonHash = encodePassword(passwordClair, salt);
        return comparePasswords(originalHash, comparisonHash);
    }
    private static Sha512 passwordToPersist(byte[] originalHash, String password, String salt) throws
            NoSuchAlgorithmException, UnsupportedEncodingException {
        Sha512 sh=new Sha512();
//        byte[] comparisonHash = encodePassword(password, salt);
        sh.setSalt(salt);sh.setHash(BaseEncoding.base64().encode(originalHash));
        return sh;
    }
    private static boolean comparePasswords(byte[] originalHash, byte[] comparisonHash) {
        int diff = originalHash.length ^ comparisonHash.length;
        for (int i = 0; i < originalHash.length && i < comparisonHash.length; i++) {
            diff |= originalHash[i] ^ comparisonHash[i];
        }

        return diff == 0;
    }

    public static boolean checkPassword(String originalHashStr, String passwordClair,String salt) throws
            NoSuchAlgorithmException, UnsupportedEncodingException {
        byte[] comparisonHash = encodePassword(passwordClair, salt);
        System.out.println();
        byte[] originalHash = Base64.getDecoder().decode(originalHashStr.getBytes("UTF-8"));
        return comparePasswords(originalHash, comparisonHash);
    }
}
